package us.dot.its.jpo.conflictmonitor.monitor.topologies.assessments;

import static us.dot.its.jpo.conflictmonitor.monitor.algorithms.connection_of_travel_assessment.ConnectionOfTravelAssessmentConstants.*;

import java.util.ArrayList;
import java.util.List;



import org.apache.kafka.common.serialization.Serdes;
import org.apache.kafka.common.utils.Bytes;
import org.apache.kafka.streams.KeyValue;
import org.apache.kafka.streams.StreamsBuilder;
import org.apache.kafka.streams.Topology;
import org.apache.kafka.streams.kstream.Aggregator;
import org.apache.kafka.streams.kstream.Consumed;
import org.apache.kafka.streams.kstream.Grouped;
import org.apache.kafka.streams.kstream.Initializer;
import org.apache.kafka.streams.kstream.KStream;
import org.apache.kafka.streams.kstream.KTable;
import org.apache.kafka.streams.kstream.Materialized;
import org.apache.kafka.streams.kstream.Printed;
import org.apache.kafka.streams.kstream.Produced;
import org.apache.kafka.streams.state.KeyValueStore;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import us.dot.its.jpo.conflictmonitor.monitor.algorithms.BaseStreamsTopology;
import us.dot.its.jpo.conflictmonitor.monitor.algorithms.connection_of_travel_assessment.ConnectionOfTravelAssessmentParameters;
import us.dot.its.jpo.conflictmonitor.monitor.algorithms.connection_of_travel_assessment.ConnectionOfTravelAssessmentStreamsAlgorithm;
import us.dot.its.jpo.conflictmonitor.monitor.models.EventAssessment;
import us.dot.its.jpo.conflictmonitor.monitor.models.assessments.ConnectionOfTravelAggregator;
import us.dot.its.jpo.conflictmonitor.monitor.models.assessments.ConnectionOfTravelAssessment;
import us.dot.its.jpo.conflictmonitor.monitor.models.assessments.ConnectionOfTravelAssessmentGroup;
import us.dot.its.jpo.conflictmonitor.monitor.models.events.ConnectionOfTravelEvent;
import us.dot.its.jpo.conflictmonitor.monitor.models.events.TimestampExtractors.ConnectionOfTravelTimestampExtractor;
import us.dot.its.jpo.conflictmonitor.monitor.models.notifications.ConnectionOfTravelNotification;
import us.dot.its.jpo.conflictmonitor.monitor.serialization.JsonSerdes;


@Component(DEFAULT_CONNECTION_OF_TRAVEL_ASSESSMENT_ALGORITHM)
public class ConnectionOfTravelAssessmentTopology
    extends BaseStreamsTopology<ConnectionOfTravelAssessmentParameters>
    implements ConnectionOfTravelAssessmentStreamsAlgorithm {

    private static final Logger logger = LoggerFactory.getLogger(ConnectionOfTravelAssessmentTopology.class);

    @Override
    protected Logger getLogger() {
        return logger;
    }



    public Topology buildTopology() {
        var builder = new StreamsBuilder();

        // GeoJson Input Spat Stream
        KStream<String, ConnectionOfTravelEvent> connectionOfTravelEvents = 
            builder.stream(
                parameters.getConnectionOfTravelEventTopicName(), 
                Consumed.with(
                    Serdes.String(), 
                    JsonSerdes.ConnectionOfTravelEvent())
                    .withTimestampExtractor(new ConnectionOfTravelTimestampExtractor())
                );

        if(parameters.isDebug()){
            connectionOfTravelEvents.print(Printed.toSysOut());
        }

        Initializer<ConnectionOfTravelAggregator> connectionOfTravelAssessmentInitializer = ()->{
            ConnectionOfTravelAggregator agg = new ConnectionOfTravelAggregator();
            return agg;
        };


        Aggregator<String, ConnectionOfTravelEvent, ConnectionOfTravelAggregator> connectionOfTravelEventAggregator = 
            (key, value, aggregate) -> aggregate.add(value);


        KTable<String, ConnectionOfTravelAggregator> connectionOfTravelAssessments = 
            connectionOfTravelEvents.groupByKey(Grouped.with(Serdes.String(), JsonSerdes.ConnectionOfTravelEvent()))
            .aggregate(
                connectionOfTravelAssessmentInitializer,
                connectionOfTravelEventAggregator,
                Materialized.<String, ConnectionOfTravelAggregator, KeyValueStore<Bytes, byte[]>>as("connectionOfTravelAssessments")
                    .withKeySerde(Serdes.String())
                    .withValueSerde(JsonSerdes.ConnectionOfTravelAggregator())
            );

        // Map the Windowed K Stream back to a Key Value Pair containing the assessment and the generating event.
        KStream<String, EventAssessment> connectionOfTravelEventAssessmentStream = connectionOfTravelAssessments.toStream()
            .map((key, value) -> {
                // StopLineStopAssessment assessment = value.getStopLineStopAssessment();
                EventAssessment eventAssessment = value.getEventAssessmentPair(parameters.getLookBackPeriodDays());
                eventAssessment.getAssessment().setSource(key);
                return KeyValue.pair(key, eventAssessment);
            }
        );

        // Split Apart the Assessment from the event to put back on a topic.
        KStream<String, ConnectionOfTravelAssessment> connectionOfTravelAssessmentStream = connectionOfTravelEventAssessmentStream
            .map((key, value) -> {
                ConnectionOfTravelAssessment assessment = (ConnectionOfTravelAssessment)value.getAssessment();
                return KeyValue.pair(key, assessment);
            }
        );

        

        connectionOfTravelAssessmentStream.to(
            parameters.getConnectionOfTravelAssessmentOutputTopicName(), 
            Produced.with(Serdes.String(),
                    JsonSerdes.ConnectionOfTravelAssessment()));

        connectionOfTravelAssessmentStream.print(Printed.toSysOut());


        KStream<String, ConnectionOfTravelNotification> notificationEventStream = connectionOfTravelEventAssessmentStream.flatMap(
            (key, value)->{
                ConnectionOfTravelAssessment assessment = (ConnectionOfTravelAssessment)value.getAssessment();
                ConnectionOfTravelEvent event = (ConnectionOfTravelEvent)value.getEvent();

                List<KeyValue<String, ConnectionOfTravelNotification>> result = new ArrayList<KeyValue<String, ConnectionOfTravelNotification>>();
                for(ConnectionOfTravelAssessmentGroup assessmentGroup: assessment.getConnectionOfTravelAssessmentGroups()){
                    if(assessmentGroup.getIngressLaneID() == event.getIngressLaneID() && assessmentGroup.getEgressLaneID() == event.getEgressLaneID()){
                        if(assessmentGroup.getEventCount() >= parameters.getMinimumNumberOfEvents() && assessmentGroup.getConnectionID() < 0){
                            ConnectionOfTravelNotification notification = new ConnectionOfTravelNotification();
                            notification.setIngressLane(assessmentGroup.getIngressLaneID());
                            notification.setEgressLane(assessmentGroup.getEgressLaneID());
                            notification.setNotificationText("Connection of Travel Notification, Unknown Lane connection between ingress lane: " + assessmentGroup.getIngressLaneID() + " and egress lane: " + assessmentGroup.getEgressLaneID()+".");
                            notification.setNotificationHeading("Connection of Travel Notification");
                            notification.setAssessment(assessment);
                            
                            result.add(new KeyValue<>(key, notification));
                        }
                    }
                    
                }
                
                return result;
            }
        );
                
        KTable<String, ConnectionOfTravelNotification> connectionNotificationTable = 
            notificationEventStream.groupByKey(Grouped.with(Serdes.String(), JsonSerdes.ConnectionOfTravelNotification()))
            .reduce(
                (oldValue, newValue)->{
                        return newValue;
                },
            Materialized.<String, ConnectionOfTravelNotification, KeyValueStore<Bytes, byte[]>>as("ConnectionOfTravelNotification")
            .withKeySerde(Serdes.String())
            .withValueSerde(JsonSerdes.ConnectionOfTravelNotification())
        );
    
        connectionNotificationTable.toStream().to(
            parameters.getConnectionOfTravelNotificationTopicName(),
            Produced.with(Serdes.String(),
                    JsonSerdes.ConnectionOfTravelNotification()));

                    
        return builder.build();
    }    


    
}

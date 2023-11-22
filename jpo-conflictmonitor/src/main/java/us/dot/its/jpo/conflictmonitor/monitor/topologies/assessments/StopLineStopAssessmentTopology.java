package us.dot.its.jpo.conflictmonitor.monitor.topologies.assessments;

import org.apache.kafka.common.serialization.Serdes;
import org.apache.kafka.common.utils.Bytes;
import org.apache.kafka.streams.KeyValue;
import org.apache.kafka.streams.StreamsBuilder;
import org.apache.kafka.streams.Topology;
import org.apache.kafka.streams.kstream.*;
import org.apache.kafka.streams.state.KeyValueStore;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import us.dot.its.jpo.conflictmonitor.monitor.algorithms.BaseStreamsTopology;
import us.dot.its.jpo.conflictmonitor.monitor.algorithms.stop_line_stop_assessment.StopLineStopAssessmentParameters;
import us.dot.its.jpo.conflictmonitor.monitor.algorithms.stop_line_stop_assessment.StopLineStopAssessmentStreamsAlgorithm;
import us.dot.its.jpo.conflictmonitor.monitor.models.EventAssessment;
import us.dot.its.jpo.conflictmonitor.monitor.models.assessments.StopLineStopAggregator;
import us.dot.its.jpo.conflictmonitor.monitor.models.assessments.StopLineStopAssessment;
import us.dot.its.jpo.conflictmonitor.monitor.models.assessments.StopLineStopAssessmentGroup;
import us.dot.its.jpo.conflictmonitor.monitor.models.events.StopLineStopEvent;
import us.dot.its.jpo.conflictmonitor.monitor.models.events.TimestampExtractors.StopLineStopTimestampExtractor;
import us.dot.its.jpo.conflictmonitor.monitor.models.notifications.StopLineStopNotification;
import us.dot.its.jpo.conflictmonitor.monitor.serialization.JsonSerdes;

import static us.dot.its.jpo.conflictmonitor.monitor.algorithms.stop_line_stop_assessment.StopLineStopAssessmentConstants.DEFAULT_STOP_LINE_STOP_ASSESSMENT_ALGORITHM;

import java.util.ArrayList;
import java.util.List;;


@Component(DEFAULT_STOP_LINE_STOP_ASSESSMENT_ALGORITHM)
public class StopLineStopAssessmentTopology
    extends BaseStreamsTopology<StopLineStopAssessmentParameters>
    implements StopLineStopAssessmentStreamsAlgorithm {

    private static final Logger logger = LoggerFactory.getLogger(StopLineStopAssessmentTopology.class);



    @Override
    protected Logger getLogger() {
        return logger;
    }




    public Topology buildTopology() {
        var builder = new StreamsBuilder();

        // GeoJson Input Spat Stream
        KStream<String, StopLineStopEvent> stopLineStopEvents =
            builder.stream(
                parameters.getStopLineStopEventTopicName(), 
                Consumed.with(
                    Serdes.String(), 
                    JsonSerdes.StopLineStopEvent())
                    .withTimestampExtractor(new StopLineStopTimestampExtractor())
                );

        Initializer<StopLineStopAggregator> stopLineStopAssessmentInitializer = ()->{
            StopLineStopAggregator agg = new StopLineStopAggregator();
            // agg.setMessageDurationDays(parameters.getLookBackPeriodDays());
            return agg;
        };

        stopLineStopEvents.print(Printed.toSysOut());

        Aggregator<String, StopLineStopEvent, StopLineStopAggregator> stopLineStopEventAggregator =
            (key, value, aggregate)-> {
                return aggregate.add(value);
            };


        KTable<String, StopLineStopAggregator> stopLineStopAssessments = 
            stopLineStopEvents.groupByKey(Grouped.with(Serdes.String(), JsonSerdes.StopLineStopEvent()))
            .aggregate(
                stopLineStopAssessmentInitializer,
                stopLineStopEventAggregator,
                Materialized.<String, StopLineStopAggregator, KeyValueStore<Bytes, byte[]>>as("stopLineStopEventAssessments")
                    .withKeySerde(Serdes.String())
                    .withValueSerde(JsonSerdes.StopLineStopAggregator())
            );


        

        // Map the Windowed K Stream back to a Key Value Pair containing the assessment and the generating event.
        KStream<String, EventAssessment> stopLineStopEventAssessmentStream = stopLineStopAssessments.toStream()
            .map((key, value) -> {
                // StopLineStopAssessment assessment = value.getStopLineStopAssessment();
                EventAssessment eventAssessment = value.getEventAssessmentPair(parameters.getLookBackPeriodDays());
                eventAssessment.getAssessment().setSource(key);
                return KeyValue.pair(key, eventAssessment);
            }
        );

        // Split Apart the Assessment from the event to put back on a topic.
        KStream<String, StopLineStopAssessment> stopLineStopAssessmentStream = stopLineStopEventAssessmentStream
            .map((key, value) -> {
                StopLineStopAssessment assessment = (StopLineStopAssessment)value.getAssessment();
                return KeyValue.pair(key, assessment);
            }
        );

        

        stopLineStopAssessmentStream.to(
            parameters.getStopLineStopAssessmentOutputTopicName(), 
            Produced.with(Serdes.String(),
                    JsonSerdes.StopLineStopAssessment()));

        stopLineStopAssessmentStream.print(Printed.toSysOut());


        KStream<String, StopLineStopNotification> notificationEventStream = stopLineStopEventAssessmentStream.flatMap(
            (key, value)->{


                List<KeyValue<String, StopLineStopNotification>> result = new ArrayList<KeyValue<String, StopLineStopNotification>>();
                StopLineStopAssessment assessment = (StopLineStopAssessment) value.getAssessment();
                StopLineStopEvent event = (StopLineStopEvent) value.getEvent();


                for(StopLineStopAssessmentGroup group: assessment.getStopLineStopAssessmentGroup()){
                    // Only Send Assessments that match the generating signal group.
                    if(group.getSignalGroup() == event.getSignalGroup() && group.getNumberOfEvents() >= parameters.getMinimumEventsToNotify()){
                        double totalTime = group.getTimeStoppedOnDark() + group.getTimeStoppedOnGreen() + group.getTimeStoppedOnRed() + group.getTimeStoppedOnYellow();
                        if(group.getTimeStoppedOnGreen() > parameters.getGreenLightPercentToNotify() * totalTime){
                            StopLineStopNotification notification = new StopLineStopNotification();
                            notification.setSignalGroup(group.getSignalGroup());
                            notification.setNotificationText("Stop Line Stop Notification, Percent Time stopped on green: " + group.getTimeStoppedOnGreen() + " For Signal group: " + group.getSignalGroup() + " Exceeds Maximum Allowable Percent");
                            notification.setNotificationHeading("Stop Line Stop Notification");
                            notification.setAssessment(assessment);
                            
                            result.add(new KeyValue<>(key, notification));
                        }
                    }
                }

                return result;
            }
        );

        notificationEventStream.print(Printed.toSysOut());
                
        KTable<String, StopLineStopNotification> connectionNotificationTable = 
            notificationEventStream.groupByKey(Grouped.with(Serdes.String(), JsonSerdes.StopLineStopNotification()))
            .reduce(
                (oldValue, newValue)->{
                        return newValue;
                },
            Materialized.<String, StopLineStopNotification, KeyValueStore<Bytes, byte[]>>as("StopLineStopNotification")
            .withKeySerde(Serdes.String())
            .withValueSerde(JsonSerdes.StopLineStopNotification())
        );
    
        connectionNotificationTable.toStream().to(
            parameters.getStopLineStopNotificationOutputTopicName(),
            Produced.with(Serdes.String(),
                    JsonSerdes.StopLineStopNotification()));

        return builder.build();
    }    


}

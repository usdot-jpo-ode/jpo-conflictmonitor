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
import us.dot.its.jpo.conflictmonitor.monitor.algorithms.stop_line_passage_assessment.StopLinePassageAssessmentParameters;
import us.dot.its.jpo.conflictmonitor.monitor.algorithms.stop_line_passage_assessment.StopLinePassageAssessmentStreamsAlgorithm;
import us.dot.its.jpo.conflictmonitor.monitor.models.EventAssessment;
import us.dot.its.jpo.conflictmonitor.monitor.models.assessments.StopLinePassageAggregator;
import us.dot.its.jpo.conflictmonitor.monitor.models.assessments.StopLinePassageAssessment;
import us.dot.its.jpo.conflictmonitor.monitor.models.assessments.StopLinePassageAssessmentGroup;
import us.dot.its.jpo.conflictmonitor.monitor.models.events.StopLinePassageEvent;
import us.dot.its.jpo.conflictmonitor.monitor.models.events.TimestampExtractors.SignalStateTimestampExtractor;
import us.dot.its.jpo.conflictmonitor.monitor.models.notifications.StopLinePassageNotification;
import us.dot.its.jpo.conflictmonitor.monitor.serialization.JsonSerdes;

import static us.dot.its.jpo.conflictmonitor.monitor.algorithms.stop_line_passage_assessment.StopLinePassageAssessmentConstants.DEFAULT_STOP_LINE_PASSAGE_ASSESSMENT_ALGORITHM;

import java.util.ArrayList;
import java.util.List;


@Component(DEFAULT_STOP_LINE_PASSAGE_ASSESSMENT_ALGORITHM)
public class StopLinePassageAssessmentTopology
    extends BaseStreamsTopology<StopLinePassageAssessmentParameters>
    implements StopLinePassageAssessmentStreamsAlgorithm {

    private static final Logger logger = LoggerFactory.getLogger(StopLinePassageAssessmentTopology.class);



    @Override
    protected Logger getLogger() {
        return logger;
    }




    public Topology buildTopology() {
        var builder = new StreamsBuilder();

        // GeoJson Input Spat Stream
        KStream<String, StopLinePassageEvent> signalStateEvents =
            builder.stream(
                parameters.getStopLinePassageEventTopicName(), 
                Consumed.with(
                    Serdes.String(), 
                    JsonSerdes.StopLinePassageEvent())
                    .withTimestampExtractor(new SignalStateTimestampExtractor())
                );

        Initializer<StopLinePassageAggregator> signalStateAssessmentInitializer = ()->{
            StopLinePassageAggregator agg = new StopLinePassageAggregator();
            // agg.setMessageDurationDays(parameters.getLookBackPeriodDays());

            // logger.info("Setting up Signal State Event Assessment Topology \n\n\n\n");
            return agg;
        };

        signalStateEvents.print(Printed.toSysOut());

        Aggregator<String, StopLinePassageEvent, StopLinePassageAggregator> signalStateEventAggregator =
            (key, value, aggregate)-> {
                return aggregate.add(value);
            };


        KTable<String, StopLinePassageAggregator> signalStateAssessments = 
            signalStateEvents.groupByKey(Grouped.with(Serdes.String(), JsonSerdes.StopLinePassageEvent()))
            .aggregate(
                signalStateAssessmentInitializer,
                signalStateEventAggregator,
                Materialized.<String, StopLinePassageAggregator, KeyValueStore<Bytes, byte[]>>as("stopLinePassageAssessments")
                    .withKeySerde(Serdes.String())
                    .withValueSerde(JsonSerdes.SignalStateEventAggregator())
            );

        // Map the Windowed K Stream back to a Key Value Pair containing the assessment and the generating event.
        KStream<String, EventAssessment> stopLinePassageEventAssessmentStream = signalStateAssessments.toStream()
            .map((key, value) -> {
                EventAssessment eventAssessment = value.getEventAssessmentPair(parameters.getLookBackPeriodDays());
                eventAssessment.getAssessment().setSource(key);
                return KeyValue.pair(key, eventAssessment);
            }
        );


        // Split Apart the Assessment from the event to put back on a topic.
        KStream<String, StopLinePassageAssessment> stopLinePassageAssessmentStream = stopLinePassageEventAssessmentStream
            .map((key, value) -> {
                StopLinePassageAssessment assessment = (StopLinePassageAssessment)value.getAssessment();
                return KeyValue.pair(key, assessment);
            }
        );


        

        // // Map the Windowed K Stream back to a Key Value Pair
        // KStream<String, StopLinePassageAssessment> stopLineStopEventAssessmentStream = signalStateAssessments.toStream()
        //     .map((key, value) -> {
        //         StopLinePassageAssessment assessment = value.getEventAssessmentPair(parameters.getLookBackPeriodDays());
        //         assessment.setSource(key);
        //         return KeyValue.pair(key, assessment);
        //     }
        // );

        stopLinePassageAssessmentStream.to(
            parameters.getStopLinePassageAssessmentOutputTopicName(), 
            Produced.with(Serdes.String(),
                    JsonSerdes.StopLinePassageAssessment()));

        stopLinePassageAssessmentStream.print(Printed.toSysOut());


         KStream<String, StopLinePassageNotification> notificationEventStream = stopLinePassageEventAssessmentStream.flatMap(
            (key, value)->{


                List<KeyValue<String, StopLinePassageNotification>> result = new ArrayList<KeyValue<String, StopLinePassageNotification>>();
                StopLinePassageAssessment assessment = (StopLinePassageAssessment) value.getAssessment();
                StopLinePassageEvent event = (StopLinePassageEvent) value.getEvent();


                for(StopLinePassageAssessmentGroup group: assessment.getSignalStateEventAssessmentGroup()){
                    // Only Send Assessments that match the generating signal group.
                    int eventCount = group.getDarkEvents() + group.getGreenEvents() + group.getYellowEvents() +  group.getRedEvents();
                    if(group.getSignalGroup() == event.getSignalGroup() && eventCount >= parameters.getMinimumEventsToNotify()){;
                        if((group.getRedEvents() / eventCount) >= parameters.getRedLightPercentToNotify()){
                            StopLinePassageNotification notification = new StopLinePassageNotification();
                            notification.setSignalGroup(group.getSignalGroup());
                            notification.setNotificationText("Stop Line Passage Notification, percent of passage events on red: " + Math.round(100 * group.getRedEvents() / eventCount) + "% for signal group: " + group.getSignalGroup() + " exceeds maximum allowable percent.");
                            notification.setNotificationHeading("Stop Line Passage Notification");
                            notification.setIntersectionID(assessment.getIntersectionID());
                            notification.setRoadRegulatorID(assessment.getRoadRegulatorID());
                            notification.setAssessment(assessment);
                            
                            result.add(new KeyValue<>(key, notification));
                            

                        }else if((group.getYellowEvents() + group.getRedEvents()) / eventCount >= parameters.getYellowLightPercentToNotify()){
                            StopLinePassageNotification notification = new StopLinePassageNotification();
                            notification.setSignalGroup(group.getSignalGroup());
                            notification.setNotificationText("Stop Line Passage Notification, percent of passage events on red and yellow: " + Math.round(100 * (group.getRedEvents() + group.getYellowEvents()) / eventCount) + "% for signal group: " + group.getSignalGroup() + " exceeds Maximum Allowable Percent.");
                            notification.setNotificationHeading("Stop Line Passage Notification");
                            notification.setIntersectionID(assessment.getIntersectionID());
                            notification.setRoadRegulatorID(assessment.getRoadRegulatorID());
                            notification.setAssessment(assessment);

                            result.add(new KeyValue<>(key, notification));
                            
                        }
                    }


                }

                return result;
            }
        );

        notificationEventStream.print(Printed.toSysOut());
                
        KTable<String, StopLinePassageNotification> stopLinePassageNotificationTable = 
            notificationEventStream.groupByKey(Grouped.with(Serdes.String(), JsonSerdes.StopLinePassageNotification()))
            .reduce(
                (oldValue, newValue)->{
                        return newValue;
                },
            Materialized.<String, StopLinePassageNotification, KeyValueStore<Bytes, byte[]>>as("StopLinePassageNotification")
            .withKeySerde(Serdes.String())
            .withValueSerde(JsonSerdes.StopLinePassageNotification())
        );
    
        stopLinePassageNotificationTable.toStream().to(
            parameters.getStopLinePassageNotificationOutputTopicName(),
            Produced.with(Serdes.String(),
                    JsonSerdes.StopLinePassageNotification()));

        return builder.build();
    }    


}

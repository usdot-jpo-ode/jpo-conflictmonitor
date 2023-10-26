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
import us.dot.its.jpo.conflictmonitor.monitor.algorithms.signal_state_event_assessment.SignalStateEventAssessmentParameters;
import us.dot.its.jpo.conflictmonitor.monitor.algorithms.signal_state_event_assessment.SignalStateEventAssessmentStreamsAlgorithm;
import us.dot.its.jpo.conflictmonitor.monitor.models.assessments.StopLinePassageAggregator;
import us.dot.its.jpo.conflictmonitor.monitor.models.assessments.StopLinePassageAssessment;
import us.dot.its.jpo.conflictmonitor.monitor.models.events.StopLinePassageEvent;
import us.dot.its.jpo.conflictmonitor.monitor.models.events.TimestampExtractors.SignalStateTimestampExtractor;
import us.dot.its.jpo.conflictmonitor.monitor.serialization.JsonSerdes;

import static us.dot.its.jpo.conflictmonitor.monitor.algorithms.signal_state_event_assessment.SignalStateEventAssessmentConstants.DEFAULT_SIGNAL_STATE_EVENT_ASSESSMENT_ALGORITHM;


@Component(DEFAULT_SIGNAL_STATE_EVENT_ASSESSMENT_ALGORITHM)
public class SignalStateEventAssessmentTopology
    extends BaseStreamsTopology<SignalStateEventAssessmentParameters>
    implements SignalStateEventAssessmentStreamsAlgorithm {

    private static final Logger logger = LoggerFactory.getLogger(SignalStateEventAssessmentTopology.class);



    @Override
    protected Logger getLogger() {
        return logger;
    }




    public Topology buildTopology() {
        var builder = new StreamsBuilder();

        // GeoJson Input Spat Stream
        KStream<String, StopLinePassageEvent> signalStateEvents =
            builder.stream(
                parameters.getSignalStateEventTopicName(), 
                Consumed.with(
                    Serdes.String(), 
                    JsonSerdes.StopLinePassageEvent())
                    .withTimestampExtractor(new SignalStateTimestampExtractor())
                );

        Initializer<StopLinePassageAggregator> signalStateAssessmentInitializer = ()->{
            StopLinePassageAggregator agg = new StopLinePassageAggregator();
            agg.setMessageDurationDays(parameters.getLookBackPeriodDays());

            logger.info("Setting up Signal State Event Assessment Topology \n\n\n\n");
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
                Materialized.<String, StopLinePassageAggregator, KeyValueStore<Bytes, byte[]>>as("signalStateEventAssessments")
                    .withKeySerde(Serdes.String())
                    .withValueSerde(JsonSerdes.SignalStateEventAggregator())
            );


        

        // Map the Windowed K Stream back to a Key Value Pair
        KStream<String, StopLinePassageAssessment> signalStateAssessmentStream = signalStateAssessments.toStream()
            .map((key, value) -> {
                StopLinePassageAssessment assessment = value.getSignalStateEventAssessment();
                assessment.setSource(key);
                return KeyValue.pair(key, assessment);
            }
        );

        signalStateAssessmentStream.to(
            parameters.getSignalStateEventAssessmentOutputTopicName(), 
            Produced.with(Serdes.String(),
                    JsonSerdes.SignalStateEventAssessment()));

        signalStateAssessmentStream.print(Printed.toSysOut());

        return builder.build();
    }    


}

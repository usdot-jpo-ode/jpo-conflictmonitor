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
import us.dot.its.jpo.conflictmonitor.monitor.models.assessments.StopLineStopAggregator;
import us.dot.its.jpo.conflictmonitor.monitor.models.assessments.StopLineStopAssessment;
import us.dot.its.jpo.conflictmonitor.monitor.models.events.StopLineStopEvent;
import us.dot.its.jpo.conflictmonitor.monitor.models.events.TimestampExtractors.StopLineStopTimestampExtractor;
import us.dot.its.jpo.conflictmonitor.monitor.serialization.JsonSerdes;

import static us.dot.its.jpo.conflictmonitor.monitor.algorithms.stop_line_stop_assessment.StopLineStopAssessmentConstants.DEFAULT_STOP_LINE_STOP_ASSESSMENT_ALGORITHM;;


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
            agg.setMessageDurationDays(parameters.getLookBackPeriodDays());

            logger.info("Setting up Stop Line Stop Topology \n\n\n\n");
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


        

        // Map the Windowed K Stream back to a Key Value Pair
        KStream<String, StopLineStopAssessment> stopLineStopAssessmentStream = stopLineStopAssessments.toStream()
            .map((key, value) -> {
                logger.info("\n\n\n\n\n\n Generating Stop Line Stop Assessment");
                
                return KeyValue.pair(key, value.getStopLineStopAssessment());
            }
        );

        logger.error("Stop Line Assesment Output Topic name:" + parameters.getStopLineStopAssessmentOutputTopicName());

        stopLineStopAssessmentStream.to(
            parameters.getStopLineStopAssessmentOutputTopicName(), 
            Produced.with(Serdes.String(),
                    JsonSerdes.StopLineStopAssessment()));

        stopLineStopAssessmentStream.print(Printed.toSysOut());

        return builder.build();
    }    


}

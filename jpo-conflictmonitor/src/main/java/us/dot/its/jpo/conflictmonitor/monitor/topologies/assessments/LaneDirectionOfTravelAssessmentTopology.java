package us.dot.its.jpo.conflictmonitor.monitor.topologies.assessments;

import static us.dot.its.jpo.conflictmonitor.monitor.algorithms.lane_direction_of_travel_assessment.LaneDirectionOfTravelAssessmentConstants.*;

import java.util.Properties;



import org.apache.kafka.common.serialization.Serdes;
import org.apache.kafka.common.utils.Bytes;
import org.apache.kafka.streams.KafkaStreams;
import org.apache.kafka.streams.KeyValue;
import org.apache.kafka.streams.StreamsBuilder;
import org.apache.kafka.streams.Topology;
import org.apache.kafka.streams.KafkaStreams.StateListener;
import org.apache.kafka.streams.errors.StreamsUncaughtExceptionHandler.StreamThreadExceptionResponse;
import org.apache.kafka.streams.kstream.Aggregator;
import org.apache.kafka.streams.kstream.Consumed;
import org.apache.kafka.streams.kstream.Grouped;
import org.apache.kafka.streams.kstream.Initializer;
import org.apache.kafka.streams.kstream.KGroupedStream;
import org.apache.kafka.streams.kstream.KStream;
import org.apache.kafka.streams.kstream.KTable;
import org.apache.kafka.streams.kstream.Materialized;
import org.apache.kafka.streams.kstream.Printed;
import org.apache.kafka.streams.kstream.Produced;
import org.apache.kafka.streams.state.KeyValueStore;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import us.dot.its.jpo.conflictmonitor.monitor.algorithms.lane_direction_of_travel_assessment.LaneDirectionOfTravelAssessmentParameters;
import us.dot.its.jpo.conflictmonitor.monitor.algorithms.lane_direction_of_travel_assessment.LaneDirectionOfTravelAssessmentStreamsAlgorithm;
import us.dot.its.jpo.conflictmonitor.monitor.models.assessments.LaneDirectionOfTravelAggregator;
import us.dot.its.jpo.conflictmonitor.monitor.models.assessments.LaneDirectionOfTravelAssessment;
import us.dot.its.jpo.conflictmonitor.monitor.models.events.LaneDirectionOfTravelEvent;
import us.dot.its.jpo.conflictmonitor.monitor.models.events.TimestampExtractors.LaneDirectionOfTravelTimestampExtractor;
import us.dot.its.jpo.conflictmonitor.monitor.serialization.JsonSerdes;


@Component(DEFAULT_LANE_DIRECTION_OF_TRAVEL_ASSESSMENT_ALGORITHM)
public class LaneDirectionOfTravelAssessmentTopology 
    implements LaneDirectionOfTravelAssessmentStreamsAlgorithm {

    private static final Logger logger = LoggerFactory.getLogger(LaneDirectionOfTravelAssessmentTopology.class);

    LaneDirectionOfTravelAssessmentParameters parameters;
    Properties streamsProperties;
    Topology topology;
    KafkaStreams streams;

    @Override
    public void setParameters(LaneDirectionOfTravelAssessmentParameters parameters) {
        this.parameters = parameters;
    }

    @Override
    public LaneDirectionOfTravelAssessmentParameters getParameters() {
        return parameters;
    }

    @Override
    public void setStreamsProperties(Properties streamsProperties) {
       this.streamsProperties = streamsProperties;
    }

    @Override
    public Properties getStreamsProperties() {
        return streamsProperties;
    }

    @Override
    public KafkaStreams getStreams() {
        return streams;
    }

    @Override
    public void start() {
        if (parameters == null) {
            throw new IllegalStateException("Start called before setting parameters.");
        }
        if (streamsProperties == null) {
            throw new IllegalStateException("Streams properties are not set.");
        }
        if (streams != null && streams.state().isRunningOrRebalancing()) {
            throw new IllegalStateException("Start called while streams is already running.");
        }

        

        logger.info("StartingSignalStateEventAssessmentTopology");
        Topology topology = buildTopology();
        streams = new KafkaStreams(topology, streamsProperties);
        streams.setUncaughtExceptionHandler(ex -> {
            logger.error("KafkaStreams uncaught exception, will try replacing thread", ex);
            return StreamThreadExceptionResponse.REPLACE_THREAD;
        });
        streams.start();
        try {
            Thread.sleep(15000);
        } catch (InterruptedException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        logger.info("Started SignalStateEventAssessmentTopology.");
        System.out.println("Started Events Topology");
    }

    public Topology buildTopology() {
        var builder = new StreamsBuilder();

        
        KStream<String, LaneDirectionOfTravelEvent> laneDirectionOfTravelEvents = 
            builder.stream(
                parameters.getLaneDirectionOfTravelEventTopicName(), 
                Consumed.with(
                    Serdes.String(), 
                    JsonSerdes.LaneDirectionOfTravelEvent())
                    .withTimestampExtractor(new LaneDirectionOfTravelTimestampExtractor())
                );

        if(parameters.isDebug())
        laneDirectionOfTravelEvents.print(Printed.toSysOut());

        KGroupedStream<String, LaneDirectionOfTravelEvent> laneDirectionOfTravelEventsGroup = laneDirectionOfTravelEvents.groupByKey(Grouped.with(Serdes.String(), JsonSerdes.LaneDirectionOfTravelEvent()));

        Initializer<LaneDirectionOfTravelAggregator> laneDirectionOfTravelAssessmentInitializer = ()->{
            LaneDirectionOfTravelAggregator agg = new LaneDirectionOfTravelAggregator();
            agg.setMessageDurationDays(parameters.getLookBackPeriodDays());
            agg.setTolerance(parameters.getHeadingToleranceDegrees());
            return agg;
        };

        Aggregator<String, LaneDirectionOfTravelEvent, LaneDirectionOfTravelAggregator> laneDirectionOfTravelEventAggregator = 
            (key, value, aggregate) -> aggregate.add(value);

        KTable<String, LaneDirectionOfTravelAggregator> laneDirectionOfTravelAssessments = 
            laneDirectionOfTravelEvents.groupByKey(Grouped.with(Serdes.String(), JsonSerdes.LaneDirectionOfTravelEvent()))
            //.windowedBy(signalStateEventJoinWindow)
            .aggregate(
                laneDirectionOfTravelAssessmentInitializer,
                laneDirectionOfTravelEventAggregator,
                Materialized.<String, LaneDirectionOfTravelAggregator, KeyValueStore<Bytes, byte[]>>as("laneDirectionOfTravelEventAssessments")
                    .withKeySerde(Serdes.String())
                    .withValueSerde(JsonSerdes.LaneDirectionOfTravelAggregator())
            );
        
        KStream<String, LaneDirectionOfTravelAssessment> laneDirectionOfTravelAssessmentStream = laneDirectionOfTravelAssessments.toStream()
            .map((key, value) -> KeyValue.pair(key, value.getLaneDirectionOfTravelAssessment())
        );
        
        // laneDirectionOfTravelAssessmentStream.print(Printed.toSysOut());

        laneDirectionOfTravelAssessmentStream.to(
        parameters.getLaneDirectionOfTravelAssessmentOutputTopicName(), 
        Produced.with(Serdes.String(),
                JsonSerdes.LaneDirectionOfTravelAssessment()));


        return builder.build();
    }    

    @Override
    public void stop() {
        logger.info("Stopping SignalStateEventAssessmentTopology.");
        if (streams != null) {
            streams.close();
            streams.cleanUp();
            streams = null;
        }
        logger.info("Stopped SignalStateEventAssessmentTopology.");
    }

    @Override
    public void registerStateListener(StateListener stateListener) {
        if (streams != null) {
            streams.setStateListener(stateListener);
        }
    }
    
}

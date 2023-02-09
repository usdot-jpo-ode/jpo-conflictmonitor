package us.dot.its.jpo.conflictmonitor.monitor.topologies.assessments;

import static us.dot.its.jpo.conflictmonitor.monitor.algorithms.signal_state_event_assessment.SignalStateEventAssessmentConstants.*;

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
import org.apache.kafka.streams.kstream.KStream;
import org.apache.kafka.streams.kstream.KTable;
import org.apache.kafka.streams.kstream.Materialized;
import org.apache.kafka.streams.kstream.Printed;
import org.apache.kafka.streams.kstream.Produced;
import org.apache.kafka.streams.state.KeyValueStore;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import us.dot.its.jpo.conflictmonitor.monitor.algorithms.signal_state_event_assessment.SignalStateEventAssessmentParameters;
import us.dot.its.jpo.conflictmonitor.monitor.algorithms.signal_state_event_assessment.SignalStateEventAssessmentStreamsAlgorithm;
import us.dot.its.jpo.conflictmonitor.monitor.models.assessments.SignalStateEventAggregator;
import us.dot.its.jpo.conflictmonitor.monitor.models.assessments.SignalStateEventAssessment;
import us.dot.its.jpo.conflictmonitor.monitor.models.events.SignalStateEvent;
import us.dot.its.jpo.conflictmonitor.monitor.models.events.TimestampExtractors.SignalStateTimestampExtractor;
import us.dot.its.jpo.conflictmonitor.monitor.serialization.JsonSerdes;



@Component(DEFAULT_SIGNAL_STATE_EVENT_ASSESSMENT_ALGORITHM)
public class SignalStateEventAssessmentTopology 
    implements SignalStateEventAssessmentStreamsAlgorithm {

    private static final Logger logger = LoggerFactory.getLogger(SignalStateEventAssessmentTopology.class);

    SignalStateEventAssessmentParameters parameters;
    Properties streamsProperties;
    Topology topology;
    KafkaStreams streams;

    @Override
    public void setParameters(SignalStateEventAssessmentParameters parameters) {
        this.parameters = parameters;
    }

    @Override
    public SignalStateEventAssessmentParameters getParameters() {
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
        logger.info("Started SignalStateEventAssessmentTopology.");
        System.out.println("Started Events Topology");
    }

    public Topology buildTopology() {
        var builder = new StreamsBuilder();

        // GeoJson Input Spat Stream
        KStream<String, SignalStateEvent> signalStateEvents = 
            builder.stream(
                parameters.getSignalStateEventTopicName(), 
                Consumed.with(
                    Serdes.String(), 
                    JsonSerdes.SignalStateEvent())
                    .withTimestampExtractor(new SignalStateTimestampExtractor())
                );

        Initializer<SignalStateEventAggregator> signalStateAssessmentInitializer = ()->{
            SignalStateEventAggregator agg = new SignalStateEventAggregator();
            agg.setMessageDurationDays(parameters.getLookBackPeriodDays());
            return agg;
        };

        signalStateEvents.print(Printed.toSysOut());

        Aggregator<String, SignalStateEvent, SignalStateEventAggregator> signalStateEventAggregator = 
            (key, value, aggregate) -> aggregate.add(value);


        KTable<String, SignalStateEventAggregator> signalStateAssessments = 
            signalStateEvents.groupByKey(Grouped.with(Serdes.String(), JsonSerdes.SignalStateEvent()))
            .aggregate(
                signalStateAssessmentInitializer,
                signalStateEventAggregator,
                Materialized.<String, SignalStateEventAggregator, KeyValueStore<Bytes, byte[]>>as("signalStateEventAssessments")
                    .withKeySerde(Serdes.String())
                    .withValueSerde(JsonSerdes.SignalStateEventAggregator())
            );


        

        // Map the Windowed K Stream back to a Key Value Pair
        KStream<String, SignalStateEventAssessment> signalStateAssessmentStream = signalStateAssessments.toStream()
            .map((key, value) -> KeyValue.pair(key, value.getSignalStateEventAssessment())
        );

        signalStateAssessmentStream.to(
            parameters.getSignalStateEventAssessmentOutputTopicName(), 
            Produced.with(Serdes.String(),
                    JsonSerdes.SignalStateEventAssessment()));

        signalStateAssessmentStream.print(Printed.toSysOut());

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

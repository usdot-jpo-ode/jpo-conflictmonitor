package us.dot.its.jpo.conflictmonitor.monitor.topologies.assessments;

import static us.dot.its.jpo.conflictmonitor.monitor.algorithms.connection_of_travel_assessment.ConnectionOfTravelAssessmentConstants.*;

import java.time.Duration;
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
import org.apache.kafka.streams.kstream.SlidingWindows;
import org.apache.kafka.streams.kstream.Windowed;
import org.apache.kafka.streams.state.KeyValueStore;
import org.apache.kafka.streams.state.WindowStore;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import us.dot.its.jpo.conflictmonitor.monitor.algorithms.connection_of_travel_assessment.ConnectionOfTravelAssessmentParameters;
import us.dot.its.jpo.conflictmonitor.monitor.algorithms.connection_of_travel_assessment.ConnectionOfTravelAssessmentStreamsAlgorithm;
import us.dot.its.jpo.conflictmonitor.monitor.models.assessments.ConnectionOfTravelAggregator;
import us.dot.its.jpo.conflictmonitor.monitor.models.assessments.ConnectionOfTravelAssessment;
import us.dot.its.jpo.conflictmonitor.monitor.models.events.ConnectionOfTravelEvent;
import us.dot.its.jpo.conflictmonitor.monitor.models.events.TimestampExtractors.ConnectionOfTravelTimestampExtractor;
import us.dot.its.jpo.conflictmonitor.monitor.serialization.JsonSerdes;


@Component(DEFAULT_CONNECTION_OF_TRAVEL_ASSESSMENT_ALGORITHM)
public class ConnectionOfTravelAssessmentTopology 
    implements ConnectionOfTravelAssessmentStreamsAlgorithm {

    private static final Logger logger = LoggerFactory.getLogger(ConnectionOfTravelAssessmentTopology.class);

    ConnectionOfTravelAssessmentParameters parameters;
    Properties streamsProperties;
    Topology topology;
    KafkaStreams streams;

    @Override
    public void setParameters(ConnectionOfTravelAssessmentParameters parameters) {
        this.parameters = parameters;
    }

    @Override
    public ConnectionOfTravelAssessmentParameters getParameters() {
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
        logger.info("StartingConnectionOfTravelAssessmentTopology");
        Topology topology = buildTopology();
        streams = new KafkaStreams(topology, streamsProperties);
        streams.setUncaughtExceptionHandler(ex -> {
            logger.error("KafkaStreams uncaught exception, will try replacing thread", ex);
            return StreamThreadExceptionResponse.REPLACE_THREAD;
        });
        streams.start();
        logger.info("Started ConnectionOfTravelAssessmentTopology.");
        System.out.println("Started Events Topology");
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
            agg.setMessageDurationDays(parameters.getLookBackPeriodDays());
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

        // Map the Windowed K Stream back to a Key Value Pair
        KStream<String, ConnectionOfTravelAssessment> connectionOfTravelAssessmentStream = connectionOfTravelAssessments.toStream()
            .map((key, value) -> KeyValue.pair(key, value.getConnectionOfTravelAssessment())
        );

        if(parameters.isDebug()){
            connectionOfTravelAssessmentStream.print(Printed.toSysOut());
        }

        connectionOfTravelAssessmentStream.to(
            parameters.getConnectionOfTravelAssessmentOutputTopicName(), 
            Produced.with(Serdes.String(),
                    JsonSerdes.ConnectionOfTravelAssessment()));

                    
        return builder.build();
    }    

    @Override
    public void stop() {
        logger.info("Stopping ConnectionOfTravelEventAssessmentTopology.");
        if (streams != null) {
            streams.close();
            streams.cleanUp();
            streams = null;
        }
        logger.info("Stopped ConnectionOfTravelEventAssessmentTopology.");
    }

    @Override
    public void registerStateListener(StateListener stateListener) {
        if (streams != null) {
            streams.setStateListener(stateListener);
        }
    }
    
}

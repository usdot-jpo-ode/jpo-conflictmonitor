package us.dot.its.jpo.conflictmonitor.monitor.algorithms;

import lombok.Getter;
import lombok.Setter;
import org.apache.kafka.streams.KafkaStreams;
import org.apache.kafka.streams.Topology;
import org.apache.kafka.streams.errors.StreamsUncaughtExceptionHandler;
import org.slf4j.Logger;


import java.util.Properties;

/**
 * Base class for a standalone streams topology
 * @param <TParams> Type of parameters
 */
public abstract class BaseStreamsTopology<TParams> {

    protected abstract Logger getLogger();

    @Getter
    @Setter
    protected TParams parameters;

    @Getter
    @Setter
    protected Properties streamsProperties;

    @Getter
    protected Topology topology;

    /**
     * -- SETTER --
     *  Method for testing
     */
    @Setter
    @Getter
    protected KafkaStreams streams;

    protected KafkaStreams.StateListener stateListener;
    protected StreamsUncaughtExceptionHandler exceptionHandler;


    public void start() {
        validate();
        getLogger().info("Starting {}.", this.getClass().getSimpleName());
        topology = buildTopology();
        streams = createKafkaStreams();
        if (exceptionHandler != null) streams.setUncaughtExceptionHandler(exceptionHandler);
        if (stateListener != null) streams.setStateListener(stateListener);
        streams.start();
        getLogger().info("Started {}.", this.getClass().getSimpleName());
    }

    protected void validate() {
        if (parameters == null) {
            throw new IllegalStateException("Start called before setting parameters.");
        }
        if (streamsProperties == null) {
            throw new IllegalStateException("Streams properties are not set.");
        }
        if (streams != null && streams.state().isRunningOrRebalancing()) {
            throw new IllegalStateException("Start called while streams is already running.");
        }
    }

    /**
     * Get the Kafka Streams topology from the supplied topology subcomponents
     * @return {@link Topology} for this streams topology
     */
    public abstract Topology buildTopology();


    /**
     * Overridable method for testing
     * @return {@link KafkaStreams}
     */
    protected KafkaStreams createKafkaStreams() {
        return new KafkaStreams(getTopology(), getStreamsProperties());
    }


    public void stop() {
        getLogger().info("Stopping {}.", this.getClass().getSimpleName());
        if (streams != null) {
            streams.close();
            streams.cleanUp();
            streams = null;
        }
        getLogger().info("Stopped {}.", this.getClass().getSimpleName());
    }


    public void registerStateListener(KafkaStreams.StateListener stateListener) {
        this.stateListener = stateListener;
    }


    public void registerUncaughtExceptionHandler(StreamsUncaughtExceptionHandler exceptionHandler) {
        this.exceptionHandler = exceptionHandler;
    }
}

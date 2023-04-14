package us.dot.its.jpo.conflictmonitor.monitor.algorithms;

import org.apache.kafka.streams.KafkaStreams;
import org.apache.kafka.streams.Topology;
import org.apache.kafka.streams.errors.StreamsUncaughtExceptionHandler;
import org.slf4j.Logger;


import java.util.Properties;

public abstract class BaseStreamsTopology<TParams>
        {

    protected abstract Logger getLogger();

    protected TParams parameters;
    protected Properties streamsProperties;
    protected Topology topology;
    protected KafkaStreams streams;
    protected KafkaStreams.StateListener stateListener;
    protected StreamsUncaughtExceptionHandler exceptionHandler;



    public void setParameters(TParams parameters) {
        this.parameters = parameters;
    }


    public TParams getParameters() {
        return parameters;
    }


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


    public void setStreamsProperties(Properties streamsProperties) {
        this.streamsProperties = streamsProperties;
    }


    public Properties getStreamsProperties() {
        return streamsProperties;
    }


    public KafkaStreams getStreams() {
        return streams;
    }


    public Topology getTopology() {
        return topology;
    }



    public void registerStateListener(KafkaStreams.StateListener stateListener) {
        this.stateListener = stateListener;
    }


    public void registerUncaughtExceptionHandler(StreamsUncaughtExceptionHandler exceptionHandler) {
        this.exceptionHandler = exceptionHandler;
    }
}

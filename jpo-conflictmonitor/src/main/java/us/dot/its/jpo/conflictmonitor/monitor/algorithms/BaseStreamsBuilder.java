package us.dot.its.jpo.conflictmonitor.monitor.algorithms;

import org.apache.kafka.streams.StreamsBuilder;
import org.apache.kafka.streams.Topology;
import org.slf4j.Logger;

/**
 * Base class for an algorithm implemented as a chained topology builder to build part of
 * a larger topology.
 *
 * @param <TParams>
 */
public abstract class BaseStreamsBuilder<TParams> {

    protected abstract Logger getLogger();

    protected TParams parameters;

    public void setParameters(TParams parameters) {
        this.parameters = parameters;
    }

    public TParams getParameters() {
        return parameters;
    }


    public abstract StreamsBuilder buildTopology(StreamsBuilder builder);

}

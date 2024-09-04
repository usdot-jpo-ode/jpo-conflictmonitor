package us.dot.its.jpo.conflictmonitor.monitor.algorithms;

import lombok.Getter;
import lombok.Setter;
import org.apache.kafka.streams.StreamsBuilder;
import org.slf4j.Logger;

/**
 * Base class for an algorithm implemented as a chained topology builder to build part of a larger topology.
 *
 * @param <TParams>
 */
@Getter
@Setter
public abstract class BaseStreamsBuilder<TParams> {

    protected abstract Logger getLogger();

    protected TParams parameters;

}

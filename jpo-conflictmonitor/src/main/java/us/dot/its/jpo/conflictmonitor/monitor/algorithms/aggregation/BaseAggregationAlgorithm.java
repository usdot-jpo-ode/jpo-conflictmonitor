package us.dot.its.jpo.conflictmonitor.monitor.algorithms.aggregation;

import us.dot.its.jpo.conflictmonitor.monitor.algorithms.ConfigurableAlgorithm;
import us.dot.its.jpo.conflictmonitor.monitor.models.events.Event;

import java.util.function.Supplier;

/**
 * Aggregation Algorithm
 *
 * @param <TEvent> Input event type
 * @param <TAggEvent> Output aggregated event type
 */
public interface BaseAggregationAlgorithm<TEvent extends Event, TAggEvent extends Event>
    extends ConfigurableAlgorithm<AggregationParameters> {

    /**
     * @return A new TAggEvent
     */
    TAggEvent constructEventAggregation();

}

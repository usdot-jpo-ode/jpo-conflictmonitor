package us.dot.its.jpo.conflictmonitor.monitor.algorithms.aggregation;

import us.dot.its.jpo.conflictmonitor.monitor.algorithms.ConfigurableAlgorithm;
import us.dot.its.jpo.conflictmonitor.monitor.models.events.Event;
import us.dot.its.jpo.conflictmonitor.monitor.models.events.EventAggregation;

/**
 * Aggregation Algorithm
 *
 * @param <TEvent> Input event type
 * @param <TAggEvent> Output aggregated event type
 */
public interface AggregationAlgorithmInterface<TEvent extends Event, TAggEvent extends EventAggregation<TEvent>>
    extends ConfigurableAlgorithm<AggregationParameters> {

    /**
     * Creates an event aggregation object, initializing it with the first received event object within a time period
     * @param event The first event object
     * @return The event aggregation object
     */
    TAggEvent constructEventAggregation(TEvent event);

    /**
     * The event aggregation type
     * @return String naming the event aggregation type
     */
    String eventAggregationType();
}

package us.dot.its.jpo.conflictmonitor.monitor.algorithms.event_state_progression;

import us.dot.its.jpo.conflictmonitor.monitor.algorithms.ConfigurableAlgorithm;
import us.dot.its.jpo.conflictmonitor.monitor.algorithms.aggregation.event_state_progression.EventStateProgressionAggregationAlgorithm;

/**
 * Interface for Event State Progression algorithms.
 * <p>
 * Extends the base {@link ConfigurableAlgorithm} interface for Event State Progression validation parameters.
 */
public interface EventStateProgressionAlgorithm
    extends ConfigurableAlgorithm<EventStateProgressionParameters> {

    void setAggregationAlgorithm(EventStateProgressionAggregationAlgorithm aggregationAlgorithm);

}

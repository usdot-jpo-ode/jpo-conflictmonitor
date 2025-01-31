package us.dot.its.jpo.conflictmonitor.monitor.algorithms.event_state_progression;

import us.dot.its.jpo.conflictmonitor.monitor.algorithms.ConfigurableAlgorithm;
import us.dot.its.jpo.conflictmonitor.monitor.algorithms.aggregation.event_state_progression.EventStateProgressionAggregationAlgorithm;

/**
 * Algorithm to detect illegal SPaT transitions.
 */
public interface EventStateProgressionAlgorithm
    extends ConfigurableAlgorithm<EventStateProgressionParameters> {

    void setAggregationAlgorithm(EventStateProgressionAggregationAlgorithm aggregationAlgorithm);

}

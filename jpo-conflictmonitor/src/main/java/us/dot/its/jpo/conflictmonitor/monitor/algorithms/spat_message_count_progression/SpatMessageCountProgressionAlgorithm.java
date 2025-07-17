package us.dot.its.jpo.conflictmonitor.monitor.algorithms.spat_message_count_progression;

import us.dot.its.jpo.conflictmonitor.monitor.algorithms.Algorithm;
import us.dot.its.jpo.conflictmonitor.monitor.algorithms.aggregation.spat_message_count_progression.SpatMessageCountProgressionAggregationAlgorithm;

/**
 * Interface for SPaT message count progression algorithms.
 * <p>
 * Extends the base {@link Algorithm} interface for SPaT message count progression parameters.
 */
public interface SpatMessageCountProgressionAlgorithm extends Algorithm<SpatMessageCountProgressionParameters>{

    /**
     * Sets the AggregationAlgorithm used for validation.
     *
     * @param aggregationAlgorithm the aggregationAlgorithm to set
     */
    void setAggregationAlgorithm(SpatMessageCountProgressionAggregationAlgorithm aggregationAlgorithm);

}







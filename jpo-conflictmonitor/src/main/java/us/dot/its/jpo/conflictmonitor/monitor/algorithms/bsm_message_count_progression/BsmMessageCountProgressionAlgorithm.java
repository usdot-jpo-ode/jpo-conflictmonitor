package us.dot.its.jpo.conflictmonitor.monitor.algorithms.bsm_message_count_progression;

import us.dot.its.jpo.conflictmonitor.monitor.algorithms.Algorithm;
import us.dot.its.jpo.conflictmonitor.monitor.algorithms.aggregation.bsm_message_count_progression.BsmMessageCountProgressionAggregationAlgorithm;

/**
 * Interface for BSM Message Count Progression algorithms.
 * <p>
 * Extends the base {@link Algorithm} interface for BSM Message Count Progression Algorithms.
 */
public interface BsmMessageCountProgressionAlgorithm
        extends Algorithm<BsmMessageCountProgressionParameters>{


    /**
     * Sets aggregationAlgorithm used for BSM Message Count Progression analysis.
     *
     * @param aggregationAlgorithm the aggregationAlgorithm to set
     */
    void setAggregationAlgorithm(BsmMessageCountProgressionAggregationAlgorithm aggregationAlgorithm);

}







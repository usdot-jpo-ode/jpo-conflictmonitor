package us.dot.its.jpo.conflictmonitor.monitor.algorithms.validation.spat;

import us.dot.its.jpo.conflictmonitor.monitor.algorithms.Algorithm;
import us.dot.its.jpo.conflictmonitor.monitor.algorithms.aggregation.validation.spat.SpatMinimumDataAggregationAlgorithm;
import us.dot.its.jpo.conflictmonitor.monitor.algorithms.timestamp_delta.spat.SpatTimestampDeltaAlgorithm;

/**
 * Interface for SPaT validation algorithms.
 * <p>
 * Extends the base {@link Algorithm} interface for SPaT validation parameters.
 */
public interface SpatValidationAlgorithm
    extends Algorithm<SpatValidationParameters> {

    /**
     * Gets the SPaT timestamp delta algorithm used for validation.
     *
     * @return the SpatTimestampDeltaAlgorithm instance
     */
    SpatTimestampDeltaAlgorithm getTimestampDeltaAlgorithm();

    /**
     * Sets the SPaT timestamp delta algorithm used for validation.
     *
     * @param timestampDeltaAlgorithm the SpatTimestampDeltaAlgorithm to set
     */
    void setTimestampDeltaAlgorithm(SpatTimestampDeltaAlgorithm timestampDeltaAlgorithm);

    /**
     * Sets the minimum data aggregation algorithm used for SPaT validation.
     *
     * @param spatMinimumDataAggregationAlgorithm the SpatMinimumDataAggregationAlgorithm to set
     */
    void setMinimumDataAggregationAlgorithm(SpatMinimumDataAggregationAlgorithm spatMinimumDataAggregationAlgorithm);
}

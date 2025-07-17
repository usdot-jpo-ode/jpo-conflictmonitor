package us.dot.its.jpo.conflictmonitor.monitor.algorithms.map_message_count_progression;

import us.dot.its.jpo.conflictmonitor.monitor.algorithms.Algorithm;
import us.dot.its.jpo.conflictmonitor.monitor.algorithms.aggregation.map_message_count_progression.MapMessageCountProgressionAggregationAlgorithm;

/**
 * Interface for Map Message Count Progression algorithms.
 * <p>
 * Extends the base {@link Algorithm} interface for Map Message Count Progression parameters.
 */
public interface MapMessageCountProgressionAlgorithm extends Algorithm<MapMessageCountProgressionParameters>{

    /**
     * Sets the aggregation algorithm used for Map Message Count Progression.
     *
     * @param aggregationAlgorithm the Map Message Count Progression algorithm to set
     */
    void setAggregationAlgorithm(MapMessageCountProgressionAggregationAlgorithm aggregationAlgorithm);

}







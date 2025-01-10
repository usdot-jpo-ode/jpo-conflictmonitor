package us.dot.its.jpo.conflictmonitor.monitor.algorithms.map_message_count_progression;

import us.dot.its.jpo.conflictmonitor.monitor.algorithms.Algorithm;
import us.dot.its.jpo.conflictmonitor.monitor.algorithms.aggregation.map_message_count_progression.MapMessageCountProgressionAggregationAlgorithm;

public interface MapMessageCountProgressionAlgorithm extends Algorithm<MapMessageCountProgressionParameters>{

    void setAggregationAlgorithm(MapMessageCountProgressionAggregationAlgorithm aggregationAlgorithm);

}







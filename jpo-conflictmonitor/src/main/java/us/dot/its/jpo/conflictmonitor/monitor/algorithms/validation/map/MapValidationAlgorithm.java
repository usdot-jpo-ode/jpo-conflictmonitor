package us.dot.its.jpo.conflictmonitor.monitor.algorithms.validation.map;

import us.dot.its.jpo.conflictmonitor.monitor.algorithms.Algorithm;
import us.dot.its.jpo.conflictmonitor.monitor.algorithms.timestamp_delta.map.MapTimestampDeltaAlgorithm;

public interface MapValidationAlgorithm
        extends Algorithm<MapValidationParameters> {

    MapTimestampDeltaAlgorithm getTimestampDeltaAlgorithm();
    void setTimestampDeltaAlgorithm(MapTimestampDeltaAlgorithm timestampDeltaAlgorithm);
}

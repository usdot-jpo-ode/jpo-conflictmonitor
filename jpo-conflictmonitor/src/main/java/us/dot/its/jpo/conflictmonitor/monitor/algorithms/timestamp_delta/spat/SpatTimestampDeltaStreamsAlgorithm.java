package us.dot.its.jpo.conflictmonitor.monitor.algorithms.timestamp_delta.spat;

import us.dot.its.jpo.conflictmonitor.monitor.algorithms.timestamp_delta.BaseTimestampDeltaStreamsAlgorithm;
import us.dot.its.jpo.geojsonconverter.pojos.spat.ProcessedSpat;

public interface SpatTimestampDeltaStreamsAlgorithm
        extends SpatTimestampDeltaAlgorithm, BaseTimestampDeltaStreamsAlgorithm<ProcessedSpat> {
}

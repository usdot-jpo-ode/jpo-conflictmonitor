package us.dot.its.jpo.conflictmonitor.monitor.algorithms.timestamp_delta.map;

import us.dot.its.jpo.conflictmonitor.monitor.algorithms.timestamp_delta.BaseTimestampDeltaStreamsAlgorithm;
import us.dot.its.jpo.geojsonconverter.pojos.geojson.LineString;
import us.dot.its.jpo.geojsonconverter.pojos.geojson.map.ProcessedMap;

public interface MapTimestampDeltaStreamsAlgorithm
        extends MapTimestampDeltaAlgorithm, BaseTimestampDeltaStreamsAlgorithm<ProcessedMap<LineString>> {
}

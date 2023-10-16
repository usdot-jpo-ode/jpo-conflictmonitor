package us.dot.its.jpo.conflictmonitor.monitor.models.map.store;

import org.apache.kafka.common.utils.Bytes;
import org.locationtech.jts.geom.CoordinateXY;
import us.dot.its.jpo.geojsonconverter.pojos.geojson.LineString;
import us.dot.its.jpo.geojsonconverter.pojos.geojson.map.ProcessedMap;

import java.util.List;

public interface ReadableMapSpatiallyIndexedStateStore {
    byte[] read(Bytes key);
    List<ProcessedMap<LineString>> spatialQuery(CoordinateXY coords);
}

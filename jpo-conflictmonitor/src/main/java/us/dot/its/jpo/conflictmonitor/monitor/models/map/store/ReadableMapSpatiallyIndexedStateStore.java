package us.dot.its.jpo.conflictmonitor.monitor.models.map.store;

import org.apache.kafka.common.utils.Bytes;
import org.locationtech.jts.geom.CoordinateXY;
import us.dot.its.jpo.conflictmonitor.monitor.models.map.MapBoundingBox;


import java.util.List;

public interface ReadableMapSpatiallyIndexedStateStore {
    byte[] read(Bytes key);
    List<MapBoundingBox> spatialQuery(CoordinateXY coords);
}

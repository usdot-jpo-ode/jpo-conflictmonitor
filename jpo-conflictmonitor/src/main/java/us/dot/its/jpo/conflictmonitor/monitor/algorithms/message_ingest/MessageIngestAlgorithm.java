package us.dot.its.jpo.conflictmonitor.monitor.algorithms.message_ingest;

import org.locationtech.jts.index.quadtree.Quadtree;
import us.dot.its.jpo.conflictmonitor.monitor.algorithms.Algorithm;
import us.dot.its.jpo.conflictmonitor.monitor.models.map.MapIndex;

public interface MessageIngestAlgorithm
    extends Algorithm<MessageIngestParameters> {
    MapIndex getMapIndex();
    void setMapIndex(MapIndex mapIndex);
}

package us.dot.its.jpo.conflictmonitor.monitor.algorithms.bsm_event;

import us.dot.its.jpo.conflictmonitor.monitor.algorithms.Algorithm;
import us.dot.its.jpo.conflictmonitor.monitor.models.map.MapIndex;

public interface BsmEventAlgorithm extends Algorithm<BsmEventParameters> {

    MapIndex getMapIndex();
    void setMapIndex(MapIndex mapIndex);
}

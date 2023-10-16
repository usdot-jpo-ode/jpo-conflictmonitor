package us.dot.its.jpo.conflictmonitor.monitor.algorithms.bsm_event;

import us.dot.its.jpo.conflictmonitor.monitor.algorithms.StreamsTopology;
import us.dot.its.jpo.conflictmonitor.monitor.models.map.store.ReadableMapSpatiallyIndexedStateStore;

public interface BsmEventStreamsAlgorithm
    extends BsmEventAlgorithm, StreamsTopology {

    void setMapSpatiallyIndexedStateStore(ReadableMapSpatiallyIndexedStateStore mapSpatiallyIndexedStateStore);

}

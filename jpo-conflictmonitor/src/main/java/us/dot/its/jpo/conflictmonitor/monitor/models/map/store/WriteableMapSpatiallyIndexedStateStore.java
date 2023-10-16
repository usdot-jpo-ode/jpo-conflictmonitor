package us.dot.its.jpo.conflictmonitor.monitor.models.map.store;

import org.apache.kafka.common.utils.Bytes;

public interface WriteableMapSpatiallyIndexedStateStore extends ReadableMapSpatiallyIndexedStateStore {

    void write(Bytes key, byte[] value);
}

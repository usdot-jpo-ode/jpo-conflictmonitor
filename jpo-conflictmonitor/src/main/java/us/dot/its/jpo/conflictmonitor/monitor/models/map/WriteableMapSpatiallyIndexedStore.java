package us.dot.its.jpo.conflictmonitor.monitor.models.map;

import org.apache.kafka.common.utils.Bytes;

public interface WriteableMapSpatiallyIndexedStore extends ReadableMapSpatiallyIndexedStore {

    void write(Bytes key, byte[] value);
}

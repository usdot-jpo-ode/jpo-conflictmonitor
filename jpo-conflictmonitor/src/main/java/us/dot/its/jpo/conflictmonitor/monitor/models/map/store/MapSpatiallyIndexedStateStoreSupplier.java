package us.dot.its.jpo.conflictmonitor.monitor.models.map.store;


import org.apache.kafka.common.utils.Bytes;
import org.apache.kafka.streams.state.KeyValueBytesStoreSupplier;
import org.apache.kafka.streams.state.KeyValueStore;
import us.dot.its.jpo.conflictmonitor.monitor.models.map.MapIndex;

public class MapSpatiallyIndexedStateStoreSupplier implements KeyValueBytesStoreSupplier {

    private final String name;
    private final MapIndex mapIndex;
    private final String processedMapTopicName;

    public MapSpatiallyIndexedStateStoreSupplier(String name, MapIndex mapIndex, String processedMapTopicName) {
        this.name = name;
        this.mapIndex = mapIndex;
        this.processedMapTopicName = processedMapTopicName;
    }
    @Override
    public String name() {
        return name;
    }

    @Override
    public KeyValueStore<Bytes, byte[]> get() {
        return new MapSpatiallyIndexedStateStore(name, mapIndex, processedMapTopicName);
    }

    @Override
    public String metricsScope() {
        return "map-spatially-indexed";
    }
}

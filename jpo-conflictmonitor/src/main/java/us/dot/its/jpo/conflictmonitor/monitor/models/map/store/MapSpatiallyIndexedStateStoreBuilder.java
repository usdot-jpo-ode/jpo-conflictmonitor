package us.dot.its.jpo.conflictmonitor.monitor.models.map.store;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import lombok.With;
import lombok.experimental.Accessors;
import org.apache.kafka.common.serialization.Serde;
import org.apache.kafka.common.serialization.Serdes;
import org.apache.kafka.common.utils.Bytes;
import org.apache.kafka.common.utils.Time;
import org.apache.kafka.streams.state.internals.AbstractStoreBuilder;
import us.dot.its.jpo.conflictmonitor.monitor.models.map.MapIndex;


public class MapSpatiallyIndexedStateStoreBuilder extends AbstractStoreBuilder<Bytes, byte[], MapSpatiallyIndexedStateStore> {

    public MapSpatiallyIndexedStateStoreBuilder(final String name) {
        super(name, Serdes.Bytes(), Serdes.ByteArray(), Time.SYSTEM);
    }


    private MapIndex mapIndex;
    private String processedMapTopicName;

    public MapSpatiallyIndexedStateStoreBuilder withMapIndex(MapIndex mapIndex) {
        this.mapIndex = mapIndex;
        return this;
    }

    public MapSpatiallyIndexedStateStoreBuilder withProcessedMapTopicName(String processedMapTopicName) {
        this.processedMapTopicName = processedMapTopicName;
        return this;
    }

    @Override
    public MapSpatiallyIndexedStateStore build() {
        return new MapSpatiallyIndexedStateStore(name, mapIndex, processedMapTopicName);
    }
}

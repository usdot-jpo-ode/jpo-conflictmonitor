package us.dot.its.jpo.conflictmonitor.monitor.models.map.store;

import lombok.experimental.Accessors;
import org.apache.kafka.common.serialization.Serde;
import org.apache.kafka.common.utils.Bytes;
import org.apache.kafka.common.utils.Time;
import org.apache.kafka.streams.state.internals.AbstractStoreBuilder;
import us.dot.its.jpo.conflictmonitor.monitor.models.map.MapIndex;

@Accessors(fluent = true)
public class MapSpatiallyIndexedStateStoreBuilder extends AbstractStoreBuilder<Bytes, byte[], MapSpatiallyIndexedStateStore> {

    public MapSpatiallyIndexedStateStoreBuilder(final String name,
                                                final Serde<Bytes> keySerde,
                                                final Serde<byte[]> valueSerde,
                                                final Time time) {
        super(name, keySerde, valueSerde, time);
    }

    private MapIndex mapIndex;

    private String processedMapTopicName;

    @Override
    public MapSpatiallyIndexedStateStore build() {
        return new MapSpatiallyIndexedStateStore(name, mapIndex, processedMapTopicName);
    }
}

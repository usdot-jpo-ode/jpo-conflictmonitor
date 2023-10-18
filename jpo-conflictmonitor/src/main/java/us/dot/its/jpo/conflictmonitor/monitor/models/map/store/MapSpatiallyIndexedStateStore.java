package us.dot.its.jpo.conflictmonitor.monitor.models.map.store;

import org.apache.kafka.common.serialization.Serde;
import org.apache.kafka.common.utils.Bytes;
import org.apache.kafka.streams.KeyValue;
import org.apache.kafka.streams.state.ValueAndTimestamp;
import org.apache.kafka.streams.state.internals.InMemoryKeyValueStore;
import org.apache.kafka.streams.state.internals.ValueAndTimestampSerde;
import org.locationtech.jts.geom.CoordinateXY;
import us.dot.its.jpo.conflictmonitor.monitor.models.map.MapBoundingBox;
import us.dot.its.jpo.conflictmonitor.monitor.models.map.MapIndex;
import us.dot.its.jpo.conflictmonitor.monitor.serialization.JsonSerdes;

import java.util.List;


/**
 * Custom State store for {@link MapBoundingBox}es with spatial indexing and spatial query.
 * <p>Implemented according to:
 * <a href="https://docs.confluent.io/platform/current/streams/developer-guide/interactive-queries.html#querying-local-custom-state-stores">Querying local custom state stores</a>
 *
 */
public class MapSpatiallyIndexedStateStore
        extends InMemoryKeyValueStore
        implements WriteableMapSpatiallyIndexedStateStore {
    public MapSpatiallyIndexedStateStore(String name,
                                         MapIndex mapIndex,
                                         String processedMapTopicName) {
        super(name);
        this.mapIndex = mapIndex;
        this.processedMapTopicName = processedMapTopicName;
    }

    private final MapIndex mapIndex;
    private final String processedMapTopicName;

    @Override
    public synchronized void put(final Bytes key, final byte[] value) {
        super.put(key, value);
        insertIntoSpatialIndex(value);
    }

    @Override
    public synchronized byte[] putIfAbsent(final Bytes key, final byte[] value) {
        var result = super.putIfAbsent(key, value);
        insertIntoSpatialIndex(value);
        return result;
    }

    @Override
    public synchronized void putAll(final List<KeyValue<Bytes, byte[]>> entries) {
        super.putAll(entries);
        for (var entry : entries) {
            insertIntoSpatialIndex(entry.value);
        }
    }

    @Override
    public synchronized byte[] delete(final Bytes key) {
        var result = super.delete(key);

        // Remove from spatial index
        byte[] value = get(key);
        if (value != null) {
            try (Serde<MapBoundingBox> serde = JsonSerdes.MapBoundingBox()) {
                var deserializer = serde.deserializer();
                MapBoundingBox map = deserializer.deserialize(processedMapTopicName, value);
                mapIndex.remove(map);
            }
        }
        return result;
    }




    private void insertIntoSpatialIndex(byte[] value) {
        if (mapIndex == null) throw new RuntimeException("MapIndex is not set");
        if (processedMapTopicName == null) throw new RuntimeException("ProcessedMapTopicName is not set.");
        // deserialize ProcessedMap and insert into quadtree
        try (Serde<MapBoundingBox> serde = JsonSerdes.MapBoundingBox()) {
            // Values in the state store are wrapped with a timestamp
            try (ValueAndTimestampSerde<MapBoundingBox> vtSerde = new ValueAndTimestampSerde<>(serde)) {
                var deserializer = vtSerde.deserializer();
                ValueAndTimestamp<MapBoundingBox> valueAndTimestamp = deserializer.deserialize(processedMapTopicName, value);
                mapIndex.insert(valueAndTimestamp.value());
            }
        }
    }

    @Override
    public byte[] read(Bytes key) {
        return get(key);
    }

    /**
     * Spatial Query
     * @param coords
     * @return List of {@link MapBoundingBox}es containing the coordinate (Longitude/Latitude)
     */
    public List<MapBoundingBox> spatialQuery(CoordinateXY coords) {
        return mapIndex.mapsContainingPoint(coords);
    }

    @Override
    public void write(Bytes key, byte[] value) {
        put(key, value);
    }
}

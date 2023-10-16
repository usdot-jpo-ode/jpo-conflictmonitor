package us.dot.its.jpo.conflictmonitor.monitor.models.map.store;

import org.apache.kafka.common.utils.Bytes;
import org.apache.kafka.streams.state.QueryableStoreType;
import org.apache.kafka.streams.state.internals.StateStoreProvider;
import org.locationtech.jts.geom.CoordinateXY;
import us.dot.its.jpo.geojsonconverter.pojos.geojson.LineString;
import us.dot.its.jpo.geojsonconverter.pojos.geojson.map.ProcessedMap;

import java.util.ArrayList;
import java.util.List;

public class MapSpatiallyIndexedStateStoreTypeWrapper implements ReadableMapSpatiallyIndexedStateStore {

    private final QueryableStoreType<ReadableMapSpatiallyIndexedStateStore> customStoreType;
    private final String storeName;
    private final StateStoreProvider provider;

    public MapSpatiallyIndexedStateStoreTypeWrapper(final StateStoreProvider provider,
                                  final String storeName,
                                  final QueryableStoreType<ReadableMapSpatiallyIndexedStateStore> customStoreType) {
        this.provider = provider;
        this.storeName = storeName;
        this.customStoreType = customStoreType;
    }



    @Override
    public byte[] read(Bytes key) {
        final List<ReadableMapSpatiallyIndexedStateStore> stores = provider.stores(storeName, customStoreType);
        for (var store : stores) {
            var value = store.read(key);
            if (value != null) return value;
        }
        return null;
    }

    @Override
    public List<ProcessedMap<LineString>> spatialQuery(CoordinateXY coords) {
        final List<ReadableMapSpatiallyIndexedStateStore> stores = provider.stores(storeName, customStoreType);
        List<ProcessedMap<LineString>> values = new ArrayList<>();
        for (var store : stores) {
            var value = store.spatialQuery(coords);
            values.addAll(value);
        }
        return values;
    }
}

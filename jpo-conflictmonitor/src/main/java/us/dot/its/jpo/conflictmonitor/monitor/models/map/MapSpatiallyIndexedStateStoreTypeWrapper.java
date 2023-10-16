package us.dot.its.jpo.conflictmonitor.monitor.models.map;

import org.apache.kafka.common.utils.Bytes;
import org.apache.kafka.streams.state.QueryableStoreType;
import org.apache.kafka.streams.state.internals.StateStoreProvider;
import org.locationtech.jts.geom.CoordinateXY;
import us.dot.its.jpo.geojsonconverter.pojos.geojson.LineString;
import us.dot.its.jpo.geojsonconverter.pojos.geojson.map.ProcessedMap;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class MapSpatiallyIndexedStateStoreTypeWrapper implements ReadableMapSpatiallyIndexedStore {

    private final QueryableStoreType<ReadableMapSpatiallyIndexedStore> customStoreType;
    private final String storeName;
    private final StateStoreProvider provider;

    public MapSpatiallyIndexedStateStoreTypeWrapper(final StateStoreProvider provider,
                                  final String storeName,
                                  final QueryableStoreType<ReadableMapSpatiallyIndexedStore> customStoreType) {
        this.provider = provider;
        this.storeName = storeName;
        this.customStoreType = customStoreType;
    }



    @Override
    public byte[] read(Bytes key) {
        final List<ReadableMapSpatiallyIndexedStore> stores = provider.stores(storeName, customStoreType);
        for (var store : stores) {
            var value = store.read(key);
            if (value != null) return value;
        }
        return null;
    }

    @Override
    public List<ProcessedMap<LineString>> spatialQuery(CoordinateXY coords) {
        final List<ReadableMapSpatiallyIndexedStore> stores = provider.stores(storeName, customStoreType);
        List<ProcessedMap<LineString>> values = new ArrayList<>();
        for (var store : stores) {
            var value = store.spatialQuery(coords);
            values.addAll(value);
        }
        return values;
    }
}

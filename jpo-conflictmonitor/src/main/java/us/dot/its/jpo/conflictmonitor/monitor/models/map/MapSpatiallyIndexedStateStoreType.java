package us.dot.its.jpo.conflictmonitor.monitor.models.map;

import org.apache.kafka.streams.processor.StateStore;
import org.apache.kafka.streams.state.QueryableStoreType;
import org.apache.kafka.streams.state.internals.StateStoreProvider;

public class MapSpatiallyIndexedStateStoreType implements QueryableStoreType<ReadableMapSpatiallyIndexedStore> {
    @Override
    public boolean accepts(StateStore stateStore) {
        return stateStore instanceof MapSpatiallyIndexedStateStore;
    }

    @Override
    public ReadableMapSpatiallyIndexedStore create(StateStoreProvider storeProvider, String storeName) {
        return new MapSpatiallyIndexedStateStoreTypeWrapper(storeProvider, storeName, this);
    }
}

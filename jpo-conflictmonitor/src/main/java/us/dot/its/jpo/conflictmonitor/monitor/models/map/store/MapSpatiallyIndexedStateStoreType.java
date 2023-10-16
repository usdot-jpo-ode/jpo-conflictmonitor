package us.dot.its.jpo.conflictmonitor.monitor.models.map.store;

import org.apache.kafka.streams.processor.StateStore;
import org.apache.kafka.streams.state.QueryableStoreType;
import org.apache.kafka.streams.state.internals.StateStoreProvider;

public class MapSpatiallyIndexedStateStoreType implements QueryableStoreType<ReadableMapSpatiallyIndexedStateStore> {
    @Override
    public boolean accepts(StateStore stateStore) {
        return stateStore instanceof MapSpatiallyIndexedStateStore;
    }

    @Override
    public ReadableMapSpatiallyIndexedStateStore create(StateStoreProvider storeProvider, String storeName) {
        return new MapSpatiallyIndexedStateStoreTypeWrapper(storeProvider, storeName, this);
    }
}

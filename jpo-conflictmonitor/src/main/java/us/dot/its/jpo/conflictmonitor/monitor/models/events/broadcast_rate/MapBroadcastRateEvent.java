package us.dot.its.jpo.conflictmonitor.monitor.models.events.broadcast_rate;

import us.dot.its.jpo.geojsonconverter.geojson.map.MapFeatureCollection;

public class MapBroadcastRateEvent
    extends BroadcastRateEvent {

    @Override
    public Class<?> getMessageClass() {
        return MapFeatureCollection.class;
    }
        
}

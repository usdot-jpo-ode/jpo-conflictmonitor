package us.dot.its.jpo.conflictmonitor.monitor.models.broadcast_rate;

import us.dot.its.jpo.geojsonconverter.geojson.spat.SpatFeatureCollection;

public class SpatBroadcastRateEvent
    extends BroadcastRateEvent {

    @Override
    public Class<?> getMessageClass() {
       return SpatFeatureCollection.class;
    }
    
}

package us.dot.its.jpo.conflictmonitor.monitor.models.notifications.broadcast_rate;

import us.dot.its.jpo.conflictmonitor.monitor.models.events.broadcast_rate.MapBroadcastRateEvent;

public class MapBroadcastRateNotification 
    extends BroadcastRateNotification<MapBroadcastRateEvent> {

    public MapBroadcastRateNotification() {
        super("MapBroadcastRateNotification");
    }
}

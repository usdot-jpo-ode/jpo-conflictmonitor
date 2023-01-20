package us.dot.its.jpo.conflictmonitor.monitor.models.notifications.broadcast_rate;

import us.dot.its.jpo.conflictmonitor.monitor.models.events.broadcast_rate.SpatBroadcastRateEvent;

public class SpatBroadcastRateNotification extends BroadcastRateNotification<SpatBroadcastRateEvent> {

    public SpatBroadcastRateNotification() {
        super("SpatBroadcastRateNotification");
    }
    
}

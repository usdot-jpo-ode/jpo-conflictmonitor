package us.dot.its.jpo.conflictmonitor.monitor.models.events.timestamp_delta;

import us.dot.its.jpo.geojsonconverter.pojos.spat.ProcessedSpat;

public class SpatTimestampDeltaEvent
    extends BaseTimestampDeltaEvent {
    public SpatTimestampDeltaEvent() {
        super("SpatTimestampDelta", ProcessedSpat.class.getName());
    }
}

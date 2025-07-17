package us.dot.its.jpo.conflictmonitor.monitor.models.events.timestamp_delta;

import us.dot.its.jpo.geojsonconverter.pojos.spat.ProcessedSpat;

/**
 * SpatTimeStampDeltaEvent. A SpatTimeStampDeltaEvent is generated when the timestamp parsed by the SPaT message using the moy and year field is not close to the time at which the ODE received the SPaT message.
 * If these events are generated, it typically means that either the ODE or the RSU unit do not have an accurate time sync.
 */
public class SpatTimestampDeltaEvent
    extends BaseTimestampDeltaEvent {
    public SpatTimestampDeltaEvent() {
        super("SpatTimestampDelta", ProcessedSpat.class.getName());
    }
}

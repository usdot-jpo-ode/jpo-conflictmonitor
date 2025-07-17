package us.dot.its.jpo.conflictmonitor.monitor.models.events.timestamp_delta;

import us.dot.its.jpo.geojsonconverter.pojos.geojson.map.ProcessedMap;

/**
 * MapTimeStampDeltaEvent. A MapTimeStampDeltaEvent is generated when the timestamp parsed from the MAP message using the moy and year field is not close to the time at which the ODE received the MAP message.
 * If these events are generated, it typically means that either the ODE or the RSU unit do not have an accurate time sync. 
 * For MAP messages this event may be produced falsely if the RSU doesn't update the time of the MAP message before broadcasting it.
 */
public class MapTimestampDeltaEvent
    extends BaseTimestampDeltaEvent {

    public MapTimestampDeltaEvent() {
        super("MapTimestampDelta", ProcessedMap.class.getTypeName());
    }

}

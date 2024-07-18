package us.dot.its.jpo.conflictmonitor.monitor.models.events.timestamp_delta;

import us.dot.its.jpo.geojsonconverter.pojos.geojson.map.ProcessedMap;

public class MapTimestampDeltaEvent
    extends BaseTimestampDeltaEvent {

    public MapTimestampDeltaEvent() {
        super(ProcessedMap.class.getTypeName());
    }

}

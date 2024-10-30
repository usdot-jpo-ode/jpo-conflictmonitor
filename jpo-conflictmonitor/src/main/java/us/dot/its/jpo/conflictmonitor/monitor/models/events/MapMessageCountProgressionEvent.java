package us.dot.its.jpo.conflictmonitor.monitor.models.events;

import lombok.EqualsAndHashCode;
import lombok.Generated;
import lombok.Getter;
import lombok.Setter;
import us.dot.its.jpo.geojsonconverter.pojos.geojson.map.ProcessedMap;
import us.dot.its.jpo.geojsonconverter.pojos.geojson.LineString;
@Getter
@Setter
@EqualsAndHashCode(callSuper=true)
@Generated
public class MapMessageCountProgressionEvent extends Event{

    private ProcessedMap<LineString> previousMap;
    private ProcessedMap<LineString> newMap;
    private String message;

    public MapMessageCountProgressionEvent(){
        super("MapMessageCountProgression");
    }

}

package us.dot.its.jpo.conflictmonitor.monitor.models.events;

import java.time.ZonedDateTime;

import org.apache.kafka.common.protocol.types.Field.Str;

import lombok.EqualsAndHashCode;
import lombok.Generated;
import lombok.Getter;
import lombok.Setter;
import us.dot.its.jpo.geojsonconverter.pojos.geojson.map.ProcessedMap;
import us.dot.its.jpo.geojsonconverter.pojos.geojson.LineString;

enum TimeMarkType {
    MIN_END_TIME,
    MAX_END_TIME,
}

@Getter
@Setter
@EqualsAndHashCode(callSuper=true)
@Generated
public class MapRevisionCounterEvent extends Event{

    private ProcessedMap<LineString> previousMap;
    private ProcessedMap<LineString> newMap;
    private String message;

    public MapRevisionCounterEvent(){
        super("MapRevisionCounter");
    }

}

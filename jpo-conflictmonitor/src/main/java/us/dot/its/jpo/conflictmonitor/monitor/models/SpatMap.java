package us.dot.its.jpo.conflictmonitor.monitor.models;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;
import us.dot.its.jpo.geojsonconverter.pojos.geojson.LineString;
import us.dot.its.jpo.geojsonconverter.pojos.geojson.map.ProcessedMap;
import us.dot.its.jpo.geojsonconverter.pojos.spat.ProcessedSpat;

@Getter
@Setter
@Generated
@ToString
public class SpatMap {
    private ProcessedSpat spat;
    private ProcessedMap<LineString> map;

    @JsonCreator
    public SpatMap(
            @JsonProperty("spat") ProcessedSpat spat,
            @JsonProperty("map") ProcessedMap<LineString> map) {
        this.spat = spat;
        this.map = map;
    }
}

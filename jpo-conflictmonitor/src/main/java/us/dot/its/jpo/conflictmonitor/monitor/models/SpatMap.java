package us.dot.its.jpo.conflictmonitor.monitor.models;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;
import us.dot.its.jpo.geojsonconverter.pojos.geojson.LineString;
import us.dot.its.jpo.geojsonconverter.pojos.geojson.map.ProcessedMap;
import us.dot.its.jpo.geojsonconverter.pojos.spat.ProcessedSpat;

/**
 * Container class for pairing a processed SPaT message with its corresponding MAP message.
 * Used to associate signal phase and timing data with intersection geometry.
 */
@Getter
@Setter
@Generated
@ToString
public class SpatMap {

    /** The processed SPaT (Signal Phase and Timing) message. */
    private ProcessedSpat spat;

    /** The processed MAP message containing intersection geometry. */
    private ProcessedMap<LineString> map;

    @JsonCreator
    public SpatMap(
            @JsonProperty("spat") ProcessedSpat spat,
            @JsonProperty("map") ProcessedMap<LineString> map) {
        this.spat = spat;
        this.map = map;
    }
}

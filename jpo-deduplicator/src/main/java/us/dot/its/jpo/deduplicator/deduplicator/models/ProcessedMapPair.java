package us.dot.its.jpo.deduplicator.deduplicator.models;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import us.dot.its.jpo.geojsonconverter.pojos.geojson.LineString;
import us.dot.its.jpo.geojsonconverter.pojos.geojson.map.ProcessedMap;

@NoArgsConstructor
@Setter
@Getter
public class ProcessedMapPair {

    public ProcessedMap<LineString> message;
    public boolean shouldSend;

    public ProcessedMapPair(ProcessedMap<LineString> message, boolean shouldSend){
        this.message = message;
        this.shouldSend = shouldSend;
    }
}

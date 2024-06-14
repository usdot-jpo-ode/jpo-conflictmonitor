package us.dot.its.jpo.deduplicator.deduplicator.models;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import us.dot.its.jpo.geojsonconverter.pojos.geojson.map.ProcessedMap;

@NoArgsConstructor
@Setter
@Getter
public class ProcessedMapWktPair {

    public ProcessedMap<String> message;
    public boolean shouldSend;

    public ProcessedMapWktPair(ProcessedMap<String> message, boolean shouldSend){
        this.message = message;
        this.shouldSend = shouldSend;
    }
}

package us.dot.its.jpo.deduplication.deduplicator.serialization;

import org.apache.kafka.common.serialization.Serde;
import org.apache.kafka.common.serialization.Serdes;

import us.dot.its.jpo.deduplication.deduplicator.models.ProcessedMapPair;
import us.dot.its.jpo.geojsonconverter.pojos.geojson.LineString;
import us.dot.its.jpo.geojsonconverter.pojos.geojson.map.ProcessedMap;
import us.dot.its.jpo.geojsonconverter.serialization.deserializers.JsonDeserializer;
import us.dot.its.jpo.geojsonconverter.serialization.deserializers.ProcessedMapDeserializer;
import us.dot.its.jpo.geojsonconverter.serialization.serializers.JsonSerializer;


public class PairSerdes {
    public static Serde<ProcessedMapPair> ProcessedMapPair() {
        return Serdes.serdeFrom(
            new JsonSerializer<ProcessedMapPair>(), 
            new JsonDeserializer<>(ProcessedMapPair.class));
    }

    // public static Serde<ProcessedMap<LineString>> ProcessedMapGeoJson() {
    //     return Serdes.serdeFrom(
    //         new JsonSerializer<ProcessedMap<LineString>>(), 
    //         new ProcessedMapDeserializer<>(LineString.class));
    // }
}

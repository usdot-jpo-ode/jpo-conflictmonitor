package us.dot.its.jpo.deduplication.deduplicator.serialization;

import org.apache.kafka.common.serialization.Serde;
import org.apache.kafka.common.serialization.Serdes;

import us.dot.its.jpo.deduplication.deduplicator.models.ProcessedMapPair;
import us.dot.its.jpo.deduplication.deduplicator.models.OdeMapPair;
import us.dot.its.jpo.deduplication.deduplicator.models.JsonPair;
import us.dot.its.jpo.deduplication.deduplicator.models.Pair;
import us.dot.its.jpo.geojsonconverter.pojos.geojson.map.DeserializedRawMap;
import us.dot.its.jpo.geojsonconverter.serialization.deserializers.JsonDeserializer;
import us.dot.its.jpo.geojsonconverter.serialization.serializers.JsonSerializer;


public class PairSerdes {
    public static Serde<ProcessedMapPair> ProcessedMapPair() {
        return Serdes.serdeFrom(
            new JsonSerializer<ProcessedMapPair>(), 
            new JsonDeserializer<>(ProcessedMapPair.class));
    }

    public static Serde<OdeMapPair> OdeMapPair() {
        return Serdes.serdeFrom(
            new JsonSerializer<OdeMapPair>(), 
            new JsonDeserializer<>(OdeMapPair.class));
    }

    public static Serde<Pair> Pair() {
        return Serdes.serdeFrom(
            new JsonSerializer<Pair>(), 
            new JsonDeserializer<>(Pair.class));
    }

    public static Serde<DeserializedRawMap> RawMap() {
        return Serdes.serdeFrom(
            new JsonSerializer<DeserializedRawMap>(), 
            new JsonDeserializer<>(DeserializedRawMap.class));
    }

    public static Serde<JsonPair> JsonPair() {
        return Serdes.serdeFrom(
            new JsonSerializer<JsonPair>(), 
            new JsonDeserializer<>(JsonPair.class));
    }

    // public static Serde<ProcessedMap<LineString>> ProcessedMapGeoJson() {
    //     return Serdes.serdeFrom(
    //         new JsonSerializer<ProcessedMap<LineString>>(), 
    //         new ProcessedMapDeserializer<>(LineString.class));
    // }
}

package us.dot.its.jpo.deduplicator.deduplicator.serialization;

import org.apache.kafka.common.serialization.Serde;
import org.apache.kafka.common.serialization.Serdes;

import us.dot.its.jpo.deduplicator.deduplicator.models.ProcessedMapPair;
import us.dot.its.jpo.deduplicator.deduplicator.models.ProcessedMapWktPair;
import us.dot.its.jpo.deduplicator.deduplicator.models.OdeMapPair;
import us.dot.its.jpo.deduplicator.deduplicator.models.OdeBsmPair;
import us.dot.its.jpo.deduplicator.deduplicator.models.JsonPair;
import us.dot.its.jpo.geojsonconverter.pojos.geojson.map.DeserializedRawMap;
import us.dot.its.jpo.geojsonconverter.serialization.deserializers.JsonDeserializer;
import us.dot.its.jpo.geojsonconverter.serialization.serializers.JsonSerializer;


public class PairSerdes {
    public static Serde<ProcessedMapPair> ProcessedMapPair() {
        return Serdes.serdeFrom(
            new JsonSerializer<ProcessedMapPair>(), 
            new JsonDeserializer<>(ProcessedMapPair.class));
    }

    public static Serde<ProcessedMapWktPair> ProcessedMapWktPair() {
        return Serdes.serdeFrom(
            new JsonSerializer<ProcessedMapWktPair>(), 
            new JsonDeserializer<>(ProcessedMapWktPair.class));
    }

    public static Serde<OdeMapPair> OdeMapPair() {
        return Serdes.serdeFrom(
            new JsonSerializer<OdeMapPair>(), 
            new JsonDeserializer<>(OdeMapPair.class));
    }

    public static Serde<OdeBsmPair> OdeBsmPair() {
        return Serdes.serdeFrom(
            new JsonSerializer<OdeBsmPair>(), 
            new JsonDeserializer<>(OdeBsmPair.class));
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
}

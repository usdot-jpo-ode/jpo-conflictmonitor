package us.dot.its.jpo.deduplicator.deduplicator.serialization;

import org.apache.kafka.common.serialization.Serde;
import org.apache.kafka.common.serialization.Serdes;

import com.fasterxml.jackson.databind.JsonNode;

import us.dot.its.jpo.geojsonconverter.serialization.deserializers.JsonDeserializer;
import us.dot.its.jpo.geojsonconverter.serialization.serializers.JsonSerializer;
import us.dot.its.jpo.ode.model.OdeTimData;

public class JsonSerdes {
    public static Serde<OdeTimData> Tim() {
        return Serdes.serdeFrom(
            new JsonSerializer<OdeTimData>(), 
            new JsonDeserializer<>(OdeTimData.class));
    }

    public static Serde<JsonNode> JSON(){
        return Serdes.serdeFrom(
            new JsonSerializer<JsonNode>(), 
            new JsonDeserializer<>(JsonNode.class));
    }
}

package us.dot.its.jpo.deduplicator.deduplicator.models;

import com.fasterxml.jackson.databind.JsonNode;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
@Setter
@Getter
public class JsonPair {

    public JsonNode message;
    public boolean shouldSend;

    public JsonPair(JsonNode message, boolean shouldSend){
        this.message = message;
        this.shouldSend = shouldSend;
    }
}

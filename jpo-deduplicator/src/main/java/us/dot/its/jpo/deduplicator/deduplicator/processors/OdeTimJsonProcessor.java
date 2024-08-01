package us.dot.its.jpo.deduplicator.deduplicator.processors;

import java.time.Duration;
import java.time.Instant;
import java.time.format.DateTimeFormatter;

import com.fasterxml.jackson.databind.JsonNode;

public class OdeTimJsonProcessor extends DeduplicationProcessor<JsonNode>{

    DateTimeFormatter formatter = DateTimeFormatter.ISO_INSTANT;

    public OdeTimJsonProcessor(String storeName){
        this.storeName = storeName;
    }


    @Override
    public Instant getMessageTime(JsonNode message) {
        try {
            String time = message.get("metadata").get("odeReceivedAt").asText();
            return Instant.from(formatter.parse(time));
        } catch (Exception e) {
            return Instant.ofEpochMilli(0);
        }
    }

    @Override
    public boolean isDuplicate(JsonNode lastMessage, JsonNode newMessage) {
        Instant oldValueTime = getMessageTime(lastMessage);
        Instant newValueTime = getMessageTime(newMessage);

        if(newValueTime.minus(Duration.ofMinutes(1)).isAfter(oldValueTime)){
            return false;
        }
        return true;
    }
}

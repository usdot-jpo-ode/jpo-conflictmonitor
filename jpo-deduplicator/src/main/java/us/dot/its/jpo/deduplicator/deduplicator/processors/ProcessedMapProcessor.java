package us.dot.its.jpo.deduplicator.deduplicator.processors;

import java.time.Duration;
import java.time.Instant;
import java.time.ZonedDateTime;
import java.util.Objects;

import us.dot.its.jpo.deduplicator.DeduplicatorProperties;
import us.dot.its.jpo.geojsonconverter.pojos.geojson.LineString;
import us.dot.its.jpo.geojsonconverter.pojos.geojson.map.ProcessedMap;

public class ProcessedMapProcessor extends DeduplicationProcessor<ProcessedMap<LineString>>{

    DeduplicatorProperties props;

    public ProcessedMapProcessor(DeduplicatorProperties props){
        this.props = props;
        this.storeName = props.getKafkaStateStoreProcessedMapName();
    }


    @Override
    public Instant getMessageTime(ProcessedMap<LineString> message) {
        try {
            return message.getProperties().getOdeReceivedAt().toInstant();
        } catch (Exception e) {
            return Instant.ofEpochMilli(0);
        }
    }

    @Override
    public boolean isDuplicate(ProcessedMap<LineString> lastMessage, ProcessedMap<LineString> newMessage) {

        Instant newValueTime = getMessageTime(newMessage);
        Instant oldValueTime = getMessageTime(lastMessage);
        
        if(newValueTime.minus(Duration.ofHours(1)).isAfter(oldValueTime)){
            return false;
        }else{
            ZonedDateTime newValueTimestamp = newMessage.getProperties().getTimeStamp();
            ZonedDateTime newValueOdeReceivedAt = newMessage.getProperties().getOdeReceivedAt();

            newMessage.getProperties().setTimeStamp(lastMessage.getProperties().getTimeStamp());
            newMessage.getProperties().setOdeReceivedAt(lastMessage.getProperties().getOdeReceivedAt());

            int oldHash = Objects.hash(lastMessage.toString());
            int newHash = Objects.hash(newMessage.toString());

            if(oldHash != newHash){
                newMessage.getProperties().setTimeStamp(newValueTimestamp);
                newMessage.getProperties().setOdeReceivedAt(newValueOdeReceivedAt);
                return false;
            }
        }
        return true;   
    }
}

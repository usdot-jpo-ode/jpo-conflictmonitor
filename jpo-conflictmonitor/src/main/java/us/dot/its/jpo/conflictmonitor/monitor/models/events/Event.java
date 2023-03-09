package us.dot.its.jpo.conflictmonitor.monitor.models.events;

import java.time.ZonedDateTime;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import us.dot.its.jpo.geojsonconverter.DateJsonMapper;

import lombok.EqualsAndHashCode;
import lombok.Generated;
import lombok.Getter;

@Getter
@EqualsAndHashCode
@Generated
public abstract class Event {
    
    private long eventGeneratedAt = ZonedDateTime.now().toInstant().toEpochMilli();
    private String eventType;

    public Event(String eventType){
        this.eventType = eventType;
    }

   
    @Override
    public String toString() {
        ObjectMapper mapper = DateJsonMapper.getInstance();
        String testReturn = "";
        try {
            testReturn = (mapper.writeValueAsString(this));
        } catch (JsonProcessingException e) {
            System.out.println(e);
        }
        return testReturn;
    }
}

package us.dot.its.jpo.conflictmonitor.monitor.models.events;

import java.time.ZonedDateTime;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import us.dot.its.jpo.geojsonconverter.DateJsonMapper;

public abstract class Event {
    
    private long eventGeneratedAt;
    private String eventType = "";

    public String getEventType() {
        return eventType;
    }

    public void setEventType(String eventType) {
        this.eventType = eventType;
    }

    public Event(){
        this.eventGeneratedAt = ZonedDateTime.now().toInstant().toEpochMilli();
    }

    public Event(String eventType){
        this.eventGeneratedAt = ZonedDateTime.now().toInstant().toEpochMilli();
        this.eventType = eventType;
    }

    public long getEventGeneratedAt() {
        return eventGeneratedAt;
    }

    public void setEventGeneratedAt(long eventGeneratedAt) {
        this.eventGeneratedAt = eventGeneratedAt;
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

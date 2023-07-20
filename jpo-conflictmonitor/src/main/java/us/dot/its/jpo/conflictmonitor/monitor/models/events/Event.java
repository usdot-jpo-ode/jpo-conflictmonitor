package us.dot.its.jpo.conflictmonitor.monitor.models.events;

import java.time.ZonedDateTime;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import us.dot.its.jpo.geojsonconverter.DateJsonMapper;

import lombok.EqualsAndHashCode;
import lombok.Generated;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@EqualsAndHashCode
@Generated
public abstract class Event {

    private static final Logger logger = LoggerFactory.getLogger(Event.class);
    
    private long eventGeneratedAt = ZonedDateTime.now().toInstant().toEpochMilli();
    private String eventType;

    private int intersectionID;
    private int roadRegulatorID;
    

    public Event(String eventType){
        this.eventType = eventType;
    }


    @Override
    public String toString() {
        try {
            return DateJsonMapper.getInstance().writeValueAsString(this);
        } catch (JsonProcessingException e) {
            logger.error(String.format("Exception serializing %s Event to JSON", eventType), e);
        }
        return "";
    }
}

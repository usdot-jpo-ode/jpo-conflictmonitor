package us.dot.its.jpo.conflictmonitor.monitor.models.events;

import java.time.ZonedDateTime;

import lombok.Data;
import lombok.Generated;

@Data
@Generated
public abstract class Event {
    
    private long eventGeneratedAt;

    public Event(){
        this.eventGeneratedAt = ZonedDateTime.now().toInstant().toEpochMilli();
    }

}

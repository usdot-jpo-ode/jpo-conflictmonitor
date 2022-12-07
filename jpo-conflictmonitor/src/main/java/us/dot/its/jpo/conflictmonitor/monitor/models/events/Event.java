package us.dot.its.jpo.conflictmonitor.monitor.models.events;

import java.time.ZonedDateTime;

public abstract class Event {
    
    private long eventGeneratedAt;

    public Event(){
        this.eventGeneratedAt = ZonedDateTime.now().toInstant().toEpochMilli();
    }

    public long getEventGeneratedAt() {
        return eventGeneratedAt;
    }

    public void setEventGeneratedAt(long eventGeneratedAt) {
        this.eventGeneratedAt = eventGeneratedAt;
    }
}

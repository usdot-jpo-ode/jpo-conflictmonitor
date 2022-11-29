package us.dot.its.jpo.conflictmonitor.monitor.models.events;

import java.time.ZonedDateTime;

public abstract class IntersectionEvent {
    
    private long eventGeneratedAt;

    public IntersectionEvent(){
        this.eventGeneratedAt = ZonedDateTime.now().toInstant().toEpochMilli();
    }

    public long getEventGeneratedAt() {
        return eventGeneratedAt;
    }

    public void setEventGeneratedAt(long eventGeneratedAt) {
        this.eventGeneratedAt = eventGeneratedAt;
    }
}

package us.dot.its.jpo.conflictmonitor.monitor.models.assessments;

import java.time.ZonedDateTime;

public abstract class Assessment {
    
    private long assessmentGeneratedAt;

    public Assessment(){
        this.assessmentGeneratedAt = ZonedDateTime.now().toInstant().toEpochMilli();
    }

    public long getEventGeneratedAt() {
        return assessmentGeneratedAt;
    }

    public void setEventGeneratedAt(long assessmentGeneratedAt) {
        this.assessmentGeneratedAt = assessmentGeneratedAt;
    }
}

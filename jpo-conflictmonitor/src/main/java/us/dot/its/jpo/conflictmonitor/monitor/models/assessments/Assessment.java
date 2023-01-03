package us.dot.its.jpo.conflictmonitor.monitor.models.assessments;

import java.time.ZonedDateTime;

public abstract class Assessment {
    
    private long assessmentGeneratedAt;

    public Assessment(){
        this.assessmentGeneratedAt = ZonedDateTime.now().toInstant().toEpochMilli();
    }

    public long getAssessmentGeneratedAt() {
        return assessmentGeneratedAt;
    }

    public void setAssessmentGeneratedAt(long assessmentGeneratedAt) {
        this.assessmentGeneratedAt = assessmentGeneratedAt;
    }
}

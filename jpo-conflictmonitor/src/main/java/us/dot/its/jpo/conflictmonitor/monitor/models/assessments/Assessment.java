package us.dot.its.jpo.conflictmonitor.monitor.models.assessments;

import java.time.ZonedDateTime;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import us.dot.its.jpo.geojsonconverter.DateJsonMapper;

public abstract class Assessment {
    
    private long assessmentGeneratedAt;
    private String assessmentType = "";

    public Assessment(){
        this.assessmentGeneratedAt = ZonedDateTime.now().toInstant().toEpochMilli();
    }

    public Assessment(String assessmentType){
        this.assessmentGeneratedAt = ZonedDateTime.now().toInstant().toEpochMilli();
        this.assessmentType = assessmentType;
    }
    

    public String getAssessmentType() {
        return assessmentType;
    }

    public void setAssessmentType(String assessmentType) {
        this.assessmentType = assessmentType;
    }

    public long getAssessmentGeneratedAt() {
        return assessmentGeneratedAt;
    }

    public void setAssessmentGeneratedAt(long assessmentGeneratedAt) {
        this.assessmentGeneratedAt = assessmentGeneratedAt;
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

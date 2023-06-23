package us.dot.its.jpo.conflictmonitor.monitor.models.assessments;

import java.time.ZonedDateTime;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import lombok.Generated;
import lombok.Getter;
import lombok.Setter;
import us.dot.its.jpo.geojsonconverter.DateJsonMapper;

@Getter
@Setter
@Generated
public abstract class Assessment{
    
    private long assessmentGeneratedAt = ZonedDateTime.now().toInstant().toEpochMilli();
    private String assessmentType = "";
    public int intersectionID;
    public int roadRegulatorID;

    

    public Assessment(){
    }

    public Assessment(String assessmentType){
        this.assessmentType = assessmentType;
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

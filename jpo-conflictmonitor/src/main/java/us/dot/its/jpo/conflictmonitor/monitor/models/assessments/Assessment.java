package us.dot.its.jpo.conflictmonitor.monitor.models.assessments;

import java.time.ZonedDateTime;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import lombok.EqualsAndHashCode;
import lombok.Generated;
import lombok.Getter;
import lombok.Setter;
import us.dot.its.jpo.geojsonconverter.DateJsonMapper;
import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;


@JsonTypeInfo(
        use = JsonTypeInfo.Id.NAME,
        include = JsonTypeInfo.As.EXISTING_PROPERTY,
        property = "assessmentType"
)
@JsonSubTypes({
        @JsonSubTypes.Type(value = ConnectionOfTravelAssessment.class, name = "ConnectionOfTravel"),
        @JsonSubTypes.Type(value = LaneDirectionOfTravelAssessment.class, name = "LaneDirectionOfTravel"),
        @JsonSubTypes.Type(value = StopLinePassageAssessment.class, name = "StopLinePassage"),
        @JsonSubTypes.Type(value = StopLineStopAssessment.class, name = "StopLineStop"),
})
@Getter
@Setter
@EqualsAndHashCode
@Generated
public abstract class Assessment{
    
    private long assessmentGeneratedAt = ZonedDateTime.now().toInstant().toEpochMilli();
    private String assessmentType = "";
    public int intersectionID;
    public int roadRegulatorID;
    public String source;

    

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

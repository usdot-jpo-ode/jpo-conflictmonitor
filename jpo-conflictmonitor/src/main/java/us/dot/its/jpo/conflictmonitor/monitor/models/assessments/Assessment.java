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

/**
 * Assessment is an abstract supertype that all of the different assessment types inherit from. Assessments contain the following fields
 */
@JsonTypeInfo(
        use = JsonTypeInfo.Id.NAME,
        include = JsonTypeInfo.As.EXISTING_PROPERTY,
        property = "assessmentType"
)
@JsonSubTypes({
        @JsonSubTypes.Type(value = ConnectionOfTravelAssessment.class, name = "ConnectionOfTravel"),
        @JsonSubTypes.Type(value = LaneDirectionOfTravelAssessment.class, name = "LaneDirectionOfTravel"),
        @JsonSubTypes.Type(value = StopLinePassageAssessment.class, name = "SignalStateEvent"),
        @JsonSubTypes.Type(value = StopLineStopAssessment.class, name = "StopLineStop"),
})
@Getter
@Setter
@EqualsAndHashCode
@Generated
public abstract class Assessment{
    

    /**
     * Auto-generated ZonedDateTime in UTC containing the time at which this assessment object was created
     */
    private long assessmentGeneratedAt = ZonedDateTime.now().toInstant().toEpochMilli();
    
    /**
     * assessmentType: Familiar string what type of assessment this object represents
     */
    private String assessmentType = "";

    /**
     * The intersection identification number this assessment corresponds to. Typically this matches the intersectionID of the event that generated this assessment.
     */
    public int intersectionID;

    /**
     * The road regulator id (region) of the intersection this assessment corresponds to. Typically this matches the roadRegulatorID of the event that generated this assessment.
     */
    public int roadRegulatorID;

    /**
     * The source of this data often used for partitioning. Formats are typically JSON objects like this: { rsuId='192.168.0.1', intersectionId='1234', region='-1'}
     */
    public String source;

    
    /**
     * Default empty Assessment Constructor.
     */
    public Assessment(){}

    /**
     * @param A string representing what type of assessment this object represents
     */
    public Assessment(String assessmentType){
        this.assessmentType = assessmentType;
    }


    /**
     * Converts this Object to a JSON string representation
     * @returns String representing this object
     */
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

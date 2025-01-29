package us.dot.its.jpo.conflictmonitor.monitor.models.assessments;

import java.util.ArrayList;
import java.util.List;

import lombok.Generated;
import lombok.Getter;
import lombok.Setter;


/**
 * LaneDirectionOfTravelAssessment - Assessment object containing a list describing how far vehicles are from the centerline as they drive through a lane. 
 */
@Getter
@Setter
@Generated
public class LaneDirectionOfTravelAssessment extends Assessment{

    /**
     * the time at when this Assessment was generated in utc milliseconds. This is deprecated in favor of the assessmentGeneratedAt in the parent class.
     * @deprecated
     */
    private long timestamp;

    /**
     * list of LaneDirectionOfTravelAssessmentGroups that comprise this assessment.
     */
    private List<LaneDirectionOfTravelAssessmentGroup> laneDirectionOfTravelAssessmentGroup = new ArrayList<>();
    
    public LaneDirectionOfTravelAssessment(){
        super("LaneDirectionOfTravel");
    }

}

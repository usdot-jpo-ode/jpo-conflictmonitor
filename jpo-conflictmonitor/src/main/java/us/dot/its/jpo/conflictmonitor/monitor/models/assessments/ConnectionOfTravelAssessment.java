package us.dot.its.jpo.conflictmonitor.monitor.models.assessments;

import java.util.ArrayList;
import java.util.List;

import lombok.Generated;
import lombok.Getter;
import lombok.Setter;



/**
 * ConnectionOfTravelAssessments - Assessment object containing a list of all the different sets of ingress and egress lanes vehicles have driven through the intersection with. 
 */
@Getter
@Setter
@Generated
public class ConnectionOfTravelAssessment extends Assessment{

    /**
     * long representing the time at when this Assessment was generated. This is deprecated in favor of the assessmentGeneratedAt in the parent class.
     * @deprecated
     */
    private long timestamp;

    /**
     * list of ConnectionOfTravelAssessmentGroups that comprise this assessment.
     * @return 
     */
    private List<ConnectionOfTravelAssessmentGroup> connectionOfTravelAssessmentGroups = new ArrayList<>();

    public ConnectionOfTravelAssessment(){
        super("ConnectionOfTravel");
    }
}

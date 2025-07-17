package us.dot.its.jpo.conflictmonitor.monitor.models.assessments;

import lombok.Generated;
import lombok.Getter;
import lombok.Setter;


import java.util.List;

/**
 * SignalStateAssessment - Assessment object containing a list describing how far vehicles are from the centerline as they drive through a lane. 
 */
@Getter
@Setter
@Generated
public class SignalStateAssessment extends Assessment{

    /**
     * the time at when this Assessment was generated in utc milliseconds. This is deprecated in favor of the assessmentGeneratedAt in the parent class.
     * @deprecated
     */
    private long timestamp;

    /**
     * List of Signal State Assessment Groups that contribute to this assessment
     */
    private List<SignalStateAssessmentGroup> signalStateAssessmentGroup;

    public SignalStateAssessment(){
        super("SignalState");
    }
}

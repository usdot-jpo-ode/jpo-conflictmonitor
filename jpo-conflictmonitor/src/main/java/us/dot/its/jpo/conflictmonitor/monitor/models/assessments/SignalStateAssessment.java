package us.dot.its.jpo.conflictmonitor.monitor.models.assessments;

import lombok.Generated;
import lombok.Getter;
import lombok.Setter;


import java.util.List;

/*
 * This class has been deprecated and should no longer be used. Use StopLinePassageAssessment instead.
 */

@Getter
@Setter
@Generated
@Deprecated
public class SignalStateAssessment extends Assessment{
    private long timestamp;
    private List<SignalStateAssessmentGroup> signalStateAssessmentGroup;

    public SignalStateAssessment(){
        super("SignalState");
    }
}

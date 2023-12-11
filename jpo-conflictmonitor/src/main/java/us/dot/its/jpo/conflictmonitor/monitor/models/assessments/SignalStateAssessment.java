package us.dot.its.jpo.conflictmonitor.monitor.models.assessments;

import lombok.Generated;
import lombok.Getter;
import lombok.Setter;


import java.util.List;


@Getter
@Setter
@Generated
public class SignalStateAssessment extends Assessment{
    private long timestamp;
    private List<SignalStateAssessmentGroup> signalStateAssessmentGroup;

    public SignalStateAssessment(){
        super("SignalState");
    }
}

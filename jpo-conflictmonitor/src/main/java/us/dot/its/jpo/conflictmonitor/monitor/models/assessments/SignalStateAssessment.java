package us.dot.its.jpo.conflictmonitor.monitor.models.assessments;

import lombok.Generated;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.List;

@Document("CmSignalStateAssessment")
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

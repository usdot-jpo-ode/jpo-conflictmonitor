package us.dot.its.jpo.conflictmonitor.monitor.models.assessments;

import lombok.Generated;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.List;

@Document("CmStopLineStopAssessment")
@Getter
@Setter
@Generated
public class StopLineStopAssessment extends Assessment{
    private long timestamp;
    private List<StopLineStopAssessmentGroup> stopLineStopAssessmentGroup;

    public StopLineStopAssessment(){
        super("StopLineStop");
    }
}

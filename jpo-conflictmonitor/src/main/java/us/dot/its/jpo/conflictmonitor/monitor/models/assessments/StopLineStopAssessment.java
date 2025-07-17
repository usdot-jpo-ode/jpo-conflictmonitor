package us.dot.its.jpo.conflictmonitor.monitor.models.assessments;

import lombok.Generated;
import lombok.Getter;
import lombok.Setter;

import java.util.List;


/**
 * Stop Line Stop Assessment - Assessment object containing a list of the percent of time a vehicle stopped for each color of light. 
 */
@Getter
@Setter
@Generated
public class StopLineStopAssessment extends Assessment{

    /**
     * the time at when this Assessment was generated in utc milliseconds. This is deprecated in favor of the assessmentGeneratedAt in the parent class.
     * @deprecated
     */
    private long timestamp;

    /**
     * List of StopLineStopAssessmentGroups that comprise this assessment.
     */
    private List<StopLineStopAssessmentGroup> stopLineStopAssessmentGroup;

    public StopLineStopAssessment(){
        super("StopLineStop");
    }
}
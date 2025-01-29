package us.dot.its.jpo.conflictmonitor.monitor.models.assessments;

import java.util.ArrayList;
import java.util.List;
import com.fasterxml.jackson.annotation.JsonIgnore;

import lombok.Getter;
import lombok.Setter;
import us.dot.its.jpo.conflictmonitor.monitor.models.events.StopLinePassageEvent;


/**
 * Stop Line Passage Assessment - Assessment object containing a list of what color the light was for each signal group where a vehicle passed through the light.  
 */
@Getter
@Setter
public class StopLinePassageAssessment extends Assessment{

    /**
     * the time at when this Assessment was generated in utc milliseconds. This is deprecated in favor of the assessmentGeneratedAt in the parent class.
     * @deprecated
     */
    private long timestamp;

    /**
     * List of StopLinePassageAssessmentGroups that comprise this assessment.
     */
    private List<StopLinePassageAssessmentGroup> signalStateEventAssessmentGroup = new ArrayList<>();

    public StopLinePassageAssessment(){
        super("SignalStateEvent");
    }


    /**
     * Helper function which adds StopLinePassageEvents to the appropriate existing signalStateAssessmentGroup object in the list. A new entry is added to the list if a matching entry doesn't already exist.
     */
    @JsonIgnore
    public StopLinePassageAssessment add(StopLinePassageEvent event){
        if(this.signalStateEventAssessmentGroup == null){
            signalStateEventAssessmentGroup = new ArrayList<>();
        }
        for(StopLinePassageAssessmentGroup group : this.signalStateEventAssessmentGroup){
            if(group.getSignalGroup() == event.getSignalGroup()){
                group.addSignalStateEvent(event);
                return this;
            }
        }
        StopLinePassageAssessmentGroup group = new StopLinePassageAssessmentGroup();
        group.setSignalGroup(event.getSignalGroup());
        group.addSignalStateEvent(event);
        this.signalStateEventAssessmentGroup.add(group);
        return this;
    }

    


    
}

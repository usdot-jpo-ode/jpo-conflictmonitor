package us.dot.its.jpo.conflictmonitor.monitor.models.assessments;

import com.fasterxml.jackson.annotation.JsonIgnore;

import lombok.Getter;
import lombok.Setter;
import us.dot.its.jpo.conflictmonitor.monitor.models.events.StopLinePassageEvent;

/**
 * Stop Line Passage Assessment groups represent the number of vehicles that pass through the light during each light color.
 */
@Getter
@Setter
public class StopLinePassageAssessmentGroup {

    /**
     * The Signal group id in the SPaT message that this assessment corresponds to.
     */
    private int signalGroup;

    /**
     * The number of times a vehicle passed through when the light state was dark. 
     */
    private int darkEvents;

    /**
     * The number of times a vehicle passed through on a red light.
     */
    private int redEvents;

    /**
     * The number of times a vehicle passed through on a yellow light.
     */
    private int yellowEvents;

    /**
     * The number of times a vehicle passed through on a green light.
     */
    private int greenEvents;
    
    @JsonIgnore
    public void addStopLinePassageEvent(StopLinePassageEvent event){
        switch(event.getEventState()){
            case UNAVAILABLE:
                this.darkEvents +=1;
                break;
            case DARK:
                this.darkEvents += 1;
                break;
            case STOP_THEN_PROCEED:
                this.redEvents +=1;
                break;
            case STOP_AND_REMAIN:
                this.redEvents +=1;
                break;
            case PRE_MOVEMENT:
                this.greenEvents +=1;
                break;
            case PERMISSIVE_MOVEMENT_ALLOWED:
                this.greenEvents +=1;
                break;
            case PROTECTED_MOVEMENT_ALLOWED:
                this.greenEvents +=1;
                break;
            case PERMISSIVE_CLEARANCE:
                this.yellowEvents +=1;
                break;
            case PROTECTED_CLEARANCE:
                this.yellowEvents +=1;
                break;
            case CAUTION_CONFLICTING_TRAFFIC:
                this.yellowEvents +=1;
                break;
            default:
                break;
        }
    }

}

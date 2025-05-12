package us.dot.its.jpo.conflictmonitor.monitor.models.assessments;

import com.fasterxml.jackson.annotation.JsonIgnore;

import lombok.Getter;
import lombok.Setter;
import us.dot.its.jpo.conflictmonitor.monitor.models.events.StopLinePassageEvent;

@Getter
@Setter
public class StopLinePassageAssessmentGroup {

    private int signalGroup;
    private int darkEvents;
    private int redEvents;
    private int yellowEvents;
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

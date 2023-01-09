package us.dot.its.jpo.conflictmonitor.monitor.models.assessments;

import com.fasterxml.jackson.annotation.JsonIgnore;

import us.dot.its.jpo.conflictmonitor.monitor.models.events.SignalStateEvent;

public class SignalStateEventAssessmentGroup {

    private int signalGroup;
    private int darkEvents;
    private int redEvents;
    private int yellowEvents;
    private int greenEvents;
    
    public int getSignalGroup() {
        return signalGroup;
    }

    public void setSignalGroup(int signalGroup) {
        this.signalGroup = signalGroup;
    }

    public int getDarkEvents() {
        return darkEvents;
    }

    public void setDarkEvents(int darkEvents) {
        this.darkEvents = darkEvents;
    }

    public int getRedEvents() {
        return redEvents;
    }

    public void setRedEvents(int redEvents) {
        this.redEvents = redEvents;
    }

    public int getYellowEvents() {
        return yellowEvents;
    }

    public void setYellowEvents(int yellowEvents) {
        this.yellowEvents = yellowEvents;
    }

    public int getGreenEvents() {
        return greenEvents;
    }

    public void setGreenEvents(int greenEvents) {
        this.greenEvents = greenEvents;
    }

    @JsonIgnore
    public void addSignalStateEvent(SignalStateEvent event){
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

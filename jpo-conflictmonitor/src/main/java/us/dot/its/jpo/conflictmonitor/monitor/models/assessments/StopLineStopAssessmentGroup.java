package us.dot.its.jpo.conflictmonitor.monitor.models.assessments;

import com.fasterxml.jackson.annotation.JsonIgnore;

import lombok.Getter;
import lombok.Setter;
import us.dot.its.jpo.conflictmonitor.monitor.models.events.StopLineStopEvent;


/**
 * Stop Line Passage Assessment groups represent the amount of time vehicles were stopped at each color of the signal light. 
 */
@Getter
@Setter
public class StopLineStopAssessmentGroup {

    /**
     * The Signal group id in the SPaT message that this assessment corresponds to.
     */
    private int signalGroup;

    /**
     * The number of events that are included in this assessment group.
     */
    private int numberOfEvents;

    /**
     * The amount of time vehicles were stopped at the light when the light was red. 
     */
    private double timeStoppedOnRed;

    /**
     * The amount of time vehicles were stopped at the light when the light was yellow. 
     */
    private double timeStoppedOnYellow;

    /**
     * The amount of time vehicles were stopped at the light when the light was green. 
     */
    private double timeStoppedOnGreen;

    /**
     * The amount of time vehicles were stopped at the light when the light was dark. 
     */
    private double timeStoppedOnDark;
    

    /**
     * Helper function which adds StopLineStopEvents time values to the total time values described in this model.
     */
    @JsonIgnore
    public void addStopLineStopEvent(StopLineStopEvent event){
        numberOfEvents +=1;
        timeStoppedOnRed += event.getTimeStoppedDuringRed();
        timeStoppedOnYellow += event.getTimeStoppedDuringYellow();
        timeStoppedOnGreen += event.getTimeStoppedDuringGreen();
        timeStoppedOnDark += event.getTimeStoppedDuringDark();
    }

}

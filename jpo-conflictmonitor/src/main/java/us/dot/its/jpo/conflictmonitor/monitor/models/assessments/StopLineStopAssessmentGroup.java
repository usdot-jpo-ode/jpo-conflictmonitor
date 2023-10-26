package us.dot.its.jpo.conflictmonitor.monitor.models.assessments;

import com.fasterxml.jackson.annotation.JsonIgnore;

import lombok.Getter;
import lombok.Setter;
import us.dot.its.jpo.conflictmonitor.monitor.models.events.StopLinePassageEvent;
import us.dot.its.jpo.conflictmonitor.monitor.models.events.StopLineStopEvent;

@Getter
@Setter
public class StopLineStopAssessmentGroup {

    private int signalGroup;
    private int numberOfEvents;
    private double timeStoppedOnRed;
    private double timeStoppedOnYellow;
    private double timeStoppedOnGreen;
    private double timeStoppedOnDark;
    
    @JsonIgnore
    public void addStopLineStopEvent(StopLineStopEvent event){
        numberOfEvents +=1;
        timeStoppedOnRed += event.getTimeStoppedDuringRed();
        timeStoppedOnYellow += event.getTimeStoppedDuringYellow();
        timeStoppedOnGreen += event.getTimeStoppedDuringGreen();
    }

}

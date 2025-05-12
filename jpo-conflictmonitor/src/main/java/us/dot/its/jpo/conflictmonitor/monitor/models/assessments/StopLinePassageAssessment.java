package us.dot.its.jpo.conflictmonitor.monitor.models.assessments;

import java.util.ArrayList;
import java.util.List;
import com.fasterxml.jackson.annotation.JsonIgnore;

import lombok.Getter;
import lombok.Setter;
import us.dot.its.jpo.conflictmonitor.monitor.models.events.StopLinePassageEvent;



@Getter
@Setter
public class StopLinePassageAssessment extends Assessment{
    private long timestamp;
    private List<StopLinePassageAssessmentGroup> stopLinePassageAssessmentGroup = new ArrayList<>();

    public StopLinePassageAssessment(){
        super("StopLinePassage");
    }


    @JsonIgnore
    public StopLinePassageAssessment add(StopLinePassageEvent event){
        if(this.stopLinePassageAssessmentGroup == null){
            stopLinePassageAssessmentGroup = new ArrayList<>();
        }
        for(StopLinePassageAssessmentGroup group : this.stopLinePassageAssessmentGroup){
            if(group.getSignalGroup() == event.getSignalGroup()){
                group.addStopLinePassageEvent(event);
                return this;
            }
        }
        StopLinePassageAssessmentGroup group = new StopLinePassageAssessmentGroup();
        group.setSignalGroup(event.getSignalGroup());
        group.addStopLinePassageEvent(event);
        this.stopLinePassageAssessmentGroup.add(group);
        return this;
    }

}

package us.dot.its.jpo.conflictmonitor.monitor.models.assessments;

import java.util.ArrayList;
import java.util.List;
import com.fasterxml.jackson.annotation.JsonIgnore;

import lombok.Getter;
import lombok.Setter;
import us.dot.its.jpo.conflictmonitor.monitor.models.events.StopLinePassageEvent;
import org.springframework.data.mongodb.core.mapping.Document;

@Document("CmSignalStateEventAssessment")
@Getter
@Setter
public class StopLinePassageAssessment extends Assessment{
    private long timestamp;
    private List<StopLinePassageAssessmentGroup> signalStateEventAssessmentGroup = new ArrayList<>();

    public StopLinePassageAssessment(){
        super("SignalStateEvent");
    }



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

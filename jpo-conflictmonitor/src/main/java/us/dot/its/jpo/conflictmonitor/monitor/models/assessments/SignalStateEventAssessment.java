package us.dot.its.jpo.conflictmonitor.monitor.models.assessments;

import java.util.ArrayList;
import java.util.List;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import lombok.Generated;
import lombok.Getter;
import lombok.Setter;
import us.dot.its.jpo.conflictmonitor.monitor.models.events.SignalStateEvent;
import us.dot.its.jpo.geojsonconverter.DateJsonMapper;
import org.springframework.data.mongodb.core.mapping.Document;

@Document("CmSignalStateEventAssessment")
@Getter
@Setter
public class SignalStateEventAssessment extends Assessment{
    private long timestamp;
    private List<SignalStateEventAssessmentGroup> signalStateEventAssessmentGroup = new ArrayList<>();

    public SignalStateEventAssessment(){
        super("SignalStateEvent");
    }



    @JsonIgnore
    public SignalStateEventAssessment add(SignalStateEvent event){
        if(this.signalStateEventAssessmentGroup == null){
            signalStateEventAssessmentGroup = new ArrayList<>();
        }
        for(SignalStateEventAssessmentGroup group : this.signalStateEventAssessmentGroup){
            if(group.getSignalGroup() == event.getSignalGroup()){
                group.addSignalStateEvent(event);
                return this;
            }
        }
        SignalStateEventAssessmentGroup group = new SignalStateEventAssessmentGroup();
        group.setSignalGroup(event.getSignalGroup());
        group.addSignalStateEvent(event);
        this.signalStateEventAssessmentGroup.add(group);
        return this;
    }


    
}

package us.dot.its.jpo.conflictmonitor.monitor.models.assessments;

import java.util.ArrayList;
import java.util.List;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import us.dot.its.jpo.conflictmonitor.monitor.models.events.SignalStateEvent;
import us.dot.its.jpo.geojsonconverter.DateJsonMapper;

public class SignalStateEventAssessment extends Assessment{
    private long timestamp;
    private List<SignalStateEventAssessmentGroup> signalStateEventAssessmentGroup = new ArrayList<>();

    public long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }

    public List<SignalStateEventAssessmentGroup> getSignalStateAssessmentGroup() {
        return signalStateEventAssessmentGroup;
    }

    public void setSignalStateAssessmentGroup(List<SignalStateEventAssessmentGroup> signalStateEventAssessmentGroup) {
        this.signalStateEventAssessmentGroup = signalStateEventAssessmentGroup;
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

    @Override
    public String toString() {
        ObjectMapper mapper = DateJsonMapper.getInstance();
        String testReturn = "";
        try {
            testReturn = (mapper.writeValueAsString(this));
        } catch (JsonProcessingException e) {
            System.out.println(e);
        }
        return testReturn;
    }
    
}

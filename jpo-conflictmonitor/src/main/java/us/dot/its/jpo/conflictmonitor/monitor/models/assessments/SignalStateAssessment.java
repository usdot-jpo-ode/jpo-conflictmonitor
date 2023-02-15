package us.dot.its.jpo.conflictmonitor.monitor.models.assessments;

import java.util.List;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import us.dot.its.jpo.geojsonconverter.DateJsonMapper;
import org.springframework.data.mongodb.core.mapping.Document;
@Document("CmSignalStateAssessment")
public class SignalStateAssessment extends Assessment{
    private long timestamp;
    private List<SignalStateAssessmentGroup> signalStateAssessmentGroup;

    public SignalStateAssessment(){
        super("SignalState");
    }

    public long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }

    public List<SignalStateAssessmentGroup> getSignalStateAssessmentGroup() {
        return signalStateAssessmentGroup;
    }

    public void setSignalStateAssessmentGroup(List<SignalStateAssessmentGroup> signalStateAssessmentGroup) {
        this.signalStateAssessmentGroup = signalStateAssessmentGroup;
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

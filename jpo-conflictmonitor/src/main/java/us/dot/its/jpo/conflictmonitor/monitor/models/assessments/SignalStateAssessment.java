package us.dot.its.jpo.conflictmonitor.monitor.models.assessments;

import java.util.List;

public class SignalStateAssessment extends Assessment{
    private int timestamp;
    private List<SignalStateAssessmentGroup> signalStateAssessmentGroup;

    public int getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(int timestamp) {
        this.timestamp = timestamp;
    }

    public List<SignalStateAssessmentGroup> getSignalStateAssessmentGroup() {
        return signalStateAssessmentGroup;
    }

    public void setSignalStateAssessmentGroup(List<SignalStateAssessmentGroup> signalStateAssessmentGroup) {
        this.signalStateAssessmentGroup = signalStateAssessmentGroup;
    }

    @Override
    public String toString(){
        return "Signal State Assessment";
    }
}

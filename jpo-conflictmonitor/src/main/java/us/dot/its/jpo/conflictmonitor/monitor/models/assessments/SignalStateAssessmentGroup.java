package us.dot.its.jpo.conflictmonitor.monitor.models.assessments;

public class SignalStateAssessmentGroup {

    private int signalGroup;
    private int redEvents;
    private int yellowEvents;
    private int greenEvents;

    public int getSignalGroup() {
        return signalGroup;
    }

    public void setSignalGroup(int signalGroup) {
        this.signalGroup = signalGroup;
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

}

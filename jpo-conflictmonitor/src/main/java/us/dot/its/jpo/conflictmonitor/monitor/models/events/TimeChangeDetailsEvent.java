package us.dot.its.jpo.conflictmonitor.monitor.models.events;

enum TimeMarkType {
    MIN_END_TIME,
    MAX_END_TIME,
}

public class TimeChangeDetailsEvent extends Event{

    private int roadRegulatorID;
    private int intersectionID;
    private int signalGroup;
    private int firstSpatTimestamp;
    private int secondSpatTimestamp;
    private TimeMarkType firstTimeMarkType;
    private TimeMarkType secondTimeMarkType;
    private int firstConflictingTimemark;
    private int secondConflictingTimemark;

    public int getRoadRegulatorID() {
        return roadRegulatorID;
    }

    public void setRoadRegulatorID(int roadRegulatorID) {
        this.roadRegulatorID = roadRegulatorID;
    }

    public int getIntersectionID() {
        return intersectionID;
    }

    public void setIntersectionID(int intersectionID) {
        this.intersectionID = intersectionID;
    }

    public int getSignalGroup() {
        return signalGroup;
    }

    public void setSignalGroup(int signalGroup) {
        this.signalGroup = signalGroup;
    }

    public int getFirstSpatTimestamp() {
        return firstSpatTimestamp;
    }

    public void setFirstSpatTimestamp(int firstSpatTimestamp) {
        this.firstSpatTimestamp = firstSpatTimestamp;
    }

    public int getSecondSpatTimestamp() {
        return secondSpatTimestamp;
    }

    public void setSecondSpatTimestamp(int secondSpatTimestamp) {
        this.secondSpatTimestamp = secondSpatTimestamp;
    }

    public TimeMarkType getFirstTimeMarkType() {
        return firstTimeMarkType;
    }

    public void setFirstTimeMarkType(TimeMarkType firstTimeMarkType) {
        this.firstTimeMarkType = firstTimeMarkType;
    }

    public TimeMarkType getSecondTimeMarkType() {
        return secondTimeMarkType;
    }

    public void setSecondTimeMarkType(TimeMarkType secondTimeMarkType) {
        this.secondTimeMarkType = secondTimeMarkType;
    }

    public int getFirstConflictingTimemark() {
        return firstConflictingTimemark;
    }

    public void setFirstConflictingTimemark(int firstConflictingTimemark) {
        this.firstConflictingTimemark = firstConflictingTimemark;
    }

    public int getSecondConflictingTimemark() {
        return secondConflictingTimemark;
    }

    public void setSecondConflictingTimemark(int secondConflictingTimemark) {
        this.secondConflictingTimemark = secondConflictingTimemark;
    }
}

package us.dot.its.jpo.conflictmonitor.monitor.models.events;

enum TimeMarkType {
    MIN_END_TIME,
    MAX_END_TIME,
}

public class TimeChangeDetailsEvent extends Event{

    private int roadRegulatorID;
    private int intersectionID;
    private int signalGroup;
    private long firstSpatTimestamp;
    private long secondSpatTimestamp;
    private long firstTimeMarkType;
    private long secondTimeMarkType;
    private long firstConflictingTimemark;
    private long secondConflictingTimemark;

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

    public long getFirstSpatTimestamp() {
        return firstSpatTimestamp;
    }

    public void setFirstSpatTimestamp(long firstSpatTimestamp) {
        this.firstSpatTimestamp = firstSpatTimestamp;
    }

    public long getSecondSpatTimestamp() {
        return secondSpatTimestamp;
    }

    public void setSecondSpatTimestamp(long secondSpatTimestamp) {
        this.secondSpatTimestamp = secondSpatTimestamp;
    }

    public long getFirstTimeMarkType() {
        return firstTimeMarkType;
    }

    public void setFirstTimeMarkType(long firstTimeMarkType) {
        this.firstTimeMarkType = firstTimeMarkType;
    }

    public long getSecondTimeMarkType() {
        return secondTimeMarkType;
    }

    public void setSecondTimeMarkType(long secondTimeMarkType) {
        this.secondTimeMarkType = secondTimeMarkType;
    }

    public long getFirstConflictingTimemark() {
        return firstConflictingTimemark;
    }

    public void setFirstConflictingTimemark(long firstConflictingTimemark) {
        this.firstConflictingTimemark = firstConflictingTimemark;
    }

    public long getSecondConflictingTimemark() {
        return secondConflictingTimemark;
    }

    public void setSecondConflictingTimemark(long secondConflictingTimemark) {
        this.secondConflictingTimemark = secondConflictingTimemark;
    }
}

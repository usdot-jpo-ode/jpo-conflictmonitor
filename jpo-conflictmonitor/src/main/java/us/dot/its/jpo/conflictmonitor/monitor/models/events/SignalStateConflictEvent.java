package us.dot.its.jpo.conflictmonitor.monitor.models.events;

enum ConflictType {
    PROTECTED,
    PERMISSIVE,
}

public class SignalStateConflictEvent {
    private int timestamp;
    private int roadRegulatorID;
    private int intersectionID;
    private ConflictType conflictType;
    private int firstConflictingSignalGroup;
    private int firstConflictingSignalState;
    private int secondConflictingSignalGroup;
    private int secondConflictingSignalState;

    public int getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(int timestamp) {
        this.timestamp = timestamp;
    }

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

    public ConflictType getConflictType() {
        return conflictType;
    }

    public void setConflictType(ConflictType conflictType) {
        this.conflictType = conflictType;
    }

    public int getFirstConflictingSignalGroup() {
        return firstConflictingSignalGroup;
    }

    public void setFirstConflictingSignalGroup(int firstConflictingSignalGroup) {
        this.firstConflictingSignalGroup = firstConflictingSignalGroup;
    }

    public int getFirstConflictingSignalState() {
        return firstConflictingSignalState;
    }

    public void setFirstConflictingSignalState(int firstConflictingSignalState) {
        this.firstConflictingSignalState = firstConflictingSignalState;
    }

    public int getSecondConflictingSignalGroup() {
        return secondConflictingSignalGroup;
    }

    public void setSecondConflictingSignalGroup(int secondConflictingSignalGroup) {
        this.secondConflictingSignalGroup = secondConflictingSignalGroup;
    }

    public int getSecondConflictingSignalState() {
        return secondConflictingSignalState;
    }

    public void setSecondConflictingSignalState(int secondConflictingSignalState) {
        this.secondConflictingSignalState = secondConflictingSignalState;
    }
}

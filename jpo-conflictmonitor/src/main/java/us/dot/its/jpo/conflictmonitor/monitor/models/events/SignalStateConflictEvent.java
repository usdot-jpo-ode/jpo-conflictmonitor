package us.dot.its.jpo.conflictmonitor.monitor.models.events;

import us.dot.its.jpo.ode.plugin.j2735.J2735MovementPhaseState;

public class SignalStateConflictEvent extends Event{
    private long timestamp;
    private int roadRegulatorID;
    private int intersectionID;
    private J2735MovementPhaseState conflictType;
    private int firstConflictingSignalGroup;
    private J2735MovementPhaseState firstConflictingSignalState;
    private int secondConflictingSignalGroup;
    private J2735MovementPhaseState secondConflictingSignalState;

    public long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(long timestamp) {
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

    public J2735MovementPhaseState getConflictType() {
        return conflictType;
    }

    public void setConflictType(J2735MovementPhaseState conflictType) {
        this.conflictType = conflictType;
    }

    public int getFirstConflictingSignalGroup() {
        return firstConflictingSignalGroup;
    }

    public void setFirstConflictingSignalGroup(int firstConflictingSignalGroup) {
        this.firstConflictingSignalGroup = firstConflictingSignalGroup;
    }

    public J2735MovementPhaseState getFirstConflictingSignalState() {
        return firstConflictingSignalState;
    }

    public void setFirstConflictingSignalState(J2735MovementPhaseState firstConflictingSignalState) {
        this.firstConflictingSignalState = firstConflictingSignalState;
    }

    public int getSecondConflictingSignalGroup() {
        return secondConflictingSignalGroup;
    }

    public void setSecondConflictingSignalGroup(int secondConflictingSignalGroup) {
        this.secondConflictingSignalGroup = secondConflictingSignalGroup;
    }

    public J2735MovementPhaseState getSecondConflictingSignalState() {
        return secondConflictingSignalState;
    }

    public void setSecondConflictingSignalState(J2735MovementPhaseState secondConflictingSignalState) {
        this.secondConflictingSignalState = secondConflictingSignalState;
    }
}

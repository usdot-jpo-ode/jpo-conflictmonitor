package us.dot.its.jpo.conflictmonitor.monitor.models.events;

import java.util.Set;

public class SignalGroupAlignmentEvent extends Event{
    String sourceID;
    long timestamp;
    Set<Integer> spatSignalGroupIds;
    Set<Integer> mapSignalGroupIds;

    
    public String getSourceID() {
        return sourceID;
    }

    public void setSourceID(String sourceID) {
        this.sourceID = sourceID;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }

    public Set<Integer> getSpatSignalGroupIds() {
        return spatSignalGroupIds;
    }

    public void setSpatSignalGroupIds(Set<Integer> spatSignalGroupIds) {
        this.spatSignalGroupIds = spatSignalGroupIds;
    }

    public Set<Integer> getMapSignalGroupIds() {
        return mapSignalGroupIds;
    }

    public void setMapSignalGroupIds(Set<Integer> mapSignalGroupIds) {
        this.mapSignalGroupIds = mapSignalGroupIds;
    }

}

package us.dot.its.jpo.conflictmonitor.monitor.models.events;

import java.util.Set;

public class IntersectionReferenceAlignmentEvent extends Event{
    
    private String sourceID;
    private long timestamp;
    private Set<Integer> spatRoadRegulatorIds;
    private Set<Integer> mapRoadRegulatorIds;
    private Set<Integer> spatIntersectionIds;
    private Set<Integer> mapIntersectionIds;

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
    
    public Set<Integer> getSpatRoadRegulatorIds() {
        return spatRoadRegulatorIds;
    }

    public void setSpatRoadRegulatorIds(Set<Integer> spatRoadRegulatorIds) {
        this.spatRoadRegulatorIds = spatRoadRegulatorIds;
    }
    
    public Set<Integer> getMapRoadRegulatorIds() {
        return mapRoadRegulatorIds;
    }

    public void setMapRoadRegulatorIds(Set<Integer> mapRoadRegulatorIds) {
        this.mapRoadRegulatorIds = mapRoadRegulatorIds;
    }
    
    public Set<Integer> getSpatIntersectionIds() {
        return spatIntersectionIds;
    }

    public void setSpatIntersectionIds(Set<Integer> spatIntersectionIds) {
        this.spatIntersectionIds = spatIntersectionIds;
    }
    
    public Set<Integer> getMapIntersectionIds() {
        return mapIntersectionIds;
    }

    public void setMapIntersectionIds(Set<Integer> mapIntersectionIds) {
        this.mapIntersectionIds = mapIntersectionIds;
    }
}

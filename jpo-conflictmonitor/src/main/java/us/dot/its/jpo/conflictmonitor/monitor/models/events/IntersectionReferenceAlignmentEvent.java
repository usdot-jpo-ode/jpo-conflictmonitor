package us.dot.its.jpo.conflictmonitor.monitor.models.events;

import java.util.Set;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import us.dot.its.jpo.geojsonconverter.DateJsonMapper;
import org.springframework.data.mongodb.core.mapping.Document;


@Document("CmIntersectionReferenceAlignmentEvent")
public class IntersectionReferenceAlignmentEvent extends Event{
    
    private String sourceID;
    private long timestamp;
    private Set<Integer> spatRoadRegulatorIds;
    private Set<Integer> mapRoadRegulatorIds;
    private Set<Integer> spatIntersectionIds;
    private Set<Integer> mapIntersectionIds;

    

    public IntersectionReferenceAlignmentEvent(){
        super("IntersectionReferenceAlignment");
    }

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

    @Override
    public boolean equals(Object o) {
        if (o == this)
            return true;
        if (!(o instanceof SignalStateEvent)) {
            return false;
        }
        
        IntersectionReferenceAlignmentEvent intersectionReferenceAlignmentEvent = (IntersectionReferenceAlignmentEvent) o;
        return 
            sourceID.equals(intersectionReferenceAlignmentEvent.sourceID) &&
            timestamp == intersectionReferenceAlignmentEvent.timestamp &&
            spatRoadRegulatorIds.equals(intersectionReferenceAlignmentEvent.spatRoadRegulatorIds) &&
            mapRoadRegulatorIds.equals(intersectionReferenceAlignmentEvent.mapRoadRegulatorIds) &&
            spatIntersectionIds.equals(intersectionReferenceAlignmentEvent.spatIntersectionIds) &&
            mapIntersectionIds.equals(intersectionReferenceAlignmentEvent.mapIntersectionIds);
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

    // public String getNotificationSourceString() {
    //     return "Intersection Reference Alignment Event";
    // }

    // public void setNotificationSourceString(String notificationSourceString) {
    //     this.notificationSourceString = notificationSourceString;
    // }
}

package us.dot.its.jpo.conflictmonitor.monitor.models.events;

import java.util.Set;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import us.dot.its.jpo.geojsonconverter.DateJsonMapper;

import org.springframework.data.mongodb.core.mapping.Document;
@Document("CmSignalGroupAlignmentEvent")
public class SignalGroupAlignmentEvent extends Event{
    String sourceID;
    long timestamp;
    Set<Integer> spatSignalGroupIds;
    Set<Integer> mapSignalGroupIds;

    public SignalGroupAlignmentEvent(){
        super("SignalGroupAlignment");
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

    @Override
    public boolean equals(Object o) {
        if (o == this)
            return true;
        if (!(o instanceof SignalStateEvent)) {
            return false;
        }
        SignalGroupAlignmentEvent signalGroupAlignmentEvent = (SignalGroupAlignmentEvent) o;
        return 
            sourceID.equals(signalGroupAlignmentEvent.sourceID) &&
            timestamp == signalGroupAlignmentEvent.timestamp &&
            spatSignalGroupIds.equals(signalGroupAlignmentEvent.spatSignalGroupIds) &&
            mapSignalGroupIds.equals(signalGroupAlignmentEvent.mapSignalGroupIds);
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

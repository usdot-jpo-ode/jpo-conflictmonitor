package us.dot.its.jpo.conflictmonitor.monitor.models.spat;

import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnore;
import us.dot.its.jpo.geojsonconverter.pojos.spat.MovementState;
import us.dot.its.jpo.geojsonconverter.pojos.spat.ProcessedSpat;

public class SpatTimeChangeDetail {
    private String originIP;
    private int region;
    private int intersectionID;
    private long timestamp;
    private List<SpatTimeChangeDetailState> states;

    @JsonIgnore
    public static SpatTimeChangeDetail fromProcessedSpat(ProcessedSpat spat){
        SpatTimeChangeDetail spatTimeChange = new SpatTimeChangeDetail();
        if(spat.getOriginIp() != null){
            spatTimeChange.setOriginIP(spat.getOriginIp());
        }else{
            spatTimeChange.setOriginIP("");
        }

        if(spat.getRegion() != null){
            spatTimeChange.setRegion(spat.getRegion());
        }else{
            spatTimeChange.setRegion(-1);
        }

        if(spat.getIntersectionId() != null){
            spatTimeChange.setIntersectionID(spat.getIntersectionId());
        }else{
            spatTimeChange.setIntersectionID(-1);
        }

        spatTimeChange.setTimestamp(SpatTimestampExtractor.getSpatTimestamp(spat));
        ArrayList<SpatTimeChangeDetailState> states = new ArrayList<>();
        for(MovementState state: spat.getStates()){
            states.add(SpatTimeChangeDetailState.fromMovementState(state));
        }
        spatTimeChange.setStates(states);

        

        return spatTimeChange;
    }

    public String getOriginIP() {
        return originIP;
    }

    public void setOriginIP(String originIP) {
        this.originIP = originIP;
    }

    public int getRegion() {
        return region;
    }

    public void setRegion(int region) {
        this.region = region;
    }

    public int getIntersectionID() {
        return intersectionID;
    }

    public void setIntersectionID(int intersectionID) {
        this.intersectionID = intersectionID;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }

    public List<SpatTimeChangeDetailState> getStates() {
        return states;
    }

    public void setStates(List<SpatTimeChangeDetailState> states) {
        this.states = states;
    }

    @Override
    public String toString(){
        return "Spat Time Change Detail States: " + this.states.size() + " Region: " + this.getRegion() + " Intersection: " + this.getIntersectionID() + " Timestamp: " + this.timestamp;
    }
    
}

// package us.dot.its.jpo.conflictmonitor.monitor.models.map;

// import java.util.ArrayList;
// import java.util.List;

// import com.fasterxml.jackson.annotation.JsonIgnore;
// import com.fasterxml.jackson.databind.ObjectMapper;

// import us.dot.its.jpo.geojsonconverter.pojos.geojson.map.ProcessedMap;


// public class MapTimeChangeDetail {
//     private String originIP;
//     private int region;
//     private int intersectionID;
//     private long timestamp;
//     private List<MapTimeChangeDetailState> states;

//     @JsonIgnore
//     public static MapTimeChangeDetail fromProcessedSpat(ProcessedMap map){
//         MapTimeChangeDetail mapTimeChange = new MapTimeChangeDetail();
//         if(map.getProperties().getOriginIp() != null){
//             mapTimeChange.setOriginIP(map.getProperties().getOriginIp());
//         }else{
//             mapTimeChange.setOriginIP("");
//         }

//         if(map.getProperties().getRegion() != null){
//             mapTimeChange.setRegion(map.getProperties().getRegion());
//         }else{
//             mapTimeChange.setRegion(-1);
//         }

//         if(map.getProperties().getIntersectionId() != null){
//             mapTimeChange.setIntersectionID(map.getProperties().getIntersectionId());
//         }else{
//             mapTimeChange.setIntersectionID(-1);
//         }

//         mapTimeChange.setTimestamp(MapTimestampExtractor.getProcessedMapTimestamp(map));
        

        

//         return mapTimeChange;
//     }

//     public String getOriginIP() {
//         return originIP;
//     }

//     public void setOriginIP(String originIP) {
//         this.originIP = originIP;
//     }

//     public int getRegion() {
//         return region;
//     }

//     public void setRegion(int region) {
//         this.region = region;
//     }

//     public int getIntersectionID() {
//         return intersectionID;
//     }

//     public void setIntersectionID(int intersectionID) {
//         this.intersectionID = intersectionID;
//     }

//     public long getTimestamp() {
//         return timestamp;
//     }

//     public void setTimestamp(long timestamp) {
//         this.timestamp = timestamp;
//     }

//     public List<SpatTimeChangeDetailState> getStates() {
//         return states;
//     }

//     public void setStates(List<SpatTimeChangeDetailState> states) {
//         this.states = states;
//     }

//     @Override
//     public String toString(){
//         return "Spat Time Change Detail States: " + this.states.size() + " Region: " + this.getRegion() + " Intersection: " + this.getIntersectionID() + " Timestamp: " + this.timestamp;
//     }
    
// }

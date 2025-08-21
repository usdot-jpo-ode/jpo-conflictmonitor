package us.dot.its.jpo.conflictmonitor.monitor.models.Intersection;

import java.util.*;

import lombok.*;
import us.dot.its.jpo.conflictmonitor.monitor.utils.ProcessedMapUtils;
import us.dot.its.jpo.geojsonconverter.pojos.geojson.LineString;
import us.dot.its.jpo.geojsonconverter.pojos.geojson.connectinglanes.ConnectingLanesFeature;
import us.dot.its.jpo.geojsonconverter.pojos.geojson.map.*;

import org.locationtech.jts.geom.Coordinate;
import org.locationtech.jts.io.WKTWriter;

/*
 * Intersection
 * Intersection is an internal ConflictMonitor data structure primarily used to help parse and perform geospatial evaluation on intersections.
 */
@Getter
@Setter
public class Intersection {
    
    /**
     * List of Lane objects representing all of the ingress lanes to this intersection
     */
    private List<Lane> ingressLanes;

    /**
     * List of lane objects representing all of the egress lanes to this intersection
     */
    private List<Lane> egressLanes;

    /**
     * List of IntersectionLine objects representing all of the places vehicles should stop when entering the intersection
     */
    private List<IntersectionLine> stopLines;

    /**
     * List of IntersectionLine objects representing all of the places vehicles may resume driving when leaving the intersection
     */
    private List<IntersectionLine> startLines;

    /**
     * Coordinate representing the central reference point of the intersection. Take from the MAP message.
     */
    private Coordinate referencePoint;

    /**
     * Integer representing the intersectionId of the intersection
     */
    private Integer intersectionId;

    /**
     * Integer representing the roadRegulatorID of the intersection. Will be removed in a future release
     */
    private Integer roadRegulatorId;

    /**
     * ArrayList of LaneConnection objects describing all of the ways lanes can connect to other lanes within the ConflictMonitor System
     */
    private ArrayList<LaneConnection> laneConnections;
    private Set<Integer> revocableLaneIds;

    /**
     * @param map {@code ProcessedMap<LineString>} object to convert to an intersection object
     * @return an Intersection Object representing the processed MAP
     */
    public static Intersection fromProcessedMap(ProcessedMap<LineString> map){

        Intersection intersection = new Intersection();
        ArrayList<Lane> ingressLanes = new ArrayList<>();
        ArrayList<Lane> egressLanes = new ArrayList<>();
        ArrayList<LaneConnection> laneConnections = new ArrayList<>();
        HashMap<Integer, Lane> laneLookup = new HashMap<>();

        // Get revocable lanes.
        intersection.revocableLaneIds = ProcessedMapUtils.getRevocableLanes(map);

        if (map.getProperties() == null) return intersection;

        intersection.setIntersectionId(map.getProperties().getIntersectionId());
        if(map.getProperties().getRegion() != null){
            intersection.setRoadRegulatorId(map.getProperties().getRegion());
        }else{
            intersection.setRoadRegulatorId(-1);
        }
        intersection.setReferencePoint(new Coordinate(map.getProperties().getRefPoint().getLongitude().doubleValue(), map.getProperties().getRefPoint().getLatitude().doubleValue()));

        int laneWidth = map.getProperties().getLaneWidth();



        MapFeatureCollection<LineString> features = map.getMapFeatureCollection();
        for(MapFeature<LineString> feature: features.getFeatures()){
            MapProperties props = feature.getProperties();
            Lane lane = Lane.fromGeoJsonFeature(feature, intersection.getReferencePoint(), laneWidth);
            if(props.getIngressPath()){
                ingressLanes.add(lane);
            }else{
                egressLanes.add(lane);
            }
            laneLookup.put(lane.getId(), lane);
        }

        // Create a map of Ingress/Egress lane id to connection index from connectingLanesFeatureCollection
        Map<IngressEgress, Integer> connectionIndexMap = new HashMap<>();
        ConnectingLanesFeature<LineString>[] connectingLanes = map.getConnectingLanesFeatureCollection().getFeatures();
        for (int connectionIndex = 0; connectionIndex < connectingLanes.length; ++connectionIndex) {
            var connectingLane = connectingLanes[connectionIndex];
            IngressEgress ingressEgress = new IngressEgress(connectingLane.getProperties().getIngressLaneId(),
                    connectingLane.getProperties().getEgressLaneId());
            connectionIndexMap.put(ingressEgress, connectionIndex);
        }



        for(MapFeature<LineString> feature: features.getFeatures()){
            if(feature.getProperties().getConnectsTo() != null){
                for(ProcessedConnection laneConnection: feature.getProperties().getConnectsTo()){
                    Lane ingressLane = laneLookup.get(feature.getId());
                    Lane egressLane = laneLookup.get(laneConnection.getConnectingLane().getLane());
                    int connectionId = -1;
                    int signalGroup = -1;
                    Integer ingressLaneId = null;
                    Integer egressLaneId = null;

                    if (ingressLane != null) {
                        ingressLaneId = ingressLane.getId();
                    }

                    if (egressLane != null) {
                        egressLaneId = egressLane.getId();
                    }

                    var ingressEgress = new IngressEgress(ingressLaneId, egressLaneId);
                    if (connectionIndexMap.containsKey(ingressEgress)) {
                        connectionId = connectionIndexMap.get(ingressEgress);
                    }

                    if(laneConnection.getSignalGroup() != null){
                        signalGroup = laneConnection.getSignalGroup();
                    }

                    LaneConnection connection = new LaneConnection(ingressLane, egressLane, connectionId, signalGroup);
                    laneConnections.add(connection);
                }
            }
        }





        intersection.setIngressLanes(ingressLanes);
        intersection.setEgressLanes(egressLanes);
        intersection.setLaneConnections(laneConnections);
        
        return intersection; 
    }


    public Intersection(){
        
    }

    /**
     * Creates Stop Lines for this intersection based upon ingress lanes
     */
    public void updateStopLines(){
        this.stopLines = new ArrayList<IntersectionLine>();
        for(Lane lane : this.ingressLanes){
            IntersectionLine line = IntersectionLine.fromLane(lane);
            if(line != null){
                this.stopLines.add(line);
            }
        }
    }

    /**
     * Creates start lines for this intersection based upon egress lanes
     */
    public void updateStartLines(){
        this.startLines = new ArrayList<IntersectionLine>();
        for(Lane lane : this.egressLanes){
            IntersectionLine line = IntersectionLine.fromLane(lane);
            if(line != null){
                this.startLines.add(line);
            }
        }
    }

    /**
     * 
     * @param ingressLane
     * @param egressLane
     * @return LaneConnection The lane connection object associated with this intersection for the provided ingress / egress lane pairs
     */
    public LaneConnection getLaneConnection(Lane ingressLane, Lane egressLane){
        if (ingressLane == null || egressLane == null) {
            return null;
        }
        for(LaneConnection laneConnection: this.laneConnections){

            if(laneConnection.getIngressLane().getId() == ingressLane.getId() && laneConnection.getEgressLane().getId() == egressLane.getId()){
                return laneConnection;
            }
        }
        return null;
    }

    /**
     * 
     * @param ingressLane
     * @return {@code Set<Integer>} a set of all signal groups associated with the provided ingress lane.
     */
    public Set<Integer> getSignalGroupsForIngressLane(Lane ingressLane) {

        Set<Integer> signalGroups = new HashSet<>();
        if (ingressLane == null) {
            return signalGroups;
        }
        for(LaneConnection laneConnection: this.laneConnections){
            if(laneConnection.getIngressLane().getId() == ingressLane.getId()){
                signalGroups.add(laneConnection.getSignalGroup());
            }
        }
        return signalGroups;
    }

    /**
     * 
     * @param egressLane
     * @return {@code Set<Integer>} a set of all signal groups associated with the provided ingress lane.
     */
    public Set<Integer> getSignalGroupsForEgressLane(Lane egressLane) {

        Set<Integer> signalGroups = new HashSet<>();
        if (egressLane == null) {
            return signalGroups;
        }
        for(LaneConnection laneConnection: this.laneConnections){
            if(laneConnection.getEgressLane().getId() == egressLane.getId()){
                signalGroups.add(laneConnection.getSignalGroup());
            }
        }
        return signalGroups;
    }


    /**
     * 
     * @param signalGroup
     * @return {@code ArrayList<LaneConnection>} A list of all lane connections for the provided signal group
     */
    public ArrayList<LaneConnection> getLaneConnectionBySignalGroup(int signalGroup){
        ArrayList<LaneConnection> connections = new ArrayList<>();
        for(LaneConnection laneConnection: this.laneConnections){
            if(laneConnection.getSignalGroup() == signalGroup){
                connections.add(laneConnection);
            }
        }
        return connections;
    }


    /**
     * Sets the ingress lanes associated with this intersection
     * @param ingressLanes
     */
    public void setIngressLanes(ArrayList<Lane> ingressLanes) {
        this.ingressLanes = ingressLanes;
        this.updateStopLines(); // automatically update Stop Line Locations when new ingress Lanes are assigned.
    }

    /**
     * Sets the egress lanes associated with this intersection
     * @param egressLanes
     */
    public void setEgressLanes(ArrayList<Lane> egressLanes) {
        this.egressLanes = egressLanes;
        this.updateStartLines();
    }

    /**
     * Gets the intersectionId number
     * @return int the intersection ID number. Returns 0 if the intersection ID is not available
     */
    public int getIntersectionId() {
        return intersectionId != null ? intersectionId : 0;
    }

    /**
     * 
     * @return String a string representing the lane geometry of this intersection as a WKT string
     */
    public String getIntersectionAsWkt() {
        WKTWriter writer = new WKTWriter(2);
        String wtkOut = "wtk\n";
        writer.setFormatted(true);
        for (Lane lane : this.ingressLanes) {
            wtkOut += "\"" + writer.writeFormatted(lane.getPoints()) + "\"\n";
        }

        for (Lane lane : this.egressLanes) {
            wtkOut += "\"" + writer.writeFormatted(lane.getPoints()) + "\"\n";
        }
        return wtkOut;
    }

    @Override
    public String toString(){
        return "Intersection: " + this.getIntersectionId() + " IngressLanes: " + this.getIngressLanes().size() + " Egress Lanes: " + this.getEgressLanes().size();
    }

}

/**
 * Helper class to store an ingress / egress lane pairing
 */
@Getter
@EqualsAndHashCode
@ToString
@AllArgsConstructor
class IngressEgress {
    Integer ingressLaneId;
    Integer egressLaneId;
}

package us.dot.its.jpo.conflictmonitor.monitor.models.Intersection;

import java.util.*;

import lombok.*;
import us.dot.its.jpo.geojsonconverter.pojos.geojson.LineString;
import us.dot.its.jpo.geojsonconverter.pojos.geojson.connectinglanes.ConnectingLanesFeature;
import us.dot.its.jpo.geojsonconverter.pojos.geojson.map.MapFeature;
import us.dot.its.jpo.geojsonconverter.pojos.geojson.map.MapFeatureCollection;
import us.dot.its.jpo.geojsonconverter.pojos.geojson.map.MapProperties;
import us.dot.its.jpo.geojsonconverter.pojos.geojson.map.ProcessedMap;
import us.dot.its.jpo.ode.plugin.j2735.J2735Connection;

import org.locationtech.jts.geom.Coordinate;
import org.locationtech.jts.io.WKTWriter;

@Getter
@Setter
public class Intersection {
    
    
    private List<Lane> ingressLanes;
    private List<Lane> egressLanes;
    private List<IntersectionLine> stopLines;
    private List<IntersectionLine> startLines;
    private Coordinate referencePoint;
    private Integer intersectionId;
    private Integer roadRegulatorId;
    private ArrayList<LaneConnection> laneConnections;

    public static Intersection fromProcessedMap(ProcessedMap<LineString> map){

        Intersection intersection = new Intersection();
        ArrayList<Lane> ingressLanes = new ArrayList<>();
        ArrayList<Lane> egressLanes = new ArrayList<>();
        ArrayList<LaneConnection> laneConnections = new ArrayList<>();
        HashMap<Integer, Lane> laneLookup = new HashMap<>();

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
                for(J2735Connection laneConnection: feature.getProperties().getConnectsTo()){
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

    public void updateStopLines(){
        this.stopLines = new ArrayList<IntersectionLine>();
        for(Lane lane : this.ingressLanes){
            IntersectionLine line = IntersectionLine.fromLane(lane);
            if(line != null){
                this.stopLines.add(line);
            }
        }
    }

    public void updateStartLines(){
        this.startLines = new ArrayList<IntersectionLine>();
        for(Lane lane : this.egressLanes){
            IntersectionLine line = IntersectionLine.fromLane(lane);
            if(line != null){
                this.startLines.add(line);
            }
        }
    }

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



    public ArrayList<LaneConnection> getLaneConnectionBySignalGroup(int signalGroup){
        ArrayList<LaneConnection> connections = new ArrayList<>();
        for(LaneConnection laneConnection: this.laneConnections){
            if(laneConnection.getSignalGroup() == signalGroup){
                connections.add(laneConnection);
            }
        }
        return connections;
    }



    public void setIngressLanes(ArrayList<Lane> ingressLanes) {
        this.ingressLanes = ingressLanes;
        this.updateStopLines(); // automatically update Stop Line Locations when new ingress Lanes are assigned.
    }


    public void setEgressLanes(ArrayList<Lane> egressLanes) {
        this.egressLanes = egressLanes;
        this.updateStartLines();
    }

    public int getIntersectionId() {
        return intersectionId != null ? intersectionId : 0;
    }


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

@Getter
@EqualsAndHashCode
@ToString
@AllArgsConstructor
class IngressEgress {
    Integer ingressLaneId;
    Integer egressLaneId;
}

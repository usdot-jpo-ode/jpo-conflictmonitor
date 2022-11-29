package us.dot.its.jpo.conflictmonitor.monitor.models.Intersection;

import java.util.ArrayList;

import us.dot.its.jpo.geojsonconverter.pojos.geojson.map.MapFeature;
import us.dot.its.jpo.geojsonconverter.pojos.geojson.map.MapFeatureCollection;

import org.locationtech.jts.geom.Coordinate;
import org.locationtech.jts.geom.LineString;
import org.locationtech.jts.geom.Point;
import org.locationtech.jts.io.WKTWriter;

import com.fasterxml.jackson.annotation.JsonIgnore;

public class Intersection {
    
    
    private ArrayList<Lane> ingressLanes;
    private ArrayList<Lane> egressLanes;
    private ArrayList<IntersectionLine> stopLines;
    private ArrayList<IntersectionLine> startLines;
    private Coordinate referencePoint;
    private int intersectionId;




    public static Intersection fromMapFeatureCollection(MapFeatureCollection map){

        Intersection intersection = new Intersection();
        ArrayList<Lane> ingressLanes = new ArrayList<>();
        ArrayList<Lane> egressLanes = new ArrayList<>();

        if(map.getFeatures().length > 0){
            double[] referencePoint = map.getFeatures()[0].getGeometry().getCoordinates()[0];
            intersection.setReferencePoint(new Coordinate(referencePoint[0], referencePoint[1]));
        }else{
            System.out.println("Cannot Build Intersection from MapFeatureCollection. Feature collection has no Features.");
            return null;
        }
        

        for(MapFeature feature: map.getFeatures()){
            Lane lane = Lane.fromGeoJsonFeature(feature, intersection.getReferencePoint(), 366);
            if(lane.getIngress()){
                ingressLanes.add(lane);
            }else{
                egressLanes.add(lane);
            }
        }

        intersection.setIngressLanes(ingressLanes);
        intersection.setEgressLanes(egressLanes);

        

        
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

    public ArrayList<Lane> getIngressLanes() {
        return ingressLanes;
    }

    public void setIngressLanes(ArrayList<Lane> ingressLanes) {
        this.ingressLanes = ingressLanes;
        this.updateStopLines(); // automatically update Stop Line Locations when new ingress Lanes are assigned.
    }

    public ArrayList<Lane> getEgressLanes() {
        return egressLanes;
    }

    public void setEgressLanes(ArrayList<Lane> egressLanes) {
        this.egressLanes = egressLanes;
        this.updateStartLines();
    }

    public int getIntersectionId() {
        return intersectionId;
    }

    public void setIntersectionId(int intersectionId) {
        this.intersectionId = intersectionId;
    }

    public ArrayList<IntersectionLine> getStopLines() {
        return stopLines;
    }

    public void setStopLines(ArrayList<IntersectionLine> stopLines) {
        this.stopLines = stopLines;
    }

    public Coordinate getReferencePoint() {
        return referencePoint;
    }

    public void setReferencePoint(Coordinate referencePoint) {
        this.referencePoint = referencePoint;
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

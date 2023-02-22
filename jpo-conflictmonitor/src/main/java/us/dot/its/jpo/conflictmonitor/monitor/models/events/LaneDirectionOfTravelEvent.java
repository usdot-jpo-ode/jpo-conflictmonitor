package us.dot.its.jpo.conflictmonitor.monitor.models.events;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import us.dot.its.jpo.geojsonconverter.DateJsonMapper;

import org.springframework.data.mongodb.core.mapping.Document;
@Document("CmLaneDirectionOfTravelEvent")
public class LaneDirectionOfTravelEvent extends Event{
    private long timestamp;
    private int roadRegulatorID;
    private int intersectionID;
    private int laneID;
    private int laneSegmentNumber;
    private double laneSegmentInitialLatitude;
    private double laneSegmentInitialLongitude;
    private double laneSegmentFinalLatitude;
    private double laneSegmentFinalLongitude;
    private double expectedHeading;
    private double medianVehicleHeading;
    private double medianDistanceFromCenterline;
    private int aggregateBSMCount;

    public LaneDirectionOfTravelEvent(){
        super("LaneDirectionOfTravel");
    }

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

    public int getLaneID() {
        return laneID;
    }

    public void setLaneID(int laneID) {
        this.laneID = laneID;
    }

    public int getLaneSegmentNumber() {
        return laneSegmentNumber;
    }

    public void setLaneSegmentNumber(int laneSegmentNumber) {
        this.laneSegmentNumber = laneSegmentNumber;
    }

    public double getLaneSegmentInitialLatitude() {
        return laneSegmentInitialLatitude;
    }

    public void setLaneSegmentInitialLatitude(double laneSegmentInitialLatitude) {
        this.laneSegmentInitialLatitude = laneSegmentInitialLatitude;
    }

    public double getLaneSegmentInitialLongitude() {
        return laneSegmentInitialLongitude;
    }

    public void setLaneSegmentInitialLongitude(double laneSegmentInitialLongitude) {
        this.laneSegmentInitialLongitude = laneSegmentInitialLongitude;
    }

    public double getLaneSegmentFinalLatitude() {
        return laneSegmentFinalLatitude;
    }

    public void setLaneSegmentFinalLatitude(double laneSegmentFinalLatitude) {
        this.laneSegmentFinalLatitude = laneSegmentFinalLatitude;
    }

    public double getLaneSegmentFinalLongitude() {
        return laneSegmentFinalLongitude;
    }

    public void setLaneSegmentFinalLongitude(double laneSegmentFinalLongitude) {
        this.laneSegmentFinalLongitude = laneSegmentFinalLongitude;
    }

    public double getExpectedHeading() {
        return expectedHeading;
    }

    public void setExpectedHeading(double expectedHeading) {
        this.expectedHeading = expectedHeading;
    }

    public double getMedianVehicleHeading() {
        return medianVehicleHeading;
    }

    public void setMedianVehicleHeading(double medianVehicleHeading) {
        this.medianVehicleHeading = medianVehicleHeading;
    }

    public double getMedianDistanceFromCenterline() {
        return medianDistanceFromCenterline;
    }

    public void setMedianDistanceFromCenterline(double medianDistanceFromCenterline) {
        this.medianDistanceFromCenterline = medianDistanceFromCenterline;
    }

    public int getAggregateBSMCount() {
        return aggregateBSMCount;
    }

    public void setAggregateBSMCount(int aggregateBSMCount) {
        this.aggregateBSMCount = aggregateBSMCount;
    }

    @JsonIgnore
    public String getKey(){
        return this.intersectionID + "";
    }

    @Override
    public boolean equals(Object o) {
        if (o == this)
            return true;
        if (!(o instanceof SignalStateEvent)) {
            return false;
        }
        LaneDirectionOfTravelEvent laneDirectionOfTravelEvent = (LaneDirectionOfTravelEvent) o;
        return 
            timestamp == laneDirectionOfTravelEvent.timestamp &&
            roadRegulatorID == laneDirectionOfTravelEvent.roadRegulatorID &&
            intersectionID == laneDirectionOfTravelEvent.intersectionID &&
            laneID == laneDirectionOfTravelEvent.laneID &&
            laneSegmentNumber == laneDirectionOfTravelEvent.laneSegmentNumber &&
            laneSegmentInitialLatitude == laneDirectionOfTravelEvent.laneSegmentInitialLatitude &&
            laneSegmentInitialLongitude == laneDirectionOfTravelEvent.laneSegmentInitialLongitude &&
            laneSegmentFinalLongitude == laneDirectionOfTravelEvent.laneSegmentFinalLongitude &&
            expectedHeading == laneDirectionOfTravelEvent.expectedHeading &&
            medianVehicleHeading == laneDirectionOfTravelEvent.medianVehicleHeading &&
            medianDistanceFromCenterline == laneDirectionOfTravelEvent.medianDistanceFromCenterline &&
            aggregateBSMCount == laneDirectionOfTravelEvent.aggregateBSMCount;
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

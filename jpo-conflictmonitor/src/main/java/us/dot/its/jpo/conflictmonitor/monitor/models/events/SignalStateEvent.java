package us.dot.its.jpo.conflictmonitor.monitor.models.events;

import us.dot.its.jpo.conflictmonitor.monitor.models.J2735GenericLane;

public class SignalStateEvent {
    private int timestamp;
    private int roadRegulatorID;
    private J2735GenericLane ingressLane;
    private J2735GenericLane egressLane;
    private int connectionID;
    private String eventState;
    private int vehicleID;
    private double latitude;
    private double longitude;
    private double heading;
    private double speed;

    public int getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(int timestamp) {
        this.timestamp = timestamp;
    }

    public int getRoadRegulatorID() {
        return roadRegulatorID;
    }

    public void setRoadRegulatorID(int roadRegulatorID) {
        this.roadRegulatorID = roadRegulatorID;
    }

    public J2735GenericLane getIngressLane() {
        return ingressLane;
    }

    public void setIngressLane(J2735GenericLane ingressLane) {
        this.ingressLane = ingressLane;
    }

    public J2735GenericLane getEgressLane() {
        return egressLane;
    }

    public void setEgressLane(J2735GenericLane egressLane) {
        this.egressLane = egressLane;
    }

    public int getConnectionID() {
        return connectionID;
    }

    public void setConnectionID(int connectionID) {
        this.connectionID = connectionID;
    }

    public String getEventState() {
        return eventState;
    }

    public void setEventState(String eventState) {
        this.eventState = eventState;
    }

    public int getVehicleID() {
        return vehicleID;
    }

    public void setVehicleID(int vehicleID) {
        this.vehicleID = vehicleID;
    }

    public double getLatitude() {
        return latitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }

    public double getHeading() {
        return heading;
    }

    public void setHeading(double heading) {
        this.heading = heading;
    }

    public double getSpeed() {
        return speed;
    }

    public void setSpeed(double speed) {
        this.speed = speed;
    }
}

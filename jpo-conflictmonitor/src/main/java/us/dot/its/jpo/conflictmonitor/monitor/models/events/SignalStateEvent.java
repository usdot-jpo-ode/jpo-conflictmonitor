package us.dot.its.jpo.conflictmonitor.monitor.models.events;

import com.fasterxml.jackson.annotation.JsonIgnore;

import us.dot.its.jpo.ode.plugin.j2735.J2735MovementPhaseState;

public class SignalStateEvent extends Event{
    private long timestamp;
    private int roadRegulatorID;
    private int ingressLane;
    private int egressLane;
    private int connectionID;
    private J2735MovementPhaseState eventState;
    private String vehicleID;
    private double latitude;
    private double longitude;
    private double heading;
    private double speed;
    private int signalGroup;

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

    public int getIngressLane() {
        return ingressLane;
    }

    public void setIngressLane(int ingressLane) {
        this.ingressLane = ingressLane;
    }

    public int getEgressLane() {
        return egressLane;
    }

    public void setEgressLane(int egressLane) {
        this.egressLane = egressLane;
    }

    public int getConnectionID() {
        return connectionID;
    }

    public void setConnectionID(int connectionID) {
        this.connectionID = connectionID;
    }

    public J2735MovementPhaseState getEventState() {
        return eventState;
    }

    public void setEventState(J2735MovementPhaseState eventState) {
        this.eventState = eventState;
    }

    public String getVehicleID() {
        return vehicleID;
    }

    public void setVehicleID(String vehicleID) {
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

    public int getSignalGroup() {
        return signalGroup;
    }

    public void setSignalGroup(int signalGroup) {
        this.signalGroup = signalGroup;
    }

    @JsonIgnore
    public String getKey(){
        return this.roadRegulatorID + "_" + this.vehicleID;
    }
}

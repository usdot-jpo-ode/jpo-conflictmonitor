package us.dot.its.jpo.conflictmonitor.monitor.models.events;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import us.dot.its.jpo.geojsonconverter.DateJsonMapper;
import us.dot.its.jpo.ode.plugin.j2735.J2735MovementPhaseState;

import org.springframework.data.mongodb.core.mapping.Document;
@Document("CmSignalStateStopEvent")
public class SignalStateStopEvent extends Event{
    
    private long timestamp;
    private int ingressLane;
    private int egressLane;
    private int connectionID;
    private J2735MovementPhaseState eventState;
    private String vehicleID;
    private double latitude;
    private double longitude;
    private double heading;
    private double speed;

    public SignalStateStopEvent(){
        super("SignalStateStop");
    }

    public long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
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

    public String getKey(){
        return this.getRoadRegulatorID() + "_" + this.getVehicleID();
    }

    @Override
    public boolean equals(Object o) {
        if (o == this)
            return true;
        if (!(o instanceof SignalStateStopEvent)) {
            return false;
        }
        SignalStateStopEvent signalStateStopEvent = (SignalStateStopEvent) o;
        return 
            this.getTimestamp() == signalStateStopEvent.getTimestamp() &&
            this.getRoadRegulatorID() == signalStateStopEvent.getRoadRegulatorID() &&
            this.getIngressLane() == signalStateStopEvent.getIngressLane() &&
            this.getEgressLane() == signalStateStopEvent.getEgressLane() &&
            this.getConnectionID() == signalStateStopEvent.getConnectionID() &&
            this.getEventState() == signalStateStopEvent.getEventState() &&
            this.getVehicleID() == signalStateStopEvent.getVehicleID() &&
            this.getLatitude() == signalStateStopEvent.getLatitude() &&
            this.getLongitude() == signalStateStopEvent.getLongitude() &&
            this.getHeading() == signalStateStopEvent.getHeading() &&
            this.getSpeed() == signalStateStopEvent.getSpeed();
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

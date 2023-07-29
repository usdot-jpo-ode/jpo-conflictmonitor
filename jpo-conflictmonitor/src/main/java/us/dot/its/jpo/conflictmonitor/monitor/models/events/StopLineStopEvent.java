package us.dot.its.jpo.conflictmonitor.monitor.models.events;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import lombok.EqualsAndHashCode;
import lombok.Generated;
import lombok.Getter;
import lombok.Setter;
import us.dot.its.jpo.geojsonconverter.DateJsonMapper;
import us.dot.its.jpo.ode.plugin.j2735.J2735MovementPhaseState;

import org.springframework.data.mongodb.core.mapping.Document;
@Document("CmStopLineStopEvent")
@Getter
@Setter
@EqualsAndHashCode(callSuper=true)
@Generated
public class StopLineStopEvent extends Event{

    private String source;
    private int ingressLane;
    private int egressLane;
    private int connectionID;
    private J2735MovementPhaseState eventState;
    private J2735MovementPhaseState initialEventState;
    private long initialTimestamp;
    private J2735MovementPhaseState finalEventState;
    private long finalTimestamp;
    private String vehicleID;
    private double latitude;
    private double longitude;
    private double heading;
    private int signalGroup;
    private double timeStoppedDuringRed;
    private double timeStoppedDuringYellow;
    private double timeStoppedDuringGreen;

    public StopLineStopEvent(){
        super("StopLineStop");
    }


    public String getKey(){
        return this.getRoadRegulatorID() + "_" + this.getVehicleID();
    }




}

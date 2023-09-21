package us.dot.its.jpo.conflictmonitor.monitor.models.events;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.EqualsAndHashCode;
import lombok.Generated;
import lombok.Getter;
import lombok.Setter;
import us.dot.its.jpo.ode.plugin.j2735.J2735MovementPhaseState;

@Getter
@Setter
@EqualsAndHashCode(callSuper=true)
@Generated
@JsonIgnoreProperties(ignoreUnknown = true)
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
        return this.getRoadRegulatorID() + "_" + this.getIntersectionID() + "_" + this.getVehicleID();
    }




}

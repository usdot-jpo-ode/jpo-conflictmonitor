package us.dot.its.jpo.conflictmonitor.monitor.models.events;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import lombok.EqualsAndHashCode;
import lombok.Generated;
import lombok.Getter;
import lombok.Setter;
import us.dot.its.jpo.geojsonconverter.DateJsonMapper;
import us.dot.its.jpo.ode.plugin.j2735.J2735MovementPhaseState;

import org.springframework.data.mongodb.core.mapping.Document;
@Document("CmSignalStateEvent")
@Getter
@Setter
@EqualsAndHashCode(callSuper=true)
@Generated
public class SignalStateEvent extends Event{
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
    private int signalGroup;

    public SignalStateEvent(){
        super("SignalState");
    }

    @JsonIgnore
    public String getKey(){
        return this.getRoadRegulatorID() + "_" + this.getVehicleID();
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

package us.dot.its.jpo.conflictmonitor.monitor.models.spat;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnore;

import us.dot.its.jpo.geojsonconverter.pojos.spat.MovementEvent;
import us.dot.its.jpo.geojsonconverter.pojos.spat.MovementState;
import us.dot.its.jpo.ode.plugin.j2735.J2735MovementPhaseState;

public class SpatTimeChangeDetailState {
    private long minEndTime;
    private long maxEndTime;
    private int signalGroup;
    private J2735MovementPhaseState eventState;

    @JsonIgnore
    public static SpatTimeChangeDetailState fromMovementState(MovementState state){
        SpatTimeChangeDetailState newState = new SpatTimeChangeDetailState();
        newState.setSignalGroup(state.getSignalGroup());

        List<MovementEvent> events =  state.getStateTimeSpeed();
        if(events.size() > 0){
            newState.setMaxEndTime(events.get(0).getTiming().getMaxEndTime().toInstant().toEpochMilli());
            newState.setMinEndTime(events.get(0).getTiming().getMinEndTime().toInstant().toEpochMilli());
            newState.setEventState(events.get(0).getEventState());
        }
        
        return newState;
    }
    
    public long getMinEndTime() {
        return minEndTime;
    }

    public void setMinEndTime(long minEndTime) {
        this.minEndTime = minEndTime;
    }

    public long getMaxEndTime() {
        return maxEndTime;
    }

    public void setMaxEndTime(long maxEndTime) {
        this.maxEndTime = maxEndTime;
    }

    public int getSignalGroup() {
        return signalGroup;
    }

    public void setSignalGroup(int signalGroup) {
        this.signalGroup = signalGroup;
    }

    public J2735MovementPhaseState getEventState() {
        return eventState;
    }
    public void setEventState(J2735MovementPhaseState eventState) {
        this.eventState = eventState;
    }
    
}

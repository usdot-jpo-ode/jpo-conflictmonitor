package us.dot.its.jpo.conflictmonitor.monitor.models.spat;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnore;

import lombok.EqualsAndHashCode;
import lombok.ToString;
import us.dot.its.jpo.geojsonconverter.pojos.spat.MovementEvent;
import us.dot.its.jpo.geojsonconverter.pojos.spat.MovementState;
import us.dot.its.jpo.ode.plugin.j2735.J2735MovementPhaseState;

@EqualsAndHashCode
@ToString
public class SpatTimeChangeDetailState {
    private long minEndTime;
    private long maxEndTime;
    private int signalGroup;
    private J2735MovementPhaseState eventState;

    @JsonIgnore
    public static SpatTimeChangeDetailState fromMovementState(MovementState state){
        SpatTimeChangeDetailState newState = new SpatTimeChangeDetailState();
        if (state.getSignalGroup() != null) {
            newState.setSignalGroup(state.getSignalGroup());
        }

        List<MovementEvent> events =  state.getStateTimeSpeed();

        if(events != null && events.size() > 0){
            var event = events.get(0);
            if (event.getTiming() != null) {
                var timing = event.getTiming();
                if (timing.getMaxEndTime() != null) {
                    newState.setMaxEndTime(timing.getMaxEndTime().toInstant().toEpochMilli());
                }
                if (timing.getMinEndTime() != null) {
                    newState.setMinEndTime(timing.getMinEndTime().toInstant().toEpochMilli());
                }
            }
            newState.setEventState(event.getEventState());
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

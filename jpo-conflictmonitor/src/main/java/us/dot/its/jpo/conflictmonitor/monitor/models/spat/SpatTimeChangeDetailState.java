package us.dot.its.jpo.conflictmonitor.monitor.models.spat;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnore;

import lombok.EqualsAndHashCode;
import lombok.ToString;

import us.dot.its.jpo.asn.j2735.r2024.SPAT.MovementPhaseState;
import us.dot.its.jpo.geojsonconverter.pojos.spat.ProcessedMovementEvent;
import us.dot.its.jpo.geojsonconverter.pojos.spat.ProcessedMovementState;

@EqualsAndHashCode
@ToString
public class SpatTimeChangeDetailState {
    private long minEndTime;
    private long maxEndTime;
    private int signalGroup;
    private MovementPhaseState eventState;

    @JsonIgnore
    public static SpatTimeChangeDetailState fromMovementState(ProcessedMovementState state){
        SpatTimeChangeDetailState newState = new SpatTimeChangeDetailState();
        if (state.getSignalGroup() != null) {
            newState.setSignalGroup(state.getSignalGroup());
        }

        List<ProcessedMovementEvent> events =  state.getStateTimeSpeed();

        if(events != null && !events.isEmpty()){
            var event = events.getFirst();
            if (event.getTiming() != null) {
                var timing = event.getTiming();
                if (timing.getMaxEndTime() != null) {
                    long millis = timing.getMaxEndTime().toInstant().toEpochMilli();
                    if(millis >0){
                        newState.setMaxEndTime(millis);
                    }else{
                        newState.setMaxEndTime(0);
                    }
                    
                }else{
                    newState.setMaxEndTime(-1);
                }
                if (timing.getMinEndTime() != null) {
                    long millis = timing.getMinEndTime().toInstant().toEpochMilli();
                    if(millis >0){
                        newState.setMinEndTime(millis);
                    }else{
                        newState.setMinEndTime(0);
                    }
                }else{
                    newState.setMinEndTime(-1);
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

    public MovementPhaseState getEventState() {
        return eventState;
    }
    public void setEventState(MovementPhaseState eventState) {
        this.eventState = eventState;
    }
    
}

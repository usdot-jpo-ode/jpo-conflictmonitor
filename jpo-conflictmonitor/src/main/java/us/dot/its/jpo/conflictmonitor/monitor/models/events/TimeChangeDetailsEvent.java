package us.dot.its.jpo.conflictmonitor.monitor.models.events;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import lombok.EqualsAndHashCode;
import lombok.Generated;
import lombok.Getter;
import lombok.Setter;
import us.dot.its.jpo.geojsonconverter.DateJsonMapper;
import us.dot.its.jpo.ode.plugin.j2735.J2735MovementPhaseState;

enum TimeMarkType {
    MIN_END_TIME,
    MAX_END_TIME,
}

@Getter
@Setter
@EqualsAndHashCode(callSuper=true)
@Generated
public class TimeChangeDetailsEvent extends Event{

    private int signalGroup;
    private long firstSpatTimestamp;
    private long secondSpatTimestamp;
    private String firstTimeMarkType;
    private String secondTimeMarkType;
    private long firstConflictingTimemark;
    private long secondConflictingTimemark;
    private J2735MovementPhaseState firstState;
    private J2735MovementPhaseState secondState;
    private long firstConflictingUtcTimestamp;
    private long secondConflictingUtcTimestamp;
    private String source;

    public TimeChangeDetailsEvent(){
        super("TimeChangeDetails");
    }

}

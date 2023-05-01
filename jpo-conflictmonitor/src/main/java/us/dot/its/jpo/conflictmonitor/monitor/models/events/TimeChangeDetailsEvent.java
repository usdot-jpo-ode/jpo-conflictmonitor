package us.dot.its.jpo.conflictmonitor.monitor.models.events;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import lombok.EqualsAndHashCode;
import lombok.Generated;
import lombok.Getter;
import lombok.Setter;
import us.dot.its.jpo.geojsonconverter.DateJsonMapper;

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
    private long firstTimeMarkType;
    private long secondTimeMarkType;
    private long firstConflictingTimemark;
    private long secondConflictingTimemark;

    public TimeChangeDetailsEvent(){
        super("TimeChangeDetails");
    }

}

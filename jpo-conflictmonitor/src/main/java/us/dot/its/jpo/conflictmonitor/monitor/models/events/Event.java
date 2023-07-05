package us.dot.its.jpo.conflictmonitor.monitor.models.events;

import java.time.ZonedDateTime;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import us.dot.its.jpo.conflictmonitor.monitor.models.events.broadcast_rate.MapBroadcastRateEvent;
import us.dot.its.jpo.conflictmonitor.monitor.models.events.broadcast_rate.SpatBroadcastRateEvent;
import us.dot.its.jpo.conflictmonitor.monitor.models.events.minimum_data.MapMinimumDataEvent;
import us.dot.its.jpo.conflictmonitor.monitor.models.events.minimum_data.SpatMinimumDataEvent;
import us.dot.its.jpo.geojsonconverter.DateJsonMapper;

import lombok.EqualsAndHashCode;
import lombok.Generated;
import lombok.Getter;
import lombok.Setter;

@JsonTypeInfo(
        use = JsonTypeInfo.Id.NAME,
        include = JsonTypeInfo.As.EXISTING_PROPERTY,
        property = "eventType"
)
@JsonSubTypes({
        @JsonSubTypes.Type(value = ConnectionOfTravelEvent.class, name = "ConnectionOfTravel"),
        @JsonSubTypes.Type(value = IntersectionReferenceAlignmentEvent.class, name = "IntersectionReferenceAlignment"),
        @JsonSubTypes.Type(value = LaneDirectionOfTravelEvent.class, name = "LaneDirectionOfTravel"),
        @JsonSubTypes.Type(value = SignalGroupAlignmentEvent.class, name = "SignalGroupAlignment"),
        @JsonSubTypes.Type(value = SignalStateEvent.class, name = "SignalState"),
        @JsonSubTypes.Type(value = SignalStateConflictEvent.class, name = "SignalStateConflict"),
        @JsonSubTypes.Type(value = SignalStateStopEvent.class, name = "SignalStateStop"),
        @JsonSubTypes.Type(value = TimeChangeDetailsEvent.class, name = "TimeChangeDetails"),
        @JsonSubTypes.Type(value = MapMinimumDataEvent.class, name = "MapMinimumData"),
        @JsonSubTypes.Type(value = SpatMinimumDataEvent.class, name = "SpatMinimumData"),
        @JsonSubTypes.Type(value = MapBroadcastRateEvent.class, name = "MapBroadcastRate"),
        @JsonSubTypes.Type(value = SpatBroadcastRateEvent.class, name = "SpatBroadcastRate"),
})
@Getter
@Setter
@EqualsAndHashCode
@Generated
public abstract class Event {
    
    private long eventGeneratedAt = ZonedDateTime.now().toInstant().toEpochMilli();
    private String eventType;
    private int intersectionID;
    private int roadRegulatorID;
    

    public Event(String eventType){
        this.eventType = eventType;
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

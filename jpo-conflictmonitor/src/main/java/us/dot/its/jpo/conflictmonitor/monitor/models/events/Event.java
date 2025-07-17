package us.dot.its.jpo.conflictmonitor.monitor.models.events;

import java.time.ZonedDateTime;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.core.JsonProcessingException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import us.dot.its.jpo.conflictmonitor.monitor.models.events.broadcast_rate.MapBroadcastRateEvent;
import us.dot.its.jpo.conflictmonitor.monitor.models.events.broadcast_rate.SpatBroadcastRateEvent;
import us.dot.its.jpo.conflictmonitor.monitor.models.events.minimum_data.MapMinimumDataEvent;
import us.dot.its.jpo.conflictmonitor.monitor.models.events.minimum_data.MapMinimumDataEventAggregation;
import us.dot.its.jpo.conflictmonitor.monitor.models.events.minimum_data.SpatMinimumDataEvent;
import us.dot.its.jpo.conflictmonitor.monitor.models.events.minimum_data.SpatMinimumDataEventAggregation;
import us.dot.its.jpo.conflictmonitor.monitor.models.events.timestamp_delta.MapTimestampDeltaEvent;
import us.dot.its.jpo.conflictmonitor.monitor.models.events.timestamp_delta.SpatTimestampDeltaEvent;
import us.dot.its.jpo.geojsonconverter.DateJsonMapper;

import lombok.EqualsAndHashCode;
import lombok.Generated;
import lombok.Getter;
import lombok.Setter;

/**
 * Event is the parent class to all of the different sub event types.
 */
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
        @JsonSubTypes.Type(value = StopLinePassageEvent.class, name = "StopLinePassage"),
        @JsonSubTypes.Type(value = SignalStateConflictEvent.class, name = "SignalStateConflict"),
        @JsonSubTypes.Type(value = StopLineStopEvent.class, name = "StopLineStop"),
        @JsonSubTypes.Type(value = TimeChangeDetailsEvent.class, name = "TimeChangeDetails"),
        @JsonSubTypes.Type(value = MapMinimumDataEvent.class, name = "MapMinimumData"),
        @JsonSubTypes.Type(value = SpatMinimumDataEvent.class, name = "SpatMinimumData"),
        @JsonSubTypes.Type(value = MapBroadcastRateEvent.class, name = "MapBroadcastRate"),
        @JsonSubTypes.Type(value = SpatBroadcastRateEvent.class, name = "SpatBroadcastRate"),
        @JsonSubTypes.Type(value = MapMessageCountProgressionEvent.class, name = "MapMessageCountProgression"),
        @JsonSubTypes.Type(value = SpatMessageCountProgressionEvent.class, name = "SpatMessageCountProgression"),
        @JsonSubTypes.Type(value = BsmMessageCountProgressionEvent.class, name = "BsmMessageCountProgression"),
        @JsonSubTypes.Type(value = MapTimestampDeltaEvent.class, name = "MapTimestampDelta"),
        @JsonSubTypes.Type(value = SpatTimestampDeltaEvent.class, name = "SpatTimestampDelta"),
        @JsonSubTypes.Type(value = EventStateProgressionEvent.class, name = "EventStateProgression"),
        @JsonSubTypes.Type(value = SpatMinimumDataEventAggregation.class, name = "SpatMinimumDataAggregation"),
        @JsonSubTypes.Type(value = MapMinimumDataEventAggregation.class, name = "MapMinimumDataAggregation"),
        @JsonSubTypes.Type(value = IntersectionReferenceAlignmentEventAggregation.class,
                name = "IntersectionReferenceAlignmentAggregation"),
        @JsonSubTypes.Type(value = SignalGroupAlignmentEventAggregation.class,
                name = "SignalGroupAlignmentAggregation"),
        @JsonSubTypes.Type(value = SignalStateConflictEventAggregation.class, name = "SignalStateConflictAggregation"),
        @JsonSubTypes.Type(value = TimeChangeDetailsEventAggregation.class, name = "TimeChangeDetailsAggregation"),
        @JsonSubTypes.Type(value = EventStateProgressionEventAggregation.class,
                name = "EventStateProgressionAggregation"),
        @JsonSubTypes.Type(value = BsmMessageCountProgressionEventAggregation.class,
                name = "BsmMessageCountProgressionAggregation"),
        @JsonSubTypes.Type(value = SpatMessageCountProgressionEventAggregation.class,
                name = "SpatMessageCountProgressionAggregation"),
        @JsonSubTypes.Type(value = MapMessageCountProgressionEventAggregation.class,
                name = "MapMessageCountProgressionAggregation")
})
@Getter
@Setter
@EqualsAndHashCode
@Generated
public abstract class Event {

    private static final Logger logger = LoggerFactory.getLogger(Event.class);
    
    /**
     * long representing the utc timestamp in milliseconds when this event was generated. This value is automatically created by the event when the object is generated. It doesn't represent the time that the actual data occurred. 
     * It is recommended to use this value for indexing and data retrieval as it is common among all events. In general this timestamp is within 1 - 2 seconds of the actual time at which an event occurred depending on the event. 
     */
    private long eventGeneratedAt = ZonedDateTime.now().toInstant().toEpochMilli();

    /**
     * A string representing the time of event this class represents. This is used for automatically decoding and parsing event types with Jackson.
     */
    private String eventType;


    /**
     * int representing the intersectionID where this event occurred. If this event didn't take place at an intersection (such as with vehicle Misbehavior events) a value of -1 is used instead.
     */
    private int intersectionID = -1;

    /**
     * int representing the roadRegulatorID of the intersection where this event occurred. Generally set to -1, roadRegulator is in the process of being deprecated and shouldn't be used. 
     */
    private int roadRegulatorID = -1;
    

    public Event(String eventType){
        this.eventType = eventType;
    }


    @Override
    public String toString() {
        try {
            return DateJsonMapper.getInstance().writeValueAsString(this);
        } catch (JsonProcessingException e) {
            logger.error(String.format("Exception serializing %s Event to JSON", eventType), e);
        }
        return "";
    }
}

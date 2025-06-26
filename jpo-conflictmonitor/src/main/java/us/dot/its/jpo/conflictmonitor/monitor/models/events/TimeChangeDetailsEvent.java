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

/*
 * TimeChangeDetailsEvent - TimeChangeDetailsEvents are generated whenever the SPaT timeChangeDetails changes time in an invalid way.
 * For example, if the minEndTime is reduced between two subsequent SPaT messages, a time change details event will be generated
 * For full logic on all the ways this event might be generated please see the CIMMS software installation manual.
 */
@Getter
@Setter
@EqualsAndHashCode(callSuper=true)
@Generated
public class TimeChangeDetailsEvent extends Event{

    /**
     * int representing the signal group which had an invalid time change transition
     */
    private int signalGroup;

    /**
     * long representing the utc timestamp in milliseconds of the first SPaT message 
     */
    private long firstSpatTimestamp;

    /**
     * long representing the utc timestamp in milliseconds of the second SPaT message
     */
    private long secondSpatTimestamp;

    /**
     * String representing the timeMarkType of the first conflicting timeMark
     */
    private String firstTimeMarkType;

    /**
     * String representing the timeMarkType of the second conflicting timeMark
     */
    private String secondTimeMarkType;

    /**
     * long representing the time of the first conflicting time mark
     */
    private long firstConflictingTimemark;

    /**
     * long representing the time of the second conflicting time mark
     */
    private long secondConflictingTimemark;

    /**
     * J2735MovementPhaseState representing the state of the light in the first SPaT message
     */
    private J2735MovementPhaseState firstState;

    /**
     * J2735MovementPhaseState representing the state of the light in the second SPaT message
     */
    private J2735MovementPhaseState secondState;

    /**
     * long representing the utc timestamp in milliseconds of the first conflicting timemark 
     */
    private long firstConflictingUtcTimestamp;

    /**
     * long representing the utc timestamp in milliseconds of the second conflicting timemark 
     */
    private long secondConflictingUtcTimestamp;

    /**
     * String representing the source of the event. Typically the intersection ID or RSU IP address.
     */
    private String source;

    public TimeChangeDetailsEvent(){
        super("TimeChangeDetails");
    }

}

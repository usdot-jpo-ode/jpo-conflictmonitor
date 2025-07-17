package us.dot.its.jpo.conflictmonitor.monitor.models.assessments;

import lombok.Generated;
import lombok.Getter;
import lombok.Setter;


/**
 * Signal State Assessment groups represent the state of the light when a vehicle drove through it.
 * @deprecated Use Stop Line Stop Assessment Groups or Stop Line Passage Assessment Groups instead.
 */
@Getter
@Setter
@Generated
public class SignalStateAssessmentGroup {


    /**
     * The Signal group id in the SPaT message that this assessment corresponds to.
     */
    private int signalGroup;

    /**
     * The number of times a vehicle passed through on a red light.
     */
    private int redEvents;

    /**
     * The number of times a vehicle passed through on a yellow light.
     */
    private int yellowEvents;

    /**
     * The number of times a vehicle passed through on a green light.
     */
    private int greenEvents;
}

package us.dot.its.jpo.conflictmonitor.monitor.models;

import lombok.EqualsAndHashCode;
import lombok.Generated;
import lombok.Getter;
import lombok.Setter;
import us.dot.its.jpo.conflictmonitor.monitor.models.assessments.Assessment;
import us.dot.its.jpo.conflictmonitor.monitor.models.events.Event;

/**
 * Represents the association between an Event and an Assessment.
 * This class is used to link a specific event with its corresponding assessment.
 */
@Getter
@Setter
@EqualsAndHashCode(callSuper=false)
@Generated
public class EventAssessment {
    
    /**
     * The event that is being assessed.
     */
    private Event event;

    /**
     * The assessment associated with the event.
     */
    private Assessment assessment;


}

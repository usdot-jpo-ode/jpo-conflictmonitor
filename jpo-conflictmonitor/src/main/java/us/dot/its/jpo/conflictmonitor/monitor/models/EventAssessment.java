package us.dot.its.jpo.conflictmonitor.monitor.models;

import lombok.EqualsAndHashCode;
import lombok.Generated;
import lombok.Getter;
import lombok.Setter;
import us.dot.its.jpo.conflictmonitor.monitor.models.assessments.Assessment;
import us.dot.its.jpo.conflictmonitor.monitor.models.events.Event;

@Getter
@Setter
@EqualsAndHashCode(callSuper=false)
@Generated
public class EventAssessment {
    
    private Event event;
    private Assessment assessment;


}

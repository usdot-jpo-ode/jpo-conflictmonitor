package us.dot.its.jpo.conflictmonitor.monitor.models.assessments;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import lombok.Generated;
import lombok.Getter;
import lombok.Setter;
import us.dot.its.jpo.geojsonconverter.DateJsonMapper;

/*
 * This class has been deprecated and should no longer be used. Use StopLinePassageAssessmentGroup instead.
 */

@Getter
@Setter
@Generated
@Deprecated
public class SignalStateAssessmentGroup {
    private int signalGroup;
    private int redEvents;
    private int yellowEvents;
    private int greenEvents;
}

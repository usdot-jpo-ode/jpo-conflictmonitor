package us.dot.its.jpo.conflictmonitor.monitor.models.assessments;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import lombok.Generated;
import lombok.Getter;
import lombok.Setter;
import us.dot.its.jpo.geojsonconverter.DateJsonMapper;

@Getter
@Setter
@Generated
public class SignalStateAssessmentGroup {
    private int signalGroup;
    private int redEvents;
    private int yellowEvents;
    private int greenEvents;
}

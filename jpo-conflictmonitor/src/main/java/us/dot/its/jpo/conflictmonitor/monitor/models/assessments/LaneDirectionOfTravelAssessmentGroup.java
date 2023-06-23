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
public class LaneDirectionOfTravelAssessmentGroup {
    private int laneID;
    private int segmentID;
    
    private int inToleranceEvents;
    private int outOfToleranceEvents;
    
    private double medianInToleranceHeading;
    private double medianInToleranceCenterlineDistance;
    private double medianHeading;
    private double medianCenterlineDistance;

    private double expectedHeading; 

    private double tolerance;
    private double distanceFromCenterlineTolerance;
}

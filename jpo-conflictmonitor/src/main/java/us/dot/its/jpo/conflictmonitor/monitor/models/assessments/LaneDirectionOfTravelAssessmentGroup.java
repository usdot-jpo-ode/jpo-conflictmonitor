package us.dot.its.jpo.conflictmonitor.monitor.models.assessments;

import lombok.Generated;
import lombok.Getter;
import lombok.Setter;

/**
 * LaneDirectionOfTravelAssessmentGroups identify how close vehicles were to the centerline of the line while driving through it. 
*/
@Getter
@Setter
@Generated
public class LaneDirectionOfTravelAssessmentGroup {

    /**
     * The lane ID in the MAP message
     */
    private int laneID;

    /**
     * Which segment of the lane was evaluated. Segments are defined as the space between two coordinate points in the MAP message. Segment 0 is the segment between the 1st and 2nd coordinate point in the lane of a map message.
     */
    private int segmentID;
    
    /**
     * The number of times a vehicle driving through this segment was within the allowable distance tolerance of the centerline.
     */
    private int inToleranceEvents;

    /**
     * The number of times a vehicle driving through this segment was not within the allowable distance tolerance of the centerline.
     */
    private int outOfToleranceEvents;
    
    /**
     * The median delta between the vehicles heading in degrees and the direction of the lane as the vehicle drove through the lane segment.
     */
    private double medianInToleranceHeading;

    /**
     * The median distance in meters between the center of a vehicle and the center of the lane as the vehicle drove through the lane segment.
     */
    private double medianInToleranceCenterlineDistance;

    /**
     * The overall median heading in degrees of vehicles that drove through the lane segment.
     */
    private double medianHeading;

    /**
     * The overall median distance in meters from the centerline of vehicles that drove through the lane segment.
     */
    private double medianCenterlineDistance;

    /**
     * The proper heading of this lane segment. This is measured in degrees from north. 
     */
    private double expectedHeading; 


    /**
     * The allowable delta between a vehicles heading and the lanes heading in degrees.
     */
    private double tolerance;

    /**
     * The allowable delta between the center of a vehicle and the center of the lane in meters.
     */
    private double distanceFromCenterlineTolerance;
}

package us.dot.its.jpo.conflictmonitor.monitor.models.events;

import com.fasterxml.jackson.annotation.JsonIgnore;

import lombok.EqualsAndHashCode;
import lombok.Generated;
import lombok.Getter;
import lombok.Setter;


/**
* LaneDirectionOfTravelEvent - LaneDirectionOfTravel events are generated each time a vehicle drives into or out of an intersection. The generation of this type of event doesn't indicate a problem with the intersection.
* Additionally, multiple LaneDirectionOfTravelEvent objects will be generated for each vehicle pass (one event per lane, per lane segment).
*   
*/
@Getter
@Setter
@EqualsAndHashCode(callSuper=true)
@Generated
public class LaneDirectionOfTravelEvent extends Event{

    /**
    *  long representing the utc timestamp in milliseconds from when the BSM message was in a given lane segment
    */
    private long timestamp;

    /**
    *  int representing the laneID of the lane the vehicle moved through. Lane ID's numbers correspond to the Lane ID in the ProcessedMap message.
    */
    private int laneID;

    /**
    *  int representing the lane segment for which this event was generated. Lane Segments are zero indexed based upon which 2 node points of the lane the vehicle was between.
    *  For example, for an ingress lane, segment 0 would enclose a rectangular space between the first and second node points in the lane point list.
    *  This rectangular segment would have a width equal to the lane-width from the ProcessedMap message. 
    */
    private int laneSegmentNumber;

    /**
    *  double representing the latitude of the first lane point
    */
    private double laneSegmentInitialLatitude;

    /**
    *  double representing the longitude of the first lane point
    */
    private double laneSegmentInitialLongitude;

    /**
    *  double representing the latitude of the second lane point
    */
    private double laneSegmentFinalLatitude;

    /**
    *  double representing the latitude of the second lane point
    */
    private double laneSegmentFinalLongitude;

    /**
    *  double representing the expected heading in degrees from north of a vehicle traveling through this lane segment
    */
    private double expectedHeading;

    /**
    *  double representing the median heading in degrees from north for all of the BSM's captured for a given vehicle in a given segment.
    */
    private double medianVehicleHeading;
    
    /**
    *  double representing the median distance from centerline in meters for all of the BSM's captured for a given vehicle in a given segment.
    */
    private double medianDistanceFromCenterline;

    /**
    *  int representing the number of BSM's messages that were aggregated to produce the vehicle heading and median distance from centerline. 
    */
    private int aggregateBSMCount;

    /**
    *  String representing the source of the data. Typically the RSU IP or Intersection ID
    */
    private String source;

    public LaneDirectionOfTravelEvent(){
        super("LaneDirectionOfTravel");
    }

    @JsonIgnore
    public String getKey(){
        return this.getIntersectionID() + "";
    }
}

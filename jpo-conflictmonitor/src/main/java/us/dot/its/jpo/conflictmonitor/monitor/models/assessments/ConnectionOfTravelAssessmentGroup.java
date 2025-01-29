package us.dot.its.jpo.conflictmonitor.monitor.models.assessments;

import com.fasterxml.jackson.annotation.JsonIgnore;

import lombok.Generated;
import lombok.Getter;
import lombok.Setter;
import us.dot.its.jpo.conflictmonitor.monitor.models.events.ConnectionOfTravelEvent;


/**
 * Connection of Travel Assessment groups represent the number of vehicles that passed through an intersection using a given combination of ingress and egress lanes.
 */
@Getter
@Setter
@Generated
public class ConnectionOfTravelAssessmentGroup {

    /**
     * The lane ID that vehicles take to exit the intersection.
    */
    private int ingressLaneID;

    /**
     * The lane ID that vehicles take to exit the intersection.
     */
    private int egressLaneID;

    /**
     * The connection ID representing the ingress / egress lane pair. A negative value (typically -1) is used if the ingress and egress lane are not connected in the MAP message.
     */
    private int connectionID; // may be empty

    /**
     * The number of vehicles that have used the ingress / egress lane pairing during this assessment cycle. 
     */
    private int eventCount;

    
    @JsonIgnore
    public void addConnectionOfTravelEvent(ConnectionOfTravelEvent event){
        this.eventCount +=1;
    }



}

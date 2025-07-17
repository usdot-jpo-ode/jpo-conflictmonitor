package us.dot.its.jpo.conflictmonitor.monitor.models.events;

import java.util.Set;
import lombok.EqualsAndHashCode;
import lombok.Generated;
import lombok.Getter;
import lombok.Setter;
import us.dot.its.jpo.conflictmonitor.monitor.models.RegulatorIntersectionId;



/**
* Intersection Reference Alignment Event - Intersection reference alignment events are generated when the road regulator ID's and intersection ID's defined in a matching pair of SPaT and MAP messages do not align with one another.  
*/
@Getter
@Setter
@EqualsAndHashCode(callSuper=true)
@Generated
public class IntersectionReferenceAlignmentEvent extends Event{
    
    /**
    *  String representing the source of the MAP and SPaT data. Typically the RSU IP address or intersection ID
    */
    private String source;

    /**
    *  long representing the utc timestamp in milliseconds from the SPaT / MAP message comparison
    */
    private long timestamp;

    /**
    *  Set or RegulatorIntersectionId objects containing all pairs of road regulator ID's and regions present in the SPaT message.
    */
    private Set<RegulatorIntersectionId> spatRegulatorIntersectionIds;

    /**
    *  Set or RegulatorIntersectionId objects containing all pairs of road regulator ID's and regions present in the MAP message.
    */
    private Set<RegulatorIntersectionId> mapRegulatorIntersectionIds;

    public IntersectionReferenceAlignmentEvent(){
        super("IntersectionReferenceAlignment");
    }

}

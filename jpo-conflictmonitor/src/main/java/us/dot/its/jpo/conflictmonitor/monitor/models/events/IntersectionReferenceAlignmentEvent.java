package us.dot.its.jpo.conflictmonitor.monitor.models.events;

import java.util.Set;
import lombok.EqualsAndHashCode;
import lombok.Generated;
import lombok.Getter;
import lombok.Setter;
import us.dot.its.jpo.conflictmonitor.monitor.models.RegulatorIntersectionId;
import org.springframework.data.mongodb.core.mapping.Document;


@Document("CmIntersectionReferenceAlignmentEvent")
@Getter
@Setter
@EqualsAndHashCode(callSuper=true)
@Generated
public class IntersectionReferenceAlignmentEvent extends Event{
    
    private String sourceID;
    private long timestamp;

    private Set<RegulatorIntersectionId> spatRegulatorIntersectionIds;
    private Set<RegulatorIntersectionId> mapRegulatorIntersectionIds;

    public IntersectionReferenceAlignmentEvent(){
        super("IntersectionReferenceAlignment");
    }

}

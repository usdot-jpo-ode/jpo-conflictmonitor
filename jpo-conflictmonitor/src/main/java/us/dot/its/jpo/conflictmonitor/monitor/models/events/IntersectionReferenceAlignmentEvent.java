package us.dot.its.jpo.conflictmonitor.monitor.models.events;

import java.util.Set;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import lombok.EqualsAndHashCode;
import lombok.Generated;
import lombok.Getter;
import lombok.Setter;
import us.dot.its.jpo.geojsonconverter.DateJsonMapper;
import org.springframework.data.mongodb.core.mapping.Document;


@Document("CmIntersectionReferenceAlignmentEvent")
@Getter
@Setter
@EqualsAndHashCode(callSuper=true)
@Generated
public class IntersectionReferenceAlignmentEvent extends Event{
    
    private String sourceID;
    private long timestamp;
    private Set<Integer> spatRoadRegulatorIds;
    private Set<Integer> mapRoadRegulatorIds;
    private Set<Integer> spatIntersectionIds;
    private Set<Integer> mapIntersectionIds;

    public IntersectionReferenceAlignmentEvent(){
        super("IntersectionReferenceAlignment");
    }

}

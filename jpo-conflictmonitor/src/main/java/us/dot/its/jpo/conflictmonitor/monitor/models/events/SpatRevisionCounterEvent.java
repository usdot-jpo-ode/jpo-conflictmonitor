package us.dot.its.jpo.conflictmonitor.monitor.models.events;

import lombok.EqualsAndHashCode;
import lombok.Generated;
import lombok.Getter;
import lombok.Setter;
import us.dot.its.jpo.geojsonconverter.pojos.spat.ProcessedSpat;

@Getter
@Setter
@EqualsAndHashCode(callSuper=true)
@Generated
public class SpatRevisionCounterEvent extends Event{

    private ProcessedSpat previousSpat;
    private ProcessedSpat newSpat;
    private String message;

    public SpatRevisionCounterEvent(){
        super("SpatRevisionCounter");
    }

}

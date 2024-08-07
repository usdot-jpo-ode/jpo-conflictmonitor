package us.dot.its.jpo.conflictmonitor.monitor.models.events;

import lombok.EqualsAndHashCode;
import lombok.Generated;
import lombok.Getter;
import lombok.Setter;
import us.dot.its.jpo.ode.model.OdeBsmData;

@Getter
@Setter
@EqualsAndHashCode(callSuper=true)
@Generated
public class BsmRevisionCounterEvent extends Event{

    private OdeBsmData previousBsm;
    private OdeBsmData newBsm;
    private String message;

    public BsmRevisionCounterEvent(){
        super("BsmRevisionCounter");
    }

}

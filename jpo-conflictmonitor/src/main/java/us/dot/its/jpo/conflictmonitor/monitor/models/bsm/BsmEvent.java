package us.dot.its.jpo.conflictmonitor.monitor.models.bsm;

import lombok.Data;
import lombok.Generated;
import us.dot.its.jpo.ode.model.OdeBsmData;
import us.dot.its.jpo.ode.util.JsonUtils;

@Data
@Generated
public class BsmEvent {
    private OdeBsmData startingBsm;
    private OdeBsmData endingBsm;
    private Long startingBsmTimestamp;
    private Long endingBsmTimestamp;

    public BsmEvent() {}

    public BsmEvent(OdeBsmData startingBsm){
        this.startingBsm = startingBsm;
    }

    public BsmEvent(OdeBsmData startingBsm, OdeBsmData endingBsm){
        this.startingBsm = startingBsm;
        this.endingBsm = endingBsm;
    }



    public String toString(){
        return JsonUtils.toJson(this, false);
    }


}

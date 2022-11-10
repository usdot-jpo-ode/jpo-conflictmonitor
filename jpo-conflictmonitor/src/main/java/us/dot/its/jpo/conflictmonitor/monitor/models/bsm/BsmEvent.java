package us.dot.its.jpo.conflictmonitor.monitor.models.bsm;

import us.dot.its.jpo.ode.model.OdeBsmData;
import us.dot.its.jpo.ode.util.JsonUtils;

public class BsmEvent {
    private OdeBsmData startingBsm;
    private OdeBsmData endingBsm;
    private Long startingBsmTimestamp;
    private Long endingBsmTimestamp;

    public BsmEvent(OdeBsmData startingBsm){
        this.startingBsm = startingBsm;
    }

    public BsmEvent(OdeBsmData startingBsm, OdeBsmData endingBsm){
        this.startingBsm = startingBsm;
        this.endingBsm = endingBsm;
    }

    public OdeBsmData getStartingBsm() {
        return startingBsm;
    }

    public void setStartingBsm(OdeBsmData startingBsm) {
        this.startingBsm = startingBsm;
    }

    public OdeBsmData getEndingBsm() {
        return endingBsm;
    }

    public void setEndingBsm(OdeBsmData endingBsm) {
        this.endingBsm = endingBsm;
    }

    public String toString(){
        return JsonUtils.toJson(this, false);
    }

    public void setStartingBsmTimestamp(Long startingBsmTimestamp){
        this.startingBsmTimestamp = startingBsmTimestamp;
    }

    public Long getStartingBsmTimestamp(){
        return startingBsmTimestamp;
    }

    public void setEndingBsmTimestamp(Long endingBsmTimestamp){
        this.endingBsmTimestamp = endingBsmTimestamp;
    }
    public Long getEndingBsmTimestamp(){
        return endingBsmTimestamp;
    }
}

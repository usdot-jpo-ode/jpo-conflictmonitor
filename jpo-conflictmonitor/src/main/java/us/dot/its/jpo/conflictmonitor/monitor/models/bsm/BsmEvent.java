package us.dot.its.jpo.conflictmonitor.monitor.models.bsm;

import us.dot.its.jpo.ode.model.OdeBsmData;

public class BsmEvent {
    public OdeBsmData startingBsm;
    public OdeBsmData endingBsm;

    public BsmEvent(){

    }

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
}

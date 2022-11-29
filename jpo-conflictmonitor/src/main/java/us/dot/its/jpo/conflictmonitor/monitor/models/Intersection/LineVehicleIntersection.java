package us.dot.its.jpo.conflictmonitor.monitor.models.Intersection;

import us.dot.its.jpo.ode.model.OdeBsmData;

public class LineVehicleIntersection {
    private Lane lane;
    private OdeBsmData bsm;
    
    public LineVehicleIntersection(Lane lane, OdeBsmData bsm) {
        this.lane = lane;
        this.bsm = bsm;
    }

    public OdeBsmData getBsm() {
        return bsm;
    }
    public void setBsm(OdeBsmData bsm) {
        this.bsm = bsm;
    }

    public Lane getLane() {
        return lane;
    }

    public void setLane(Lane lane) {
        this.lane = lane;
    }

    public String toString(){
        return "Line Vehicle Intersection. Line: " + lane + " BSM" + bsm;
    }
}

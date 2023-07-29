package us.dot.its.jpo.conflictmonitor.monitor.models.Intersection;

import lombok.Getter;
import lombok.Setter;
import us.dot.its.jpo.ode.model.OdeBsmData;
import java.util.List;

@Getter
@Setter
public class LineVehicleIntersection {

    private Lane lane;

    /**
     * The BSM nearest to the stop/start point
     */
    private OdeBsmData bsm;

    
    public LineVehicleIntersection(Lane lane, OdeBsmData bsm) {
        this.lane = lane;
        this.bsm = bsm;
    }

    @Override
    public String toString() {
        return "LineVehicleIntersection{" +
                "lane=" + lane +
                ", bsm=" + bsm +
                '}';
    }
}

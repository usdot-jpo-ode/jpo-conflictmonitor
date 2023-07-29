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

    /**
     * List of BSMs within the buffer distance of the stop/start point,
     * and that have the correct heading within the tolerance
     */
    private List<OdeBsmData> bsmList;
    
    public LineVehicleIntersection(Lane lane, OdeBsmData bsm) {
        this.lane = lane;
        this.bsm = bsm;
    }

    @Override
    public String toString() {
        return "LineVehicleIntersection{" +
                "lane=" + lane +
                ", bsm=" + bsm +
                ", bsmList=" + bsmList +
                '}';
    }
}

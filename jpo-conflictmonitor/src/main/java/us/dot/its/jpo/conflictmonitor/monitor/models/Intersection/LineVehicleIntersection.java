package us.dot.its.jpo.conflictmonitor.monitor.models.Intersection;

import lombok.Getter;
import lombok.Setter;
import us.dot.its.jpo.geojsonconverter.pojos.geojson.Point;
import us.dot.its.jpo.geojsonconverter.pojos.geojson.bsm.ProcessedBsm;


@Getter
@Setter
public class LineVehicleIntersection {

    private Lane lane;

    /**
     * The BSM nearest to the stop/start point
     */
    private ProcessedBsm<Point> bsm;

    
    public LineVehicleIntersection(Lane lane, ProcessedBsm<Point> bsm) {
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

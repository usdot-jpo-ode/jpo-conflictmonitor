package us.dot.its.jpo.conflictmonitor.monitor.models.Intersection;

import lombok.Getter;
import lombok.Setter;
import us.dot.its.jpo.geojsonconverter.pojos.geojson.Point;
import us.dot.its.jpo.geojsonconverter.pojos.geojson.bsm.ProcessedBsm;

/**
 * Represents the intersection of a vehicle (via BSM) with a lane line.
 * Stores the lane and the BSM data nearest to the stop/start point.
 */
@Getter
@Setter
public class LineVehicleIntersection {

    /** The lane involved in the intersection. */
    private Lane lane;

    /**
     * The BSM nearest to the stop/start point
     */
    private ProcessedBsm<Point> bsm;

    /**
     * Constructs a LineVehicleIntersection with the specified lane and BSM.
     *
     * @param lane the lane involved in the intersection
     * @param bsm  the BSM nearest to the stop/start point
     */
    public LineVehicleIntersection(Lane lane, ProcessedBsm<Point> bsm) {
        this.lane = lane;
        this.bsm = bsm;
    }

    /**
     * Returns a string representation of the LineVehicleIntersection.
     *
     * @return a string describing the lane and BSM
     */
    @Override
    public String toString() {
        return "LineVehicleIntersection{" +
                "lane=" + lane +
                ", bsm=" + bsm +
                '}';
    }
}

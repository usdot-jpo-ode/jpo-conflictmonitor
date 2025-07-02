package us.dot.its.jpo.conflictmonitor.monitor.models.Intersection;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import lombok.Getter;
import lombok.Setter;
import org.locationtech.jts.geom.*;
import org.locationtech.jts.geom.impl.PackedCoordinateSequence;
import org.locationtech.jts.io.WKTWriter;
import org.locationtech.jts.linearref.LengthIndexedLine;
import org.locationtech.jts.operation.buffer.BufferOp;
import org.locationtech.jts.operation.buffer.BufferParameters;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import us.dot.its.jpo.conflictmonitor.monitor.models.bsm.BsmAggregator;
import us.dot.its.jpo.conflictmonitor.monitor.utils.BsmUtils;
import us.dot.its.jpo.conflictmonitor.monitor.utils.CircleMath;
import us.dot.its.jpo.conflictmonitor.monitor.utils.CoordinateConversion;
import us.dot.its.jpo.geojsonconverter.pojos.geojson.bsm.ProcessedBsm;

/**
 * Represents the path of a vehicle through an intersection, constructed from a
 * sequence of BSMs.
 * Provides methods to analyze the path, determine ingress/egress lanes, and
 * filter BSMs based on spatial and speed criteria.
 */
@Getter
@Setter
public class VehiclePath {

    /** Logger for VehiclePath operations. */
    private static final Logger logger = LoggerFactory.getLogger(VehiclePath.class);

    /** The LineString representing the vehicle's path points. */
    private LineString pathPoints;
    /** Aggregator containing the BSMs for the vehicle. */
    private BsmAggregator bsms;
    /** The intersection associated with this vehicle path. */
    private Intersection intersection;
    /** GeometryFactory for creating geometric objects. */
    private GeometryFactory geometryFactory;

    /** The ingress lane the vehicle entered. */
    private Lane ingressLane;
    /** The egress lane the vehicle exited. */
    private Lane egressLane;
    /** The BSM nearest to the ingress point. */
    private ProcessedBsm<us.dot.its.jpo.geojsonconverter.pojos.geojson.Point> ingressBsm;
    /** The BSM nearest to the egress point. */
    private ProcessedBsm<us.dot.its.jpo.geojsonconverter.pojos.geojson.Point> egressBsm;

    /** Minimum distance in feet for spatial matching. */
    private double minDistanceFeet;
    /** Heading tolerance in degrees for matching. */
    private double headingToleranceDegrees;

    /**
     * Constructs a VehiclePath using the provided BSMs, intersection, and matching
     * parameters.
     *
     * @param bsms                    Aggregator containing the vehicle's BSMs
     * @param intersection            The intersection the vehicle is traversing
     * @param minDistanceFeet         Minimum distance in feet for spatial matching
     * @param headingToleranceDegrees Heading tolerance in degrees for matching
     */
    public VehiclePath(BsmAggregator bsms, Intersection intersection, double minDistanceFeet,
            double headingToleranceDegrees) {
        this.bsms = bsms;
        this.intersection = intersection;
        this.minDistanceFeet = minDistanceFeet;
        this.headingToleranceDegrees = headingToleranceDegrees;
        this.geometryFactory = new GeometryFactory();

        buildVehiclePath();
    }

    /**
     * Builds the vehicle path LineString from the BSMs and sets ingress/egress
     * information.
     */
    public void buildVehiclePath() {
        Coordinate referencePoint = this.intersection.getReferencePoint();

        if (referencePoint == null) {
            logger.error("Reference point is null");
            return;
        }

        Coordinate[] vehicleCoords = new Coordinate[bsms.getBsms().size()];
        int index = 0;
        for (ProcessedBsm<us.dot.its.jpo.geojsonconverter.pojos.geojson.Point> bsm : bsms.getBsms()) {

            CoordinateXY coords = BsmUtils.getPosition(bsm);

            double[] shiftedPosition = CoordinateConversion.longLatToOffsetCM(
                    coords.getX(),
                    coords.getY(),
                    referencePoint.getX(),
                    referencePoint.getY());
            vehicleCoords[index] = new Coordinate(shiftedPosition[0], shiftedPosition[1]);
            index++;
        }

        PackedCoordinateSequence.Double sequence = new PackedCoordinateSequence.Double(vehicleCoords);
        this.pathPoints = new LineString(sequence, this.geometryFactory);
        this.calculateIngress();
        this.calculateEgress();
    }

    /**
     * Calculates and sets the ingress lane and BSM for this vehicle path.
     */
    public void calculateIngress() {
        if (intersection.getStopLines() == null)
            return;
        LineVehicleIntersection match = findLineVehicleIntersection(this.intersection.getStopLines(), minDistanceFeet,
                headingToleranceDegrees);
        if (match != null) {
            this.ingressLane = match.getLane();
            this.ingressBsm = match.getBsm();
        }
    }

    /**
     * Calculates and sets the egress lane and BSM for this vehicle path.
     */
    public void calculateEgress() {
        if (intersection.getStartLines() == null)
            return;
        LineVehicleIntersection match = findLineVehicleIntersection(this.intersection.getStartLines(), minDistanceFeet,
                headingToleranceDegrees);
        if (match != null) {
            this.egressLane = match.getLane();
            this.egressBsm = match.getBsm();
        }
    }

    /**
     * Find the stop or start line point that the vehicle path passes closest to
     * 
     * @param lines                   - List of ingress or egress lines with stop or
     *                                start points
     * @param minDistanceFeet         - Radius of the buffer at stop/start points to
     *                                include BSMs
     * @param headingToleranceDegrees - Tolerance for heading to include BSMs
     * @return LineVehicleIntersection
     */
    public LineVehicleIntersection findLineVehicleIntersection(
            List<IntersectionLine> lines,
            final double minDistanceFeet,
            final double headingToleranceDegrees) {

        final double minDistanceCM = CoordinateConversion.feetToCM(minDistanceFeet);
        double minDistance = Double.MAX_VALUE;
        ProcessedBsm<us.dot.its.jpo.geojsonconverter.pojos.geojson.Point> matchingBsm = null;
        IntersectionLine bestLine = null;

        for (IntersectionLine line : lines) {
            if (this.pathPoints.isWithinDistance(line.getStopLinePoint(), minDistanceCM)) {
                int index = 0;
                for (ProcessedBsm<us.dot.its.jpo.geojsonconverter.pojos.geojson.Point> bsm : this.bsms.getBsms()) {
                    Point p = this.pathPoints.getPointN(index);
                    var optionalHeading = BsmUtils.getHeading(bsm);
                    if (optionalHeading.isEmpty()) {
                        logger.warn("No heading found in BSM");
                        continue;
                    }
                    double vehicleHeading = optionalHeading.get();
                    if (CircleMath.getAngularDistanceDegrees(vehicleHeading,
                            line.getHeading()) <= headingToleranceDegrees) {
                        double distance = p.distance(line.getStopLinePoint());
                        if (distance < minDistance) {
                            matchingBsm = bsm;
                            minDistance = distance;
                            bestLine = line;
                        }
                    }
                    index++;
                }
            }
        }

        if (bestLine != null) {
            return new LineVehicleIntersection(bestLine.getLane(), matchingBsm);
        } else {
            return null;
        }
    }

    /**
     * Finds BSMs that are in the specified ingress lane and within the upstream
     * search distance.
     *
     * @param lane                       The ingress lane to search
     * @param upstreamSearchDistanceFeet The upstream distance along the lane to
     *                                   search (in feet)
     * @return List of List<ProcessedBsm> within the lane and distance
     */
    public List<ProcessedBsm<us.dot.its.jpo.geojsonconverter.pojos.geojson.Point>> findBsmsInIngressLane(Lane lane,
            final double upstreamSearchDistanceFeet) {
        final double upstreamSearchDistanceCm = CoordinateConversion.feetToCM(upstreamSearchDistanceFeet);
        final double laneWidthCm = lane.getLaneWidthCm();
        final double halfLaneWidthCm = laneWidthCm / 2.0;

        LineString laneCenterline = lane.getPoints();
        LengthIndexedLine indexedLine = new LengthIndexedLine(laneCenterline);
        LineString upstreamLine = (LineString) indexedLine.extractLine(0, upstreamSearchDistanceCm * 10);

        // Construct a buffer around the upstream line based on the lane width
        BufferParameters bufferParams = new BufferParameters();
        bufferParams.setEndCapStyle(BufferParameters.CAP_SQUARE);
        Geometry buffer = BufferOp.bufferOp(upstreamLine, halfLaneWidthCm, bufferParams);

        // Find the BSMs that are within the buffer
        List<ProcessedBsm<us.dot.its.jpo.geojsonconverter.pojos.geojson.Point>> bsmsInLane = new ArrayList<>();
        int index = 0;
        for (ProcessedBsm<us.dot.its.jpo.geojsonconverter.pojos.geojson.Point> bsm : this.bsms.getBsms()) {
            Point p = this.pathPoints.getPointN(index);

            if (buffer.contains(p)) {
                bsmsInLane.add(bsm);
            }
            index++;
        }
        logger.info("Found {} BSMs in lane {}", bsmsInLane.size(), lane);
        return bsmsInLane;
    }

    /**
     * Filters the provided BSMs to include only those during a stoppage.
     * Finds the first and last BSMs below the stop speed threshold and returns all
     * BSMs between them.
     *
     * @param bsmList               List of BSMs to filter
     * @param stopSpeedThresholdMPH Speed threshold in MPH to consider a vehicle
     *                              stopped
     * @return List of ProcessedBsm<Point> representing the stoppage period
     */
    public List<ProcessedBsm<us.dot.its.jpo.geojsonconverter.pojos.geojson.Point>> filterStoppedBsms(
            final List<ProcessedBsm<us.dot.its.jpo.geojsonconverter.pojos.geojson.Point>> bsmList,
            final double stopSpeedThresholdMPH) {
        List<Integer> stoppedBsmIndices = new ArrayList<>();

        // Find indexes of all BSMs below the stop speed threshold
        for (int i = 0; i < bsmList.size(); i++) {
            ProcessedBsm<us.dot.its.jpo.geojsonconverter.pojos.geojson.Point> bsm = bsmList.get(i);
            Optional<Double> optionalSpeed = BsmUtils.getSpeedMPH(bsm);

            if (optionalSpeed.isEmpty()) {
                logger.warn("No speed found in BSM");
                continue;
            }
            double speed = optionalSpeed.get();
            if (speed <= stopSpeedThresholdMPH) {
                stoppedBsmIndices.add(i);
            }
        }

        List<ProcessedBsm<us.dot.its.jpo.geojsonconverter.pojos.geojson.Point>> filteredBsmList = new ArrayList<>();

        // Must be at least 2 BSMs to find stop and start timestamps
        if (stoppedBsmIndices.isEmpty() || stoppedBsmIndices.size() < 2) {
            return filteredBsmList;
        }

        // Return all BSMs between the first and last stopped BSMs
        int startIndex = stoppedBsmIndices.get(0);
        int endIndex = stoppedBsmIndices.get(stoppedBsmIndices.size() - 1);

        var subList = bsmList.subList(startIndex, endIndex);
        filteredBsmList.addAll(subList);
        logger.info("Found {} stopped BSMs", filteredBsmList.size());
        return filteredBsmList;
    }

    /**
     * Returns the vehicle path as a Well-Known Text (WKT) string.
     *
     * @return WKT representation of the vehicle path
     */
    public String getVehiclePathAsWkt() {
        WKTWriter writer = new WKTWriter(2);
        String wtkOut = "wtk\n";
        writer.setFormatted(true);

        wtkOut += "\"" + writer.writeFormatted(this.pathPoints) + "\"\n";

        return wtkOut;
    }

    /**
     * Returns a string representation of the VehiclePath.
     *
     * @return String describing the number of points in the path
     */
    @Override
    public String toString() {
        return "Vehicle Path: Points: " + this.pathPoints.getNumPoints();
    }

}

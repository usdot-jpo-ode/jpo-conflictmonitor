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
import us.dot.its.jpo.geojsonconverter.pojos.spat.ProcessedSpat;


@Getter
@Setter
public class VehiclePath {

    private static final Logger logger = LoggerFactory.getLogger(VehiclePath.class);
    
    private LineString pathPoints;
    private BsmAggregator bsms;
    private Intersection intersection;
    private GeometryFactory geometryFactory;

    private Lane ingressLane;
    private Lane egressLane;
    private ProcessedBsm<us.dot.its.jpo.geojsonconverter.pojos.geojson.Point> ingressBsm;
    private ProcessedBsm<us.dot.its.jpo.geojsonconverter.pojos.geojson.Point> egressBsm;

    private double minDistanceFeet;
    private double headingToleranceDegrees;

    private ProcessedSpat spat;

    public VehiclePath(BsmAggregator bsms, Intersection intersection, double minDistanceFeet, double headingToleranceDegrees,
                       ProcessedSpat spat){
        this.bsms = bsms;
        this.intersection = intersection;
        this.minDistanceFeet = minDistanceFeet;
        this.headingToleranceDegrees = headingToleranceDegrees;
        this.geometryFactory = new GeometryFactory();
        this.spat = spat;
        buildVehiclePath();
    }

    public void buildVehiclePath(){
        Coordinate referencePoint = this.intersection.getReferencePoint();

        if (referencePoint == null) {
            logger.error("Reference point is null");
            return;
        }
       
        Coordinate[] vehicleCoords = new Coordinate[bsms.getBsms().size()];
        int index =0;
        for(ProcessedBsm<us.dot.its.jpo.geojsonconverter.pojos.geojson.Point> bsm : bsms.getBsms()){
            
            CoordinateXY coords = BsmUtils.getPosition(bsm);

            double[] shiftedPosition = CoordinateConversion.longLatToOffsetCM(
                coords.getX(),
                coords.getY(),
                referencePoint.getX(),
                referencePoint.getY()
            );
            vehicleCoords[index] = new Coordinate(shiftedPosition[0], shiftedPosition[1]);
            index++;
        }
        
        PackedCoordinateSequence.Double sequence = new PackedCoordinateSequence.Double(vehicleCoords);
        this.pathPoints = new LineString(sequence, this.geometryFactory);
        this.calculateIngress();
        this.calculateEgress();
    }

    public void calculateIngress(){
        if (intersection.getStopLines() == null) return;
        LineVehicleIntersection match = findLineVehicleIntersection(this.intersection.getStopLines(), minDistanceFeet,
                headingToleranceDegrees);
        if(match != null){
            this.ingressLane = match.getLane();
            this.ingressBsm = match.getBsm();
        }
    }

    public void calculateEgress(){
        if (intersection.getStartLines() == null) return;
        LineVehicleIntersection match = findLineVehicleIntersection(this.intersection.getStartLines(), minDistanceFeet,
                headingToleranceDegrees);
        if(match != null){
            this.egressLane = match.getLane();
            this.egressBsm = match.getBsm();
        }
    }


    /**
     * Find the stop or start line point that the vehicle path passes closest to
     * @param lines - List of ingress or egress lines with stop or start points
     * @param minDistanceFeet - Radius of the buffer at stop/start points to include BSMs
     * @param headingToleranceDegrees - Tolerance for heading to include BSMs
     * @return LineVehicleIntersection
     */
    public LineVehicleIntersection findLineVehicleIntersection(
            List<IntersectionLine> lines,
            final double minDistanceFeet,
            final double headingToleranceDegrees){

        final double minDistanceCM = CoordinateConversion.feetToCM(minDistanceFeet);
        double minDistance = Double.MAX_VALUE;
        ProcessedBsm<us.dot.its.jpo.geojsonconverter.pojos.geojson.Point> matchingBsm = null;
        IntersectionLine bestLine = null;


        for(IntersectionLine line : lines){

            // If the is revocable in the map and disabled in the spat, ignore them
            final var laneId = line.getLane().getId();
            final var revocable = intersection.getRevocableLaneIds();
            final var enabled = spat.getEnabledLanes();
            if (revocable != null && revocable.contains(laneId)
                    && (enabled == null || !enabled.contains(laneId))) {
                // Disable revocable: skip it
                continue;
            }

            if(this.pathPoints.isWithinDistance(line.getStopLinePoint(), minDistanceCM)){
                int index =0;
                for(ProcessedBsm<us.dot.its.jpo.geojsonconverter.pojos.geojson.Point> bsm : this.bsms.getBsms()){
                    Point p = this.pathPoints.getPointN(index);
                    var optionalHeading = BsmUtils.getHeading(bsm);
                    if (optionalHeading.isEmpty()) {
                        logger.warn("No heading found in BSM");
                        continue;
                    }
                    double vehicleHeading = optionalHeading.get();
                    if(CircleMath.getAngularDistanceDegrees(vehicleHeading, line.getHeading()) <= headingToleranceDegrees){
                        double distance = p.distance(line.getStopLinePoint());
                        if(distance < minDistance){
                            matchingBsm = bsm;
                            minDistance = distance;
                            bestLine = line;
                        }
                    }
                    index++;
                }
            }
        }



        if(bestLine != null){
            return new LineVehicleIntersection(bestLine.getLane(), matchingBsm);
        } else{
            return null;
        }
    }

    /**
     * Find the BSMs that are in the ingress lane and within the upstream search distance
     * @param lane - The ingress lane to search
     * @param upstreamSearchDistanceFeet - The upstream distance along the lane to search
     * @return List<ProcessedBsm>
     */
    public List<ProcessedBsm<us.dot.its.jpo.geojsonconverter.pojos.geojson.Point>>
    findBsmsInIngressLane(Lane lane, final double upstreamSearchDistanceFeet) {
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
     * Filter the BSMs to include only those during a stoppage.
     *
     * <p>Find the first BSM where the speed is below the threshold, and the last BSM where the speed is below the threshold,
     * and all the BSMs between those two by time.
     *
     * @param bsmList
     * @param stopSpeedThresholdMPH
     * @return
     */
    public List<ProcessedBsm<us.dot.its.jpo.geojsonconverter.pojos.geojson.Point>>
    filterStoppedBsms(
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
            // logger.info("Evaluating BSM Speed:" + speed + "Stop Speed Threshold:" + stopSpeedThresholdMPH);
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


    
    public String getVehiclePathAsWkt() {
        WKTWriter writer = new WKTWriter(2);
        String wtkOut = "wtk\n";
        writer.setFormatted(true);

        wtkOut += "\"" + writer.writeFormatted(this.pathPoints) + "\"\n";

        return wtkOut;
    }

    @Override
    public String toString(){
        return "Vehicle Path: Points: " + this.pathPoints.getNumPoints();
    }

}

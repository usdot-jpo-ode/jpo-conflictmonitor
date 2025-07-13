package us.dot.its.jpo.conflictmonitor.monitor.models.intersection;

import org.junit.Test;
import org.locationtech.jts.geom.CoordinateXY;
import org.locationtech.jts.geom.GeometryFactory;
import us.dot.its.jpo.conflictmonitor.monitor.models.Intersection.Intersection;
import us.dot.its.jpo.conflictmonitor.monitor.models.Intersection.IntersectionLine;
import us.dot.its.jpo.conflictmonitor.monitor.models.Intersection.Lane;
import us.dot.its.jpo.conflictmonitor.monitor.models.Intersection.VehiclePath;
import us.dot.its.jpo.conflictmonitor.monitor.models.bsm.BsmAggregator;
import us.dot.its.jpo.conflictmonitor.testutils.BsmTestUtils;
import us.dot.its.jpo.geojsonconverter.pojos.spat.ProcessedSpat;

import java.time.Instant;
import java.util.Collections;

public class VehiclePathTest {

    final long startMillis = 1682615309868L;
    final long endMillis = 1682615347488L;
    final String bsmId = "A0A0A0";
    final double refLon = -105.09141081;
    final double refLat = 39.59524124;
    final double startLon = -105.09127373;
    final double startLat = 39.59504968;
    final double endLon = -105.09126715;
    final double endLat = 39.59546353;

    final double elevation = 1500.1;

    final double minDistanceFeet = 15.0;
    final double headingToleranceDegrees = 20.0;

    @Test
    public void testBuildVehiclePath_NullReferencePoint() {
        var bsmAggregator = getBsms();
        var intersection = getIntersection();
        intersection.setReferencePoint(null);
        var spat = new ProcessedSpat();
        var vehiclePath = new VehiclePath(bsmAggregator, intersection, minDistanceFeet, headingToleranceDegrees, spat);

        // Should not throw any exceptions
        vehiclePath.buildVehiclePath();
    }

    @Test
    public void testBuildVehiclePath_NullStartStopLines() {
        var bsmAggregator = getBsms();
        var intersection = getIntersection();
        intersection.setStopLines(null);
        intersection.setStartLines(null);
        var spat = new ProcessedSpat();
        var vehiclePath = new VehiclePath(bsmAggregator, intersection, minDistanceFeet, headingToleranceDegrees, spat);

        // Should not throw any exceptions
        vehiclePath.buildVehiclePath();
    }

    @Test
    public void testBuildVehiclePath() {
        var bsmAggregator = getBsms();
        var intersection = getIntersection();
        var spat = new ProcessedSpat();
        var vehiclePath = new VehiclePath(bsmAggregator, intersection, minDistanceFeet, headingToleranceDegrees, spat);

        // Should not throw any exceptions
        vehiclePath.buildVehiclePath();
    }



    private BsmAggregator getBsms() {
        final var startBsm = BsmTestUtils.processedBsmWithPosition(Instant.ofEpochMilli(startMillis), bsmId, startLon, startLat, elevation);
        final var endBsm = BsmTestUtils.processedBsmWithPosition(Instant.ofEpochMilli(endMillis), bsmId, endLon, endLat, elevation);
        var bsmAggregator = new BsmAggregator();
        bsmAggregator.add(startBsm);
        bsmAggregator.add(endBsm);
        return bsmAggregator;
    }



    private Intersection getIntersection() {
        var factory = new GeometryFactory();
        var intersection = new Intersection();
        intersection.setReferencePoint(new CoordinateXY(refLon, refLat));

        var startLane = new Lane();
        startLane.setId(10);
        var stopLane = new Lane();
        stopLane.setId(20);

        var startLine = new IntersectionLine(factory.createPoint(new CoordinateXY(startLon, startLat)), 0.0d, startLane);
        intersection.setStartLines(Collections.singletonList(startLine));

        var stopLine = new IntersectionLine(factory.createPoint(new CoordinateXY(endLon, endLat)), 0.0d, stopLane);
        intersection.setStartLines(Collections.singletonList(stopLine));
        return intersection;
    }


}

package us.dot.its.jpo.conflictmonitor.monitor.utils;

import lombok.extern.slf4j.Slf4j;
import org.locationtech.jts.geom.CoordinateXY;
import us.dot.its.jpo.geojsonconverter.pojos.geojson.Point;
import us.dot.its.jpo.geojsonconverter.pojos.geojson.bsm.BsmProperties;
import us.dot.its.jpo.geojsonconverter.pojos.geojson.bsm.ProcessedBsm;

import java.util.Optional;

@Slf4j
public class BsmUtils {



    public static CoordinateXY getPosition(ProcessedBsm<Point> processedBsm) {
        CoordinateXY position = new CoordinateXY();
        Optional<Point> optionalPoint = getGeometry(processedBsm);
        if (optionalPoint.isEmpty()) return position;
        Point point = optionalPoint.get();
        double[] coordinates = point.getCoordinates();
        if (coordinates == null || coordinates.length < 2) return position;
        position.setX(coordinates[0]);
        position.setY(coordinates[1]);
        return position;
    }



    /**
     * Get geometry
     * @param processedBsm The ProcessedBsm
     * @return The Point geometry, or empty if geometry is not a geojson Point.
     */
    public static Optional<Point> getGeometry(ProcessedBsm<?> processedBsm) {
        Object geom = processedBsm.getGeometry();
        if (geom == null) return Optional.empty();
        if (geom instanceof Point point) {
            return Optional.of(point);
        } else {
            return Optional.empty();
        }
    }

    public static Optional<BsmProperties> getProperties(ProcessedBsm<?> processedBsm) {
        BsmProperties properties = processedBsm.getProperties();
        if (properties == null) return Optional.empty();
        return Optional.of(properties);
    }

    public static Optional<Double> getHeading(ProcessedBsm<?> processedBsm) {
        Optional<BsmProperties> optProps = getProperties(processedBsm);
        if (optProps.isEmpty()) return Optional.empty();
        BsmProperties coreData = optProps.get();
        if (coreData.getHeading() == null) return Optional.empty();
        double vehicleHeading = coreData.getHeading().doubleValue();
        return Optional.of(vehicleHeading);
    }

    public static Optional<Double> getSpeedMPH(ProcessedBsm<?> processedBsm) {
        Optional<BsmProperties> optProps = getProperties(processedBsm);
        if (optProps.isEmpty()) return Optional.empty();
        BsmProperties props = optProps.get();
        if (props.getSpeed() == null) return Optional.empty();
        double speedMetersPerSecond = props.getSpeed().doubleValue();
        double speedMPH = speedMetersPerSecond * 2.23694; // convert m/s to mph
        return Optional.of(speedMPH);
    }

    public static String getVehicleId(ProcessedBsm<?> processedBsm) {
        Optional<BsmProperties> optProps = getProperties(processedBsm);
        if (optProps.isEmpty()) return "";
        BsmProperties props = optProps.get();
        return props.getId();
    }

}

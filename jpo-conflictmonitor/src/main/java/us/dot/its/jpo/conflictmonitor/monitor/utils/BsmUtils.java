package us.dot.its.jpo.conflictmonitor.monitor.utils;

import lombok.extern.slf4j.Slf4j;
import org.locationtech.jts.geom.CoordinateXY;
import us.dot.its.jpo.geojsonconverter.pojos.geojson.Point;
import us.dot.its.jpo.geojsonconverter.pojos.geojson.bsm.BsmProperties;
import us.dot.its.jpo.geojsonconverter.pojos.geojson.bsm.ProcessedBsm;

import java.util.Optional;

@Slf4j
public class BsmUtils {


    /**
     * @param processedBsm {@link ProcessedBsm<Point>} object to extract the position from. Returns a
     *            CoordinateXY object with the BSMs longitude and Latitude position.
     *            If the BSM doesn't have a defined position, this method returns an
     *            empty CoordinateXY
     * @return CoordinateXY representing the BSM's position
     */
    public static CoordinateXY getPosition(ProcessedBsm<Point> processedBsm) {
        CoordinateXY position = new CoordinateXY();
        Optional<Point> optionalPoint = getGeometry(processedBsm);
        if (optionalPoint.isEmpty()) return position;
        Point point = optionalPoint.get();
        Double[] coordinates = point.getCoordinates();
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

    /**
     * Extracts the properties from the given ProcessedBsm object.
     * 
     * @param processedBsm The ProcessedBsm object to extract properties from.
     * @return {@link Optional<BsmProperties>} containing the properties of the ProcessedBsm
     */
    public static Optional<BsmProperties> getProperties(ProcessedBsm<?> processedBsm) {
        BsmProperties properties = processedBsm.getProperties();
        if (properties == null) return Optional.empty();
        return Optional.of(properties);
    }

    /**
     * @param processedBsm {@link ProcessedBsm<?>} object to extract the heading from. If the BSM is
     *            missing a heading, no value will be populated into the optional.
     * @return {@link Optional<Double>} representing the heading of the vehicle from the BSM
     */
    public static Optional<Double> getHeading(ProcessedBsm<?> processedBsm) {
        Optional<BsmProperties> optProps = getProperties(processedBsm);
        if (optProps.isEmpty()) return Optional.empty();
        BsmProperties coreData = optProps.get();
        if (coreData.getHeading() == null) return Optional.empty();
        double vehicleHeading = coreData.getHeading().doubleValue();
        return Optional.of(vehicleHeading);
    }

    /**
     * This function extracts the speed of a BSM and converts it to Miles per Hour
     * 
     * @param processedBsm {@link ProcessedBsm<?>} object to extract the speed from from.
     * @return Returns an {@link Optional<Double>} where the
     *         double represents the speed. If the BSM doesn't have a defined speed,
     *         this method returns an empty Optional.
     */
    public static Optional<Double> getSpeedMPH(ProcessedBsm<?> processedBsm) {
        Optional<BsmProperties> optProps = getProperties(processedBsm);
        if (optProps.isEmpty()) return Optional.empty();
        BsmProperties props = optProps.get();
        if (props.getSpeed() == null) return Optional.empty();
        double speedMetersPerSecond = props.getSpeed().doubleValue();
        double speedMPH = speedMetersPerSecond * 2.23694; // convert m/s to mph
        return Optional.of(speedMPH);
    }

    /**
     * This function extracts the vehicleID of a BSM.
     * 
     * @param processedBsm {@link ProcessedBsm<?>} object to extract the vehicleID from from.
     * @return Returns a String where the
     *         string represents the vehicleID. If the BSM doesn't have a defined
     *         vehicleID, this method returns an empty string.
     */
    public static String getVehicleId(ProcessedBsm<?> processedBsm) {
        Optional<BsmProperties> optProps = getProperties(processedBsm);
        if (optProps.isEmpty()) return "";
        BsmProperties props = optProps.get();
        return props.getId();
    }

}
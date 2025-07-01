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
     * @param bsm OdeBsmData object to extract the position from. Returns a
     *            CoordinateXY object with the BSMs longitude and Latitude position.
     *            If the BSM doesn't have a defined position, this method returns an
     *            empty CoordinateXY
     * @return CoordinateXY representing the BSM's position
     */
    public static CoordinateXY getPosition(ProcessedBsm<Point> processedBsm) {
        CoordinateXY position = new CoordinateXY();
        Optional<Point> optionalPoint = getGeometry(processedBsm);
        if (optionalPoint.isEmpty())
            return position;
        Point point = optionalPoint.get();
        double[] coordinates = point.getCoordinates();
        if (coordinates == null || coordinates.length < 2)
            return position;
        position.setX(coordinates[0]);
        position.setY(coordinates[1]);
        return position;
    }

    /**
     * @param bsm OdeBsmData object to extract the heading from. If the BSM is
     *            missing a heading, no value will be populated into the optional.
     * @return Optional<Double> representing the heading of the vehicle from the BSM
     */
    public static Optional<Double> getHeading(ProcessedBsm<?> processedBsm) {
        Optional<BsmProperties> optProps = getProperties(processedBsm);
        if (optProps.isEmpty())
            return Optional.empty();
        BsmProperties coreData = optProps.get();
        if (coreData.getHeading() == null)
            return Optional.empty();
        double vehicleHeading = coreData.getHeading().doubleValue();
        return Optional.of(vehicleHeading);
    }

    /**
     * This function safely extracts the J2735Bsm data object from the supplied
     * OdeBsmData object. Returns an empty optional if no J2735Bsm can be safetly
     * parsed.
     * 
     * @param bsm OdeBsmData object to extract the J2735OdeBsmData from
     * @return CoordinateXY
     */
    public static Optional<J2735Bsm> getJ2735Bsm(OdeBsmData bsm) {
        if (bsm == null)
            return Optional.empty();
        if (bsm.getPayload() == null) {
            return Optional.empty();
        }
        if (!(bsm.getPayload() instanceof OdeBsmPayload)) {
            return Optional.empty();
        }
        var payload = (OdeBsmPayload) bsm.getPayload();
        if (payload.getBsm() == null)
            return Optional.empty();
        return Optional.of(payload.getBsm());
    }

    /**
     * This function safely extracts the J2735BsmCoreData object from the supplied
     * OdeBsmData object.
     * 
     * @param bsm OdeBsmData object to extract the position from.
     * @return Optional<J2735BsmCoreData> Returns an Optional<J2735BsmCoreData> If
     *         the BSM doesn't have a defined position, this method returns an empty
     *         Optional.
     */
    public static Optional<J2735BsmCoreData> getCoreData(OdeBsmData bsm) {
        Optional<J2735Bsm> optionalBsm = getJ2735Bsm(bsm);
        if (optionalBsm.isEmpty()) {
            return Optional.empty();
        }
        J2735Bsm j2735Bsm = optionalBsm.get();
        if (j2735Bsm.getCoreData() == null) {
            return Optional.empty();
        }
        return Optional.of(j2735Bsm.getCoreData());
    }

    /**
     * This function extracts the speed of a BSM and converts it to Miles per Hour
     * 
     * @param bsm OdeBsmData object to extract the speed from from.
     * @return Optional<J2735BsmCoreData> Returns an Optional<double> where the
     *         double represents the speed. If the BSM doesn't have a defined speed,
     *         this method returns an empty Optional.
     */
    public static Optional<Double> getSpeedMPH(ProcessedBsm<?> processedBsm) {
        Optional<BsmProperties> optProps = getProperties(processedBsm);
        if (optProps.isEmpty())
            return Optional.empty();
        BsmProperties props = optProps.get();
        if (props.getSpeed() == null)
            return Optional.empty();
        double speedMetersPerSecond = props.getSpeed().doubleValue();
        double speedMPH = speedMetersPerSecond * 2.23694; // convert m/s to mph
        return Optional.of(speedMPH);
    }

    /**
     * This function extracts the vehicleID of a BSM.
     * 
     * @param bsm OdeBsmData object to extract the vehicleID from from.
     * @return Optional<J2735BsmCoreData> Returns an Optional<String> where the
     *         string represents the vehicleID. If the BSM doesn't have a defined
     *         vehicleID, this method returns an empty Optional.
     */
    public static String getVehicleId(ProcessedBsm<?> processedBsm) {
        Optional<BsmProperties> optProps = getProperties(processedBsm);
        if (optProps.isEmpty())
            return "";
        BsmProperties props = optProps.get();
        return props.getId();
    }

    /**
     * This function returns the RSU IP that received the BSM message
     * 
     * @param bsm OdeBsmData object to extract the RsuIp from from.
     * @return String Returns an String where the string represents the originIp of
     *         the BSM. If no origin IP is set the string is left empty.
     */
    public static String getRsuIp(OdeBsmData bsm) {
        String ip = "";
        if (bsm.getMetadata() != null && bsm.getMetadata() instanceof OdeBsmMetadata) {
            var metadata = (OdeBsmMetadata) bsm.getMetadata();
            ip = metadata.getOriginIp();
        }
        return ip;
    }

    /**
     * This function returns the time at which the OdeReceived the given BSM
     * 
     * @param bsm OdeBsmData object to extract the RsuIp from from.
     * @return String Returns an String where the string represents the originIp of
     *         the BSM. If no origin IP is set the string is left empty.
     */
    public static long getOdeReceivedAt(OdeBsmData bsm) {
        long odeReceivedAt = 0;
        if (bsm != null && bsm.getMetadata() != null && bsm.getMetadata() instanceof OdeBsmMetadata metadata) {
            String strOdeReceivedAt = metadata.getOdeReceivedAt();
            assert (strOdeReceivedAt != null);
            try {
                odeReceivedAt = Instant.parse(strOdeReceivedAt).toEpochMilli();
            } catch (DateTimeParseException ex) {
                log.error(String.format("Error parsing odeReceivedAt: %s", strOdeReceivedAt), ex);
            }
        }
        return odeReceivedAt;
    }
}

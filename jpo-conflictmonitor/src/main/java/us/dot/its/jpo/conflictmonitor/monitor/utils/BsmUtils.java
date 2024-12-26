package us.dot.its.jpo.conflictmonitor.monitor.utils;

import lombok.extern.slf4j.Slf4j;
import org.locationtech.jts.geom.CoordinateXY;
import us.dot.its.jpo.geojsonconverter.pojos.geojson.Point;
import us.dot.its.jpo.geojsonconverter.pojos.geojson.bsm.BsmFeature;
import us.dot.its.jpo.geojsonconverter.pojos.geojson.bsm.BsmProperties;
import us.dot.its.jpo.geojsonconverter.pojos.geojson.bsm.ProcessedBsm;
import us.dot.its.jpo.ode.model.OdeBsmData;
import us.dot.its.jpo.ode.model.OdeBsmMetadata;
import us.dot.its.jpo.ode.model.OdeBsmPayload;
import us.dot.its.jpo.ode.plugin.j2735.J2735Bsm;
import us.dot.its.jpo.ode.plugin.j2735.J2735BsmCoreData;
import us.dot.its.jpo.ode.plugin.j2735.OdePosition3D;

import java.time.Instant;
import java.time.format.DateTimeParseException;
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

    public static Optional<BsmFeature<?>> getFeature(ProcessedBsm<?> processedBsm) {
        if (processedBsm == null) return Optional.empty();
        if (processedBsm.getFeatures() == null || processedBsm.getFeatures().length < 1) return Optional.empty();
        return Optional.of(processedBsm.getFeatures()[0]);
    }

    /**
     * Get geometry
     * @param processedBsm The ProcessedBsm
     * @return The Point geometry, or empty if geometry is not a geojson Point.
     */
    public static Optional<Point> getGeometry(ProcessedBsm<?> processedBsm) {
        Optional<BsmFeature<?>> optionalFeature = getFeature(processedBsm);
        if (optionalFeature.isEmpty()) return Optional.empty();
        BsmFeature<?> feature = optionalFeature.get();
        Object geom = feature.getGeometry();
        if (geom == null) return Optional.empty();
        if (geom instanceof Point point) {
            return Optional.of(point);
        } else {
            return Optional.empty();
        }
    }

    public static Optional<BsmProperties> getProperties(ProcessedBsm<?> processedBsm) {
        Optional<BsmFeature<?>> optFeature = getFeature(processedBsm);
        if (optFeature.isEmpty()) return Optional.empty();
        BsmFeature<?> feature = optFeature.get();
        BsmProperties properties = feature.getProperties();
        if (properties == null) return Optional.empty();
        return Optional.of(properties);
    }



    public static CoordinateXY getPosition(OdeBsmData bsm) {
        CoordinateXY position = new CoordinateXY();
        Optional<J2735BsmCoreData> optionalCoreData = getCoreData(bsm);
        if (optionalCoreData.isEmpty()) return position;
        J2735BsmCoreData coreData = optionalCoreData.get();
        OdePosition3D pos = coreData.getPosition();
        if (pos == null) return position;
        position.setX(pos.getLongitude() != null ? pos.getLongitude().doubleValue() : 0.0);
        position.setY(pos.getLatitude() != null ? pos.getLatitude().doubleValue() : 0.0);
        return position;
    }

    public static Optional<Double> getHeading(OdeBsmData bsm) {
        Optional<J2735BsmCoreData> optionalCoreData = getCoreData(bsm);
        if (optionalCoreData.isEmpty()) return Optional.empty();
        J2735BsmCoreData coreData = optionalCoreData.get();
        if (coreData.getHeading() == null) return Optional.empty();
        double vehicleHeading = coreData.getHeading().doubleValue();
        return Optional.of(vehicleHeading);
    }

    public static Optional<J2735Bsm> getJ2735Bsm(OdeBsmData bsm) {
        if (bsm == null) return Optional.empty();
        if (bsm.getPayload() == null) return Optional.empty();
        if (!(bsm.getPayload() instanceof OdeBsmPayload)) return Optional.empty();
        var payload = (OdeBsmPayload)bsm.getPayload();
        if (payload.getBsm() == null) return Optional.empty();
        return Optional.of(payload.getBsm());
    }

    public static Optional<J2735BsmCoreData> getCoreData(OdeBsmData bsm) {
        Optional<J2735Bsm> optionalBsm = getJ2735Bsm(bsm);
        if (optionalBsm.isEmpty()) return Optional.empty();
        J2735Bsm j2735Bsm = optionalBsm.get();
        if (j2735Bsm.getCoreData() == null) return Optional.empty();
        return Optional.of(j2735Bsm.getCoreData());
    }

    public static Optional<Double> getSpeedMPH(OdeBsmData bsm) {
        Optional<J2735BsmCoreData> optionalCoreData = getCoreData(bsm);
        if (optionalCoreData.isEmpty()) return Optional.empty();
        J2735BsmCoreData coreData = optionalCoreData.get();
        if (coreData.getSpeed() == null) return Optional.empty();
        double speedMetersPerSecond = coreData.getSpeed().doubleValue();
        double speedMPH = speedMetersPerSecond * 2.23694; // convert m/s to mph
        return Optional.of(speedMPH);
    }

    public static String getVehicleId(OdeBsmData bsm) {
        Optional<J2735BsmCoreData> optionalCoreData = getCoreData(bsm);
        if (optionalCoreData.isEmpty()) return "";
        J2735BsmCoreData coreData = optionalCoreData.get();
        return coreData.getId();
    }

    public static String getRsuIp(OdeBsmData bsm) {
        String ip = "";
        if (bsm.getMetadata() != null && bsm.getMetadata() instanceof OdeBsmMetadata) {
            var metadata = (OdeBsmMetadata) bsm.getMetadata();
            ip = metadata.getOriginIp();
        }
        return ip;
    }

    public static long getOdeReceivedAt(OdeBsmData bsm) {
        long odeReceivedAt = 0;
        if (bsm != null && bsm.getMetadata() != null && bsm.getMetadata() instanceof OdeBsmMetadata metadata) {
            String strOdeReceivedAt = metadata.getOdeReceivedAt();
            assert(strOdeReceivedAt != null);
            try {
                odeReceivedAt = Instant.parse(strOdeReceivedAt).toEpochMilli();
            } catch (DateTimeParseException ex) {
                log.error(String.format("Error parsing odeReceivedAt: %s", strOdeReceivedAt), ex);
            }
        }
        return odeReceivedAt;
    }
}

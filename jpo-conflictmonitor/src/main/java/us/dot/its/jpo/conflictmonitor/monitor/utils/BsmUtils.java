package us.dot.its.jpo.conflictmonitor.monitor.utils;

import org.locationtech.jts.geom.CoordinateXY;
import us.dot.its.jpo.ode.model.OdeBsmData;
import us.dot.its.jpo.ode.model.OdeBsmPayload;
import us.dot.its.jpo.ode.plugin.j2735.J2735Bsm;
import us.dot.its.jpo.ode.plugin.j2735.J2735BsmCoreData;
import us.dot.its.jpo.ode.plugin.j2735.OdePosition3D;

import java.util.Optional;

public class BsmUtils {
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
}

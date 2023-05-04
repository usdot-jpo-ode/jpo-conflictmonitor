package us.dot.its.jpo.conflictmonitor.monitor.utils;

import org.locationtech.jts.geom.CoordinateXY;
import us.dot.its.jpo.ode.model.OdeBsmData;
import us.dot.its.jpo.ode.model.OdeBsmPayload;
import us.dot.its.jpo.ode.plugin.j2735.J2735Bsm;
import us.dot.its.jpo.ode.plugin.j2735.OdePosition3D;

public class BsmUtils {
    public static CoordinateXY getPosition(OdeBsmData bsm) {
        CoordinateXY position = new CoordinateXY();
        if (bsm == null) return position;
        if (bsm.getPayload() == null) return position;
        if (!(bsm.getPayload() instanceof OdeBsmPayload)) return position;
        var payload = (OdeBsmPayload)bsm.getPayload();
        if (payload.getBsm() == null) return position;
        J2735Bsm bsmData = payload.getBsm();
        if (bsmData.getCoreData() == null) return position;
        var coreData = bsmData.getCoreData();
        OdePosition3D pos = coreData.getPosition();
        if (pos == null) return position;
        position.setX(pos.getLongitude() != null ? pos.getLongitude().doubleValue() : 0.0);
        position.setY(pos.getLatitude() != null ? pos.getLatitude().doubleValue() : 0.0);
        return position;
    }
}

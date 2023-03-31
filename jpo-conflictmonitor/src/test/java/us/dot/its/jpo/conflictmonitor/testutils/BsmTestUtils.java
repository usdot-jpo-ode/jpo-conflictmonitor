package us.dot.its.jpo.conflictmonitor.testutils;

import us.dot.its.jpo.ode.model.OdeBsmData;
import us.dot.its.jpo.ode.model.OdeBsmMetadata;
import us.dot.its.jpo.ode.model.OdeBsmPayload;
import us.dot.its.jpo.ode.plugin.j2735.J2735Bsm;
import us.dot.its.jpo.ode.plugin.j2735.J2735BsmCoreData;
import us.dot.its.jpo.ode.plugin.j2735.OdePosition3D;

import java.math.BigDecimal;
import java.time.*;
import java.time.format.DateTimeFormatter;

/**
 * Utilities to create BSM test data
 */
public class BsmTestUtils {

    public static int milliOfMinute(Instant instant) {
        ZonedDateTime zdt = instant.atZone(ZoneOffset.UTC);
        ZonedDateTime zdtMinute = ZonedDateTime.of(zdt.getYear(), zdt.getMonthValue(),
                zdt.getDayOfMonth(), zdt.getHour(), zdt.getMinute(), 0, 0, ZoneOffset.UTC);
        Duration minDuration = Duration.between(zdtMinute, zdt);
        return (int)minDuration.toMillis();
    }

    public static OdeBsmData bsmAtInstant(Instant instant) {
        var bsm = validBsm();
        var coreData = ((J2735Bsm)bsm.getPayload().getData()).getCoreData();
        var metadata = (OdeBsmMetadata)bsm.getMetadata();
        ZonedDateTime zdt = instant.atZone(ZoneOffset.UTC);
        var strDateTime = DateTimeFormatter.ISO_DATE_TIME.format(zdt);
        metadata.setOdeReceivedAt(strDateTime);
        metadata.setRecordGeneratedAt(strDateTime);
        coreData.setSecMark(milliOfMinute(instant));
        return bsm;
    }

    public static OdeBsmData validBsm() {
        final var bsm = new OdeBsmData();
        final var payload = new OdeBsmPayload();
        final var data = new J2735Bsm();
        final var coreData = new J2735BsmCoreData();
        final var position = new OdePosition3D();
        final var metadata = new OdeBsmMetadata();
        position.setLatitude(BigDecimal.valueOf(40.0));
        position.setLongitude(BigDecimal.valueOf(-105));
        position.setElevation(BigDecimal.valueOf(1600));
        coreData.setPosition(position);
        coreData.setId("id");
        coreData.setSecMark(1000);
        coreData.setSpeed(BigDecimal.valueOf(50));
        coreData.setHeading(BigDecimal.valueOf(90));
        data.setCoreData(coreData);
        payload.setData(data);
        bsm.setPayload(payload);
        metadata.setBsmSource(OdeBsmMetadata.BsmSource.unknown);
        metadata.setOriginIp("127.0.0.1");
        metadata.setRecordGeneratedAt("2020-01-01T00:00:00.000Z");
        bsm.setMetadata(metadata);

        return bsm;
    }
}

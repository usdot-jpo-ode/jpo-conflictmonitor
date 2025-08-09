package us.dot.its.jpo.conflictmonitor.testutils;

import us.dot.its.jpo.geojsonconverter.pojos.geojson.Point;
import us.dot.its.jpo.geojsonconverter.pojos.geojson.bsm.BsmProperties;
import us.dot.its.jpo.geojsonconverter.pojos.geojson.bsm.ProcessedBsm;

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

    public static ProcessedBsm<Point> processedBsmAtInstant(Instant instant, String id) {
        var bsm = validProcessedBsm();
        ZonedDateTime zdt = instant.atZone(ZoneOffset.UTC);
        var strDateTime = DateTimeFormatter.ISO_DATE_TIME.format(zdt);
        bsm.getProperties().setOdeReceivedAt(strDateTime);
        bsm.getProperties().setTimeStamp(zdt);
        BsmProperties props = bsm.getProperties();
        props.setSecMark(milliOfMinute(instant));
        props.setId(id);
        return bsm;
    }

    public static ProcessedBsm<Point> processedBsmWithPosition(Instant instant, String id, double longitude, double latitude, double elevation) {
        var bsm = processedBsmAtInstant(instant, id);
        var coords = bsm.getGeometry().getCoordinates();
        coords[0] = longitude;
        coords[1] = latitude;
        return bsm;
    }

    public static ProcessedBsm<Point> validProcessedBsm() {
        final Point geometry = new Point(-105d, 40.0d);
        final BsmProperties properties = new BsmProperties();
        properties.setId("id");
        properties.setSecMark(1000);
        properties.setSpeed(BigDecimal.valueOf(50).doubleValue());
        properties.setHeading(BigDecimal.valueOf(90).doubleValue());
        properties.setOriginIp("127.0.0.1");
        properties.setTimeStamp(ZonedDateTime.parse("2020-01-01T00:00:00.000Z"));
        properties.setOdeReceivedAt("2020-01-01T00:00:25.123Z");
        return new ProcessedBsm<>(null, geometry, properties);
    }
}

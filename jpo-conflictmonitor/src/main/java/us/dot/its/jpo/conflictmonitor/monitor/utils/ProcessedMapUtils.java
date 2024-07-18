package us.dot.its.jpo.conflictmonitor.monitor.utils;

import lombok.extern.slf4j.Slf4j;
import us.dot.its.jpo.geojsonconverter.pojos.geojson.map.MapSharedProperties;
import us.dot.its.jpo.geojsonconverter.pojos.geojson.map.ProcessedMap;

import java.time.ZonedDateTime;

/**
 * Methods to get properties from ProcessedMaps with null checks
 */
@Slf4j
public class ProcessedMapUtils {

    public static <T> long getTimestamp(ProcessedMap<T> processedMap) {
        if (processedMap == null) {
            log.error("ProcessedMap is null");
            return 0L;
        }
        MapSharedProperties properties = processedMap.getProperties();
        if (properties == null) {
            log.error("ProcessedMap.properties are null");
            return 0L;
        }
        ZonedDateTime zdt = properties.getTimeStamp();
        if (zdt == null) {
            log.error("ProcessedMap Timestamp is null");
            return 0L;
        }
        return zdt.toInstant().toEpochMilli();
    }

    public static <T> long getOdeReceivedAt(ProcessedMap<T> processedMap) {
        if (processedMap == null) {
            log.error("ProcessedMap is null");
            return 0L;
        }
        MapSharedProperties properties = processedMap.getProperties();
        if (properties == null) {
            log.error("ProcessedMap.properties are null");
            return 0L;
        }
        ZonedDateTime zdt = properties.getOdeReceivedAt();
        if (zdt == null) {
            log.error("ProcessedMap.OdeReceivedAt is null");
            return 0L;
        }
        return zdt.toInstant().toEpochMilli();
    }


}

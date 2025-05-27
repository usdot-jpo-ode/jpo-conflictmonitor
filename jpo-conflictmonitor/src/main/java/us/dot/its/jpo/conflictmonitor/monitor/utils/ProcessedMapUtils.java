package us.dot.its.jpo.conflictmonitor.monitor.utils;

import lombok.extern.slf4j.Slf4j;
import us.dot.its.jpo.geojsonconverter.pojos.geojson.BaseFeature;
import us.dot.its.jpo.geojsonconverter.pojos.geojson.map.*;
import us.dot.its.jpo.ode.plugin.j2735.J2735LaneTypeAttributes;

import java.time.ZonedDateTime;
import java.util.Arrays;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

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

    public static <T> Map<Integer, J2735LaneTypeAttributes> getLaneTypeAttributes(ProcessedMap<T> processedMap) {
        MapFeatureCollection<T> featureCollection = processedMap.getMapFeatureCollection();
        if (featureCollection == null) {
            log.error("ProcessedMap.processedMapFeatureCollection is null");
            return Map.of();
        }
        MapFeature<T>[] features = featureCollection.getFeatures();
        return Arrays.stream(features)
                .map(BaseFeature::getProperties)
                .filter(properties -> properties != null
                        && properties.getLaneId() != null
                        && properties.getLaneType() != null)
                .collect(Collectors.toUnmodifiableMap(MapProperties::getLaneId, MapProperties::getLaneType));
    }

}

package us.dot.its.jpo.conflictmonitor.monitor.utils;

import lombok.extern.slf4j.Slf4j;
import us.dot.its.jpo.asn.j2735.r2024.MapData.LaneTypeAttributes;
import us.dot.its.jpo.asn.runtime.types.Asn1Bitstring;
import us.dot.its.jpo.conflictmonitor.monitor.models.events.revocable_enabled_lane_alignment.LaneTypeAttributesMap;
import us.dot.its.jpo.conflictmonitor.monitor.models.events.revocable_enabled_lane_alignment.RevocableLaneTypeAttributes;
import us.dot.its.jpo.geojsonconverter.pojos.ProcessedBitstring;
import us.dot.its.jpo.geojsonconverter.pojos.geojson.BaseFeature;
import us.dot.its.jpo.geojsonconverter.pojos.geojson.map.*;


import java.time.ZonedDateTime;
import java.util.Arrays;
import java.util.Map;
import java.util.Set;
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

    public static <T> Set<Integer> getRevocableLanes(ProcessedMap<T> processedMap) {
        LaneTypeAttributesMap allLaneAttributes = getLaneTypeAttributesMap(processedMap);
        return getRevocableLanes(allLaneAttributes);
    }

    public static <T> Set<Integer> getRevocableLanes(LaneTypeAttributesMap allLaneAttributes) {
        return allLaneAttributes.entrySet().stream()
                .filter(entry -> entry.getValue() != null
                        && entry.getValue().revocable())
                .map(Map.Entry::getKey)
                .collect(Collectors.toUnmodifiableSet());
    }

    public static <T> LaneTypeAttributesMap getLaneTypeAttributesMap(ProcessedMap<T> processedMap) {
        Map<Integer, ProcessedLaneTypeAttributes> attributesMap = getLaneTypeAttributes(processedMap);
        Map<Integer, RevocableLaneTypeAttributes> laneTypeAttributesMap =
                attributesMap.entrySet().stream().collect(
                        Collectors.toUnmodifiableMap(Map.Entry::getKey,
                                entry -> getRevocableLaneTypeAttributes(entry.getValue())));
        return new LaneTypeAttributesMap(laneTypeAttributesMap);
    }

    private static <T> Map<Integer, ProcessedLaneTypeAttributes> getLaneTypeAttributes(ProcessedMap<T> processedMap) {
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

    private static RevocableLaneTypeAttributes getRevocableLaneTypeAttributes(ProcessedLaneTypeAttributes laneTypeAttributes) {
        ProcessedBitstring bitString = null;
        String laneType = null;
        if ((bitString = laneTypeAttributes.getBikeLane()) != null) {
            laneType = "bikeLane";
        } else if ((bitString = laneTypeAttributes.getCrosswalk()) != null) {
            laneType = "crosswalk";
        } else if ((bitString = laneTypeAttributes.getMedian()) != null) {
            laneType = "median";
        } else if ((bitString = laneTypeAttributes.getParking()) != null) {
            laneType = "parking";
        } else if ((bitString = laneTypeAttributes.getSidewalk()) != null) {
            laneType = "sidewalk";
        } else if ((bitString = laneTypeAttributes.getStriping()) != null) {
            laneType = "striping";
        } else if ((bitString = laneTypeAttributes.getTrackedVehicle()) != null) {
            laneType = "trackedVehicle";
        } else if ((bitString = laneTypeAttributes.getVehicle()) != null) {
            laneType = "vehicle";
        }
        // Revocable property is always the first bit of the bitstring of any type of the choice
        boolean revocable = bitString != null && bitString.get(0);
        return new RevocableLaneTypeAttributes(laneType, revocable);
    }


}

package us.dot.its.jpo.conflictmonitor.monitor.models.events.revocable_enabled_lane_alignment;

import java.util.Map;
import java.util.TreeMap;

/**
 * A map of Lane ID to LaneTypeAttributes, sorted by Lane ID.
 */
public class LaneTypeAttributesMap extends TreeMap<Integer, RevocableLaneTypeAttributes> {

    public LaneTypeAttributesMap() {
        super();
    }

    public LaneTypeAttributesMap(Map<Integer, RevocableLaneTypeAttributes> laneTypeAttributesMap) {
        super(laneTypeAttributesMap);
    }
}

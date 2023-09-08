package us.dot.its.jpo.conflictmonitor.monitor.models.config;

import java.util.*;

/**
 * Map of Road Regulator ID (region) -> Intersection ID -> key -> {@link IntersectionConfig}.
 * <p>Key of 0 indicates unknown Road Regulator ID
 */
public class IntersectionConfigMap extends TreeMap<Integer, IntersectionMap> {

    public IntersectionConfigMap() {}

    public IntersectionConfigMap(Collection<IntersectionConfig<?>> configColl) {
        for (var config : configColl) {
            int region = config.getRoadRegulatorID();
            int intersectionId = config.getIntersectionID();
            String key = config.getKey();

            IntersectionMap intersectionMap = null;
            KeyMap keyMap = null;

            if (containsKey(region)) {
                intersectionMap = get(region);
            } else {
                intersectionMap = new IntersectionMap();
                put(region, intersectionMap);
            }

            if (intersectionMap.containsKey(intersectionId)) {
                keyMap = intersectionMap.get(intersectionId);
            } else {
                keyMap = new KeyMap();
                intersectionMap.put(intersectionId, keyMap);
            }

            keyMap.put(key, config);
        }
    }

    public Collection<IntersectionConfig<?>> listConfigs() {
        var list = new ArrayList<IntersectionConfig<?>>();
        for (var intersectionMap : values()) {
            for (var keyMap : intersectionMap.values()) {
                list.addAll(keyMap.values());
            }
        }
        return list;
    }

    public IntersectionConfigMap filter(Optional<Integer> region, Optional<Integer> intersectionId, Optional<String> prefix) {
        var list = listConfigs();
        var filteredList = new ArrayList<IntersectionConfig<?>>();
        for (var config : list) {
            if (region.isPresent() && !region.get().equals(config.getRoadRegulatorID())) continue;
            if (intersectionId.isPresent() && !intersectionId.get().equals(config.getIntersectionID())) continue;
            if (prefix.isPresent() && !config.getKey().startsWith(prefix.get())) continue;
            filteredList.add(config);
        }
        return new IntersectionConfigMap(filteredList);
    }

}

/**
 * Map of key -> {@link IntersectionConfig}
 */
class KeyMap extends TreeMap<String, IntersectionConfig<?>> {}

/**
 * Map of IntersectionID -> {@link KeyMap}
 */
class IntersectionMap extends TreeMap<Integer, KeyMap> {}



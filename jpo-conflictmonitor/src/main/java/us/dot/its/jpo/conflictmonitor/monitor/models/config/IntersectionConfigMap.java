package us.dot.its.jpo.conflictmonitor.monitor.models.config;



import org.apache.commons.lang3.StringUtils;

import java.util.*;
import java.util.stream.Collectors;

/**
 * Map of
 *     Road Regulator ID (int, region)
 *         -> Intersection ID (int)
 *             -> Config Key (String)
 *                 -> {@link IntersectionConfig}.
 *
 * <p>Key of 0 indicates unknown Road Regulator ID
 */
public class IntersectionConfigMap extends TreeMap<Integer, IntersectionMap> {

    public IntersectionConfigMap() {}

    public IntersectionConfigMap(Collection<IntersectionConfig<?>> configColl) {
        for (var config : configColl) {
            putConfig(config);
        }
    }

    /**
     * Add a config with specified region, intersection and key
      * @param config
     */
    public void putConfig(IntersectionConfig<?> config) {
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

    /**
     * Get {@link IntersectionConfig} where region id is known
     * @param roadRegulatorId
     * @param intersectionId
     * @param key
     * @return IntersectionConfig if any defined
     */
    public Optional<IntersectionConfig<?>> getConfig(int roadRegulatorId, int intersectionId, String key) {
        var configKey = new IntersectionConfigKey(roadRegulatorId, intersectionId, key);
        return getConfig(configKey);
    }

    public Optional<IntersectionConfig<?>> getConfig(IntersectionConfigKey configKey) {
        if (containsKey(configKey)) {
            IntersectionConfig<?> config = get(configKey.getRegion()).get(configKey.getRegion()).get(configKey.getKey());
            return Optional.of(config);
        }
        return Optional.empty();
    }

    /**
     * Get list of one or more {@link IntersectionConfig} where region id is not known
     * @param intersectionId
     * @param key
     * @return List of IntersectionConfigs
     */
    public List<IntersectionConfig<?>> getConfig(int intersectionId, String key) {
        var configList = new ArrayList<IntersectionConfig<?>>();
        for (var intersectionMap : values()) {
            if (intersectionMap.containsKey(intersectionId)) {
                KeyMap keyMap = intersectionMap.get(intersectionId);
                if (keyMap.containsKey(key)) {
                    configList.add(keyMap.get(key));
                }
            }
        }
        return configList;
    }



    public boolean containsKey(IntersectionConfigKey configKey) {
        if (containsKey(configKey.getRegion())) {
            IntersectionMap intersectionMap = get(configKey.getRegion());
            if (intersectionMap.containsKey(configKey.getIntersectionId())) {
                KeyMap keyMap = intersectionMap.get(configKey.getIntersectionId());
                return keyMap.containsKey(configKey.getKey());
            }
        }
        return false;
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

    public Collection<IntersectionConfig<?>> listConfigs(String key) {
        return listConfigs().stream().filter(config -> StringUtils.equals(config.getKey(), key)).collect(Collectors.toList());
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



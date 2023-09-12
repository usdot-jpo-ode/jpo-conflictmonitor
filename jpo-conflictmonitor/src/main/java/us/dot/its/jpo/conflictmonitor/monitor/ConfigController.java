package us.dot.its.jpo.conflictmonitor.monitor;

import lombok.Getter;
import lombok.Setter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.context.annotation.DependsOn;
import org.springframework.context.annotation.Profile;
import us.dot.its.jpo.conflictmonitor.monitor.algorithms.config.ConfigAlgorithm;
import us.dot.its.jpo.conflictmonitor.monitor.algorithms.config.ConfigUpdateResult;
import us.dot.its.jpo.conflictmonitor.monitor.models.config.*;
import us.dot.its.jpo.conflictmonitor.monitor.topologies.config.ConfigTopology;

import java.util.Formatter;
import java.util.Optional;
import java.util.stream.Collectors;

@Getter
@Setter
@RestController
@RequestMapping(path = "/config", produces = MediaType.APPLICATION_JSON_VALUE)
@DependsOn("createKafkaTopics")
@Profile({"!test", "testConfig"})
public class ConfigController {

    private static final Logger logger = LoggerFactory.getLogger(ConfigController.class);

    final ConfigAlgorithm configAlgorithm;

    @Autowired
    public ConfigController(ConfigTopology configAlgorithm) {
        this.configAlgorithm = configAlgorithm;
    }

    @GetMapping(value = "/defaults")
    public @ResponseBody ResponseEntity<DefaultConfigMap> listDefaultConfigs(
            @RequestParam(name = "prefix") Optional<String> optionalPrefix)  {

        try {
            var configMap = configAlgorithm.mapDefaultConfigs();
            if (optionalPrefix.isPresent()) {
                var prefix = optionalPrefix.get();
                var filteredMap = configMap.entrySet().stream()
                        .filter(entry -> entry.getKey().startsWith(prefix))
                        .collect(Collectors.toMap(entry -> entry.getKey(), entry -> entry.getValue()));
                return ResponseEntity.ok(new DefaultConfigMap(filteredMap));
            } else {
                return ResponseEntity.ok(new DefaultConfigMap(configMap));
            }
        } catch (Exception e) {
            logger.error("Error listing default configs", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }

    }

    @GetMapping(value = "/intersections")
    public @ResponseBody ResponseEntity<IntersectionConfigMap> listIntersectionConfigs(
            @RequestParam(name = "prefix") Optional<String> optionalPrefix,
            @RequestParam(name = "intersectionId") Optional<Integer> optionalIntersectionId,
            @RequestParam(name = "region", required = false) Optional<Integer> optionalRegion) {
        try {
            var configMap = configAlgorithm.mapIntersectionConfigs();
            var filteredMap = configMap.filter(optionalRegion, optionalIntersectionId, optionalPrefix);
            return ResponseEntity.ok(filteredMap);
        } catch (Exception e) {
            logger.error("Error listing intersection configs", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }

    @GetMapping(value = "/default/{key}")
    public @ResponseBody ResponseEntity<DefaultConfig<?>> getDefaultConfig(
            @PathVariable(name = "key") String key) {
        try {
            DefaultConfig<?> config = configAlgorithm.getDefaultConfig(key);
            return ResponseEntity.ok(config);
        } catch (Exception e) {
            String msg = String.format("Error getting default config %s", key);
            logger.error(msg, e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }

    @GetMapping(value = "/intersection/{intersectionId}/{key}")
    public @ResponseBody ResponseEntity<IntersectionConfig<?>> getIntersectionConfig(
            @PathVariable(name = "intersectionId") int intersectionId,
            @PathVariable(name = "key") String key) {
        try {
            var configKey = new IntersectionConfigKey(0, intersectionId, key);
            Optional<IntersectionConfig<?>> config = configAlgorithm.getIntersectionConfig(configKey);
            return ResponseEntity.ok(config.orElse(null));
        } catch (Exception e) {
            String msg = String.format("Error getting intersection config for intersection: %s, key: %s",
                    intersectionId, key);
            logger.error(msg, e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }

    @GetMapping(value = "/intersection/{region}/{intersectionId}/{key}")
    public @ResponseBody ResponseEntity<IntersectionConfig<?>> getIntersectionConfig(
            @PathVariable(name = "region") int region,
            @PathVariable(name = "intersectionId") int intersectionId,
            @PathVariable(name = "key") String key) {
        try {
            var configKey = new IntersectionConfigKey(region, intersectionId, key);
            Optional<IntersectionConfig<?>> config = configAlgorithm.getIntersectionConfig(configKey);
            return ResponseEntity.ok(config.orElse(null));
        } catch (Exception e) {
            String msg = String.format("Error getting intersection config for region: %s, intersection: %s, key: %s",
                    region, intersectionId, key);
            logger.error(msg, e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }

    @PostMapping(value = "default/{key}")
    public @ResponseBody <T> ResponseEntity<ConfigUpdateResult<T>> saveDefaultConfig(
            @PathVariable(name = "key") String key,
            @RequestBody DefaultConfig<T> config) {
        ConfigUpdateResult<T> updateResult = new ConfigUpdateResult<T>();
        try {


            // Validate keys
            if (!key.equals(config.getKey())) {
                String msg = String.format("Key in path does not match key in body %s != %s", key, config.getKey());
                logger.error(msg);
                updateResult.setResult(ConfigUpdateResult.Result.ERROR);
                updateResult.setMessage(msg);
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(updateResult);
            }

            updateResult = configAlgorithm.updateCustomConfig(config);
            return ResponseEntity.ok(updateResult);
        } catch (ConfigException ce) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body((ConfigUpdateResult<T>)ce.getResult());
        } catch (Exception e) {
            String msg = String.format("Exception saving default config %s", config);
            logger.error(msg, e);
            updateResult.setMessage(msg);
            updateResult.setResult(ConfigUpdateResult.Result.ERROR);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(updateResult);
        }
    }

    @PostMapping(value = "intersection/{region}/{intersectionId}/{key}")
    public @ResponseBody <T> ResponseEntity<ConfigUpdateResult<T>> saveIntersectionConfig(
            @PathVariable(name = "region") int region,
            @PathVariable(name = "intersectionId") int intersectionId,
            @PathVariable(name = "key") String key,
            @RequestBody IntersectionConfig<T> config) {
        return saveIntersectionConfigHelper(region, intersectionId, key, config, true);
    }

    @PostMapping(value = "intersection/{intersectionId}/{key}")
    public @ResponseBody <T> ResponseEntity<ConfigUpdateResult<T>> saveIntersectionConfig(
            @PathVariable(name = "intersectionId") int intersectionId,
            @PathVariable(name = "key") String key,
            @RequestBody IntersectionConfig<T> config) {
        return saveIntersectionConfigHelper(0, intersectionId, key, config, false);
    }

    private <T> ResponseEntity<ConfigUpdateResult<T>> saveIntersectionConfigHelper(
            int region, int intersectionId, String key, IntersectionConfig<T> config, boolean useRegion) {

        ConfigUpdateResult<T> updateResult = new ConfigUpdateResult<T>();
        try (var errMsg = new Formatter();) {

            // Validate path keys
            if (!key.equals(config.getKey())) {
                errMsg.format("Key in path does not match key in body %s != %s%n", key, config.getKey());
            }
            if (useRegion) {
                if (region != config.getRoadRegulatorID()) {
                    errMsg.format("Region in path does not match RoadRegulatorID in body %s != %s%n", region, config.getRoadRegulatorID());
                }
            } else {
                if (config.getRoadRegulatorID() != 0) {
                    errMsg.format("Region is not specified in URL path, but RoadRegulatorID is non-zero in the body: %s != 0.  Use the intersection/{region}/{intersectionId}/{key} endpoint to post with the region.%n", config.getRoadRegulatorID());
                }
            }
            if (intersectionId != config.getIntersectionID()) {
                errMsg.format("IntersectionID in path does not match body property %s != %s%n", intersectionId, config.getIntersectionID());
            }
            if (errMsg.toString().length() > 0) {
                var msg = errMsg.toString();
                logger.error(msg);
                updateResult.setResult(ConfigUpdateResult.Result.ERROR);
                updateResult.setMessage(msg);
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(updateResult);
            }

            updateResult = configAlgorithm.updateIntersectionConfig(config);
            return ResponseEntity.ok(updateResult);

        } catch (ConfigException ce) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body((ConfigUpdateResult<T>)ce.getResult());
        } catch (Exception e) {
            String msg = String.format("Exception saving intersection config %s", config);
            logger.error(msg, e);
            updateResult.setMessage(msg);
            updateResult.setResult(ConfigUpdateResult.Result.ERROR);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(updateResult);
        }

    }

}

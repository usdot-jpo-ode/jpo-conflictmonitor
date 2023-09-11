package us.dot.its.jpo.conflictmonitor.monitor;

import lombok.Getter;
import lombok.Setter;
import static org.apache.commons.lang3.StringUtils.isBlank;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.context.annotation.DependsOn;
import org.springframework.context.annotation.Profile;
import us.dot.its.jpo.conflictmonitor.monitor.algorithms.config.ConfigUpdateResult;
import us.dot.its.jpo.conflictmonitor.monitor.models.config.*;
import us.dot.its.jpo.conflictmonitor.monitor.topologies.config.ConfigTopology;

import java.util.Optional;
import java.util.stream.Collectors;

@Getter
@Setter
@RestController
@RequestMapping(path = "/config", produces = MediaType.APPLICATION_JSON_VALUE)
@DependsOn("createKafkaTopics")
@Profile("!test")
public class ConfigController {

    private static final Logger logger = LoggerFactory.getLogger(ConfigController.class);

    final ConfigTopology configTopology;

    @Autowired
    public ConfigController(ConfigTopology configTopology) {
        this.configTopology = configTopology;
    }

    @GetMapping(value = "/defaults")
    public @ResponseBody ResponseEntity<DefaultConfigMap> listDefaultConfigs(
            @RequestParam(name = "prefix") Optional<String> optionalPrefix)  {

        try {
            var configMap = configTopology.mapDefaultConfigs();
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
            var configMap = configTopology.mapIntersectionConfigs();
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
            DefaultConfig<?> config = configTopology.getDefaultConfig(key);
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
            Optional<IntersectionConfig<?>> config = configTopology.getIntersectionConfig(intersectionId, key);
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
        String msg = String.format("Region: %s, Intersection: %s, Key: %s", region, intersectionId, key);
        return ResponseEntity.ok(null);
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

            updateResult = configTopology.updateCustomConfig(config);
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
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
    }

    @PostMapping(value = "intersection/{intersectionId}/{key}")
    public @ResponseBody <T> ResponseEntity<ConfigUpdateResult<T>> saveIntersectionConfig(
            @PathVariable(name = "intersectionId") int intersectionId,
            @PathVariable(name = "key") String key,
            @RequestBody IntersectionConfig<T> config) {
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
    }
}

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
import us.dot.its.jpo.conflictmonitor.monitor.models.config.DefaultConfig;
import us.dot.its.jpo.conflictmonitor.monitor.models.config.DefaultConfigCollection;
import us.dot.its.jpo.conflictmonitor.monitor.models.config.IntersectionConfig;
import us.dot.its.jpo.conflictmonitor.monitor.models.config.IntersectionConfigCollection;
import us.dot.its.jpo.conflictmonitor.monitor.topologies.ConfigTopology;

import java.util.Optional;
import java.util.stream.Collectors;

@Getter
@Setter
@RestController
@RequestMapping(path = "/config", produces = MediaType.APPLICATION_JSON_VALUE)
@DependsOn("createKafkaTopics")
@Profile("!test")
public class ConfigurationController {

    private static final Logger logger = LoggerFactory.getLogger(ConfigurationController.class);

    final ConfigTopology configTopology;

    @Autowired
    public ConfigurationController(ConfigTopology configTopology) {
        this.configTopology = configTopology;
    }

    @GetMapping(value = "/defaults")
    public @ResponseBody ResponseEntity<DefaultConfigCollection> listDefaultConfigs(
            @RequestParam(name = "prefix") Optional<String> optionalPrefix)  {

        try {
            var configList = configTopology.listDefaultConfigs();
            if (optionalPrefix.isPresent()) {
                var prefix = optionalPrefix.get();
                var filteredList = configList.stream()
                        .filter(entry -> entry.getKey().startsWith(prefix))
                        .collect(Collectors.toList());
                return ResponseEntity.ok(new DefaultConfigCollection(filteredList));
            } else {
                return ResponseEntity.ok(configList);
            }
        } catch (Exception e) {
            logger.error("Error listing default configs", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }

    }

    @GetMapping(value = "/intersections")
    public @ResponseBody ResponseEntity<IntersectionConfigCollection> listIntersectionConfigs(
            @RequestParam(name = "prefix") Optional<String> optionalPrefix,
            @RequestParam(name = "intersectionId") Optional<Integer> optionalIntersectionId,
            @RequestParam(name = "region") Optional<Integer> optionalRegion) {
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
    }

    @GetMapping(value = "/default/{key}")
    public @ResponseBody ResponseEntity<DefaultConfig<?>> getDefaultConfig(
            @PathVariable(name = "key") Optional<String> optionalKey) {
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
    }

    @GetMapping(value = "/intersection/{key}/{intersectionId}/{region}")
    public @ResponseBody ResponseEntity<IntersectionConfig<?>> getIntersectionConfig(
            @PathVariable(name = "intersectionId") int intersectionId) {
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
    }

    @PostMapping(value = "default/{key}")
    public @ResponseBody ResponseEntity<String> saveDefaultConfig(
            @PathVariable(name = "key") String key) {
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
    }

    @PostMapping(value = "intersection/{key}/{intersectionId}/{region}")
    public @ResponseBody ResponseEntity<String> saveIntersectionConfig(
            @PathVariable(name = "key") String key,
            @PathVariable(name = "intersectionId") String intersectionId,
            @PathVariable(name = "region") Optional<String> optionalRegion) {
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
    }
}

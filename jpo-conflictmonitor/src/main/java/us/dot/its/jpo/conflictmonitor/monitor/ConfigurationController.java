package us.dot.its.jpo.conflictmonitor.monitor;

import lombok.Getter;
import lombok.Setter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.context.annotation.DependsOn;
import org.springframework.context.annotation.Profile;
import us.dot.its.jpo.conflictmonitor.monitor.models.config.DefaultConfig;
import us.dot.its.jpo.conflictmonitor.monitor.models.config.DefaultConfigCollection;
import us.dot.its.jpo.conflictmonitor.monitor.models.config.IntersectionConfig;
import us.dot.its.jpo.conflictmonitor.monitor.models.config.IntersectionConfigCollection;

@Getter
@Setter
@RestController
@RequestMapping(path = "/config", produces = MediaType.APPLICATION_JSON_VALUE)
@DependsOn("createKafkaTopics")
@Profile("!test")
public class ConfigurationController {

    private static final Logger logger = LoggerFactory.getLogger(ConfigurationController.class);




    @GetMapping(value = "/defaults")
    public @ResponseBody ResponseEntity<DefaultConfigCollection> listDefaultConfigs(
            @RequestParam(required = false) String prefix)  {

    }

    @GetMapping(value = "/intersections")
    public @ResponseBody ResponseEntity<IntersectionConfigCollection> listIntersectionConfigs(
            @RequestParam(required = false) String prefix,
            @RequestParam(required = false) Integer intersectionId,
            @RequestParam(required = false) Integer region) {

    }

    @GetMapping(value = "/default/{name}")
    public @ResponseBody ResponseEntity<DefaultConfig<?>> getDefaultConfig(@PathVariable String name) {

    }

    @GetMapping(value = "/intersection/{name}/{intersectionId}/{region}")
    public @ResponseBody ResponseEntity<IntersectionConfig<?>> getIntersectionConfig(
            @PathVariable int intersectionId) {

    }

    @PostMapping(value = "default/{name}")
    public @ResponseBody ResponseEntity<String> saveDefaultConfig(@PathVariable String name) {

    }

    @PostMapping(value = "intersection/{name}/{intersectionId}/{region}")
    public @ResponseBody ResponseEntity<String> saveIntersectionConfig(@PathVariable )
}

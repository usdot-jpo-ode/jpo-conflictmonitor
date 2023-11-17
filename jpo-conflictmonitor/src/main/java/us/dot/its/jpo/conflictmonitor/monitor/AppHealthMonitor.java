package us.dot.its.jpo.conflictmonitor.monitor;

import java.time.Instant;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.stream.Collectors;

import org.apache.kafka.common.MetricName;
import org.apache.kafka.streams.KafkaStreams;
import org.apache.kafka.streams.KafkaStreams.State;
import org.apache.kafka.streams.kstream.Windowed;
import org.locationtech.jts.index.quadtree.Quadtree;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.DependsOn;
import org.springframework.context.annotation.Profile;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.kafka.core.KafkaAdmin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;

import lombok.Getter;
import lombok.Setter;
import us.dot.its.jpo.conflictmonitor.ConflictMonitorProperties;
import us.dot.its.jpo.conflictmonitor.KafkaConfiguration;
import us.dot.its.jpo.conflictmonitor.monitor.algorithms.AlgorithmParameters;
import us.dot.its.jpo.conflictmonitor.monitor.algorithms.StreamsTopology;
import us.dot.its.jpo.conflictmonitor.monitor.algorithms.config.ConfigParameters;
import us.dot.its.jpo.conflictmonitor.monitor.models.bsm.BsmIntersectionIdKey;
import us.dot.its.jpo.conflictmonitor.monitor.models.bsm.BsmRsuIdKey;
import us.dot.its.jpo.conflictmonitor.monitor.models.map.MapIndex;
import us.dot.its.jpo.conflictmonitor.monitor.topologies.ConfigTopology;
import us.dot.its.jpo.conflictmonitor.monitor.topologies.IntersectionEventTopology;
import us.dot.its.jpo.geojsonconverter.partitioner.RsuIntersectionKey;
import us.dot.its.jpo.geojsonconverter.pojos.geojson.LineString;
import us.dot.its.jpo.geojsonconverter.pojos.geojson.map.ProcessedMap;
import us.dot.its.jpo.geojsonconverter.pojos.spat.ProcessedSpat;
import us.dot.its.jpo.ode.model.OdeBsmData;

@Getter
@Setter
@RestController
@RequestMapping(path = "/health", produces = MediaType.APPLICATION_JSON_VALUE)
@DependsOn("createKafkaTopics")
@Profile("!test")
public class AppHealthMonitor {

    private static final ObjectMapper mapper;

    private static final Logger logger = LoggerFactory.getLogger(AppHealthMonitor.class);

    static {
        mapper = new ObjectMapper();
        mapper.configure(SerializationFeature.FAIL_ON_EMPTY_BEANS, false);
        mapper.registerModule(new JavaTimeModule());
    }

    @Autowired MonitorServiceController monitorServiceController;
    @Autowired KafkaAdmin kafkaAdmin;
    @Autowired KafkaConfiguration kafkaConfiguration;
    @Autowired ConflictMonitorProperties conflictMonitorProperties;
    @Autowired ConfigParameters configParams;
    @Autowired AlgorithmParameters algorithmParameters;
    @Autowired ConfigTopology configTopology;
    @Autowired MapIndex mapIndex;
    @Autowired IntersectionEventTopology intersectionEventTopology;

  
    public List<Object> parameterObjects() {
        List<Object> paramObjects = new ArrayList<Object>();
        paramObjects.add(configParams);
        // TODO: Fix Jackson error trying to serialize URI inside JAR for this
        //paramObjects.add(conflictMonitorProperties);
        if (algorithmParameters != null) {
            paramObjects.addAll(algorithmParameters.listParameterObjects());
        }
        return paramObjects;
    }

    @GetMapping
    public @ResponseBody ResponseEntity<String> summary() {
        try {
            var linkMap = new TreeMap<String, String>();
            addLinks(linkMap,
                    "config/default"
                    ,"config/intersection",
                    "topics",
                    "properties",
                    "streams",
                    "connectors",
                    "spatial-indexes",
                    "spat-window-store",
                    "bsm-window-store",
                    "map-store"
                );
            return getJsonResponse(linkMap);
        } catch (Exception ex) {
            return getErrorJson(ex);
        }
    }

    private void addLinks(Map<String, String> map, String... paths) {
        for (String path : paths) {
            var link = String.format("%s/health/%s", baseUrl(), path);
            map.put(path, link);
        }
    }

    @GetMapping(value = "/config/default")
    public @ResponseBody ResponseEntity<String> listDefaultConfig() {
        try {
            return getJsonResponse(configTopology.mapDefaultConfigs());
        } catch (Exception ex) {
            return getErrorJson(ex);
        }
    }

    @GetMapping(value = "/config/intersection")
    public @ResponseBody ResponseEntity<String> listIntersectionConfig() {
        try {
            return getJsonResponse(configTopology.mapIntersectionConfigs());
        } catch (Exception ex) {
            return getErrorJson(ex);
        }
    }
    

    /**
     * @return JSON map of kafka topics created by this app that currently exist
     */
    @GetMapping(value = "/topics")
    public @ResponseBody ResponseEntity<String> listTopics() {
        try {
            var existingTopics = new TreeMap<String, String>();
            
            var topicNames = kafkaConfiguration.getCreateTopics().stream()
                .filter(topic -> topic.keySet().contains("name"))
                .map(topic -> (String)topic.get("name"))
                .collect(Collectors.toUnmodifiableList());

            var topicDescMap = kafkaAdmin.describeTopics(topicNames.toArray(new String[0]));

            for (var entry : topicDescMap.entrySet()) {      
                existingTopics.put(entry.getKey(), entry.getValue().toString());
            }

            return getJsonResponse(existingTopics);
        } catch (Exception ex) {
            return getErrorJson(ex);
        }
    }

    @GetMapping(value = "/properties")
    public @ResponseBody ResponseEntity<String> listProperties() {
        try {
            var propMap = new TreeMap<String, Object>();
            
            for (var params : parameterObjects()) {
               propMap.put(params.getClass().getSimpleName(), params);
            } 
            
            return getJsonResponse(propMap);
        } catch (Exception ex) {
            logger.error("Error listing properties", ex);
            return getErrorJson(ex);
        }
    }



    @GetMapping(value = "/streams")
    public @ResponseBody ResponseEntity<String> listStreams() {
        var streamsMap = getKafkaStreamsMap();
        String baseUrl = baseUrl();
        var result = new StreamsInfoMap();
        for (String name : streamsMap.keySet()) {
            var streamsInfo = new StreamsInfo();
            String url = String.format("%s/health/streams/%s", baseUrl, name);
            streamsInfo.setDetailsUrl(url);
            result.put(name, streamsInfo);

            KafkaStreams streams = streamsMap.get(name);
            if (streams == null) {
                continue;
            } 
            var state = streams.state();
            streamsInfo.setState(state != null ? state : null);
            
        }
        return getJsonResponse(result);     
    }

    @GetMapping(value = "/streams/{name}")
    public @ResponseBody ResponseEntity<String> namedStreams(@PathVariable String name) {

        Map<String, KafkaStreams> streamsMap = getKafkaStreamsMap();
        if (streamsMap == null)
            return getOKResponse("The streams map is null.");
        if (!streamsMap.containsKey(name))
            return getOKResponse("The streams map doesn't contain an object named " + name);
        KafkaStreams streams = streamsMap.get(name);
        if (streams == null)
            return getOKResponse("The KafkaStreams object is null");

        
        var metrics = streams.metrics();
        var result = new MetricsGroupMap();
       
        for (MetricName metricName : metrics.keySet()) {
            String groupName = metricName.group();
            MetricsGroup group = null;
            if (result.containsKey(groupName)) {
                group = result.get(groupName);
            } else {
                group = new MetricsGroup();
                result.put(groupName, group);
            }
            var metric = metrics.get(metricName);
            var metricValue = metric.metricValue();
            group.put(metricName.name(), metricValue); 
        }
        return getJsonResponse(result);
       
    }

    @GetMapping(value = "/connectors")
    public @ResponseBody ResponseEntity<String> connectors() {
        final var restTemplate = new RestTemplate();
        final var url = String.format("%s/connectors?expand=status", 
                conflictMonitorProperties.getConnectURL());
        ResponseEntity<String> response = restTemplate.getForEntity(url, String.class);
        return response;
    }

    @GetMapping(value = "/spatial-indexes")
    public @ResponseBody ResponseEntity<String> spatial() {
        if (mapIndex == null) {
            return getOKResponse("The map index is null");
        }

        Quadtree quadtree = mapIndex.getQuadtree();
        var allItems = quadtree.queryAll();
        return getJsonResponse(allItems);
    }

    @GetMapping(value = "/map-store")
    public @ResponseBody ResponseEntity<String> mapStore() {
        var mapStore = intersectionEventTopology.getMapStore();
        var mapMap = new TreeMap<String, ProcessedMap<LineString>>();
        try (var mapIterator = mapStore.all()) {
            while (mapIterator.hasNext()) {
                var kvp = mapIterator.next();
                RsuIntersectionKey key = kvp.key;
                ProcessedMap<LineString> map = kvp.value;
                mapMap.put(key.toString(), map);
            }
        }
        return getJsonResponse(mapMap);
    }

    @GetMapping(value = "/spat-window-store")
    public @ResponseBody ResponseEntity<String> spatWindowStore() {
        var spatWindowStore = intersectionEventTopology.getSpatWindowStore();
        var intersectionMap = new IntersectionSpatMap();
        var formatter = DateTimeFormatter.ISO_DATE_TIME;
       try (var spatIterator = spatWindowStore.all()) {
           while (spatIterator.hasNext()) {
               var kvp = spatIterator.next();
               Windowed<RsuIntersectionKey> key = kvp.key;
               Instant startTime = key.window().startTime();
               Instant endTime = key.window().endTime();
               RsuIntersectionKey theKey= key.key();
               ProcessedSpat value = kvp.value;
               Integer intersectionId = value.getIntersectionId();
               TreeMap<String, TreeMap<String, ProcessedSpat>> spats = null;
               if (intersectionMap.containsKey(intersectionId)) {
                   spats = intersectionMap.get(intersectionId);
               } else {
                   spats = new TreeMap<String, TreeMap<String, ProcessedSpat>>();
                   intersectionMap.put(intersectionId, spats);
               }
               String window = String.format("%s / %s", formatter.format(startTime.atZone(ZoneOffset.UTC)), formatter.format(endTime.atZone(ZoneOffset.UTC)));
               TreeMap<String, ProcessedSpat> spatList = null;
               if (spats.containsKey(window)) {
                   spatList = spats.get(window);
               } else {
                   spatList = new TreeMap<String, ProcessedSpat>();
                   spats.put(window, spatList);
               }
               spatList.put(theKey.toString(), value);
           }
       }
       return getJsonResponse(intersectionMap);
    }

   @GetMapping(value = "/bsm-window-store")
   public @ResponseBody ResponseEntity<String> bsmWindowStore() {
        var bsmWindowStore = intersectionEventTopology.getBsmWindowStore();
        var intersectionMap = new IntersectionBsm();
        var formatter = DateTimeFormatter.ISO_DATE_TIME;
        try (var bsmIterator = bsmWindowStore.all()) {
            while (bsmIterator.hasNext()) {
                var kvp = bsmIterator.next();
                Windowed<BsmIntersectionIdKey> key = kvp.key;
                Instant startTime = key.window().startTime();
                Instant endTime = key.window().endTime();
                BsmIntersectionIdKey theKey= key.key();
                OdeBsmData value = kvp.value;
                // Integer intersectionId = value.();
                String vehicleId = IntersectionEventTopology.getBsmID(value);
                TreeMap<String, TreeMap<String, OdeBsmData>> bsms = null;
                if (intersectionMap.containsKey(vehicleId)) {
                    bsms = intersectionMap.get(vehicleId);
                } else {
                    bsms = new TreeMap<String, TreeMap<String, OdeBsmData>>();
                    intersectionMap.put(vehicleId, bsms);
                }
                String window = String.format("%s / %s", formatter.format(startTime.atZone(ZoneOffset.UTC)), formatter.format(endTime.atZone(ZoneOffset.UTC)));
                TreeMap<String, OdeBsmData> bsmList = null;
                if (bsms.containsKey(window)) {
                    bsmList = bsms.get(window);
                } else {
                    bsmList = new TreeMap<String, OdeBsmData>();
                    bsms.put(window, bsmList);
                }
                bsmList.put(theKey.toString(), value);
            }
        }
        return getJsonResponse(intersectionMap);
   }

    private Map<String, KafkaStreams> getKafkaStreamsMap() {

        
        var streamsMap = new TreeMap<String, KafkaStreams>();

        // Streams not part of an algorithm
        streamsMap.putAll(monitorServiceController.getStreamsMap());

        // Algorithm streams
        for (String key : monitorServiceController.getAlgoMap().keySet()) {
            var algorithm = monitorServiceController.getAlgoMap().get(key);
            if (algorithm instanceof StreamsTopology) {
                var streamsTopo = (StreamsTopology)algorithm;
                streamsMap.put(key, streamsTopo.getStreams());
            }
        }
        return streamsMap;
    }

    private ResponseEntity<String> getOKResponse(String message) {
        return ResponseEntity.status(HttpStatus.OK).contentType(MediaType.TEXT_PLAIN).body(message);
    }

    private ResponseEntity<String> getJsonResponse(Object message) {
        String json;
        try {
            json = mapper.writeValueAsString(message);
        } catch (JsonProcessingException jpe) {
            logger.error("Error converting to JSON", jpe);
           return getErrorJson(jpe);
        }
        return ResponseEntity.status(HttpStatus.OK).contentType(MediaType.APPLICATION_JSON).body(json);
    }

    private ResponseEntity<String> getErrorJson(Exception ex) {
        var errMap = Map.of("error", ex.getMessage());
        try {
            String errJson = mapper.writeValueAsString(errMap);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).contentType(MediaType.APPLICATION_JSON).body(errJson);
        } catch (JsonProcessingException e) { 
            logger.error("Error converting to JSON", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).contentType(MediaType.APPLICATION_JSON).body("{ \"error\": \"error\" }");
        }
        
    }

    public class StreamsInfoMap extends TreeMap<String, StreamsInfo> {}
    

    @Getter
    @Setter
    public class StreamsInfo {
        State state;
        String detailsUrl;      
    }

    

    public class MetricsGroupMap extends TreeMap<String, MetricsGroup> { }

    public class MetricsGroup extends TreeMap<String, Object> { }

    private String baseUrl() {
        return ServletUriComponentsBuilder.fromCurrentContextPath().build().toUriString();
    }

    public class IntersectionSpatMap extends TreeMap<Integer, TreeMap<String, TreeMap<String, ProcessedSpat>>> {}
    public class IntersectionBsm extends TreeMap<String, TreeMap<String, TreeMap<String, OdeBsmData>>> {}



}

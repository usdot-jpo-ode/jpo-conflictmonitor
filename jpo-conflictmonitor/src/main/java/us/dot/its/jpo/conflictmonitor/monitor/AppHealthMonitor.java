package us.dot.its.jpo.conflictmonitor.monitor;

import java.time.Instant;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

import jakarta.servlet.http.HttpServletRequest;
import org.apache.kafka.common.MetricName;
import org.apache.kafka.streams.KafkaStreams;
import org.apache.kafka.streams.KafkaStreams.State;
import org.apache.kafka.streams.Topology;
import org.apache.kafka.streams.TopologyDescription;
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
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

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
import us.dot.its.jpo.conflictmonitor.monitor.health.TopologyGraph;
import us.dot.its.jpo.conflictmonitor.monitor.models.bsm.BsmIntersectionIdKey;
import us.dot.its.jpo.conflictmonitor.monitor.models.config.DefaultConfigMap;
import us.dot.its.jpo.conflictmonitor.monitor.models.config.IntersectionConfigMap;
import us.dot.its.jpo.conflictmonitor.monitor.models.map.MapIndex;
import us.dot.its.jpo.conflictmonitor.monitor.topologies.config.ConfigTopology;
import us.dot.its.jpo.conflictmonitor.monitor.topologies.IntersectionEventTopology;
import us.dot.its.jpo.conflictmonitor.monitor.utils.BsmUtils;
import us.dot.its.jpo.geojsonconverter.partitioner.RsuIntersectionKey;
import us.dot.its.jpo.geojsonconverter.pojos.geojson.LineString;
import us.dot.its.jpo.geojsonconverter.pojos.geojson.Point;
import us.dot.its.jpo.geojsonconverter.pojos.geojson.bsm.ProcessedBsm;
import us.dot.its.jpo.geojsonconverter.pojos.geojson.map.ProcessedMap;
import us.dot.its.jpo.geojsonconverter.pojos.spat.ProcessedSpat;

import javax.ws.rs.Produces;

/**
 * REST controller providing health and configuration endpoints for the Conflict Monitor application.
 * <p>
 * Exposes endpoints for monitoring Kafka topics, streams, topologies, configuration, spatial indexes,
 * and window stores for SPaT and BSM data. Also provides links to connectors and application properties.
 */
@Getter
@Setter
@RestController
@RequestMapping(path = "/health", produces = MediaType.APPLICATION_JSON_VALUE)
@DependsOn("createKafkaTopics")
@Profile("!test && !testConfig")
public class AppHealthMonitor {

    /** Jackson ObjectMapper for JSON serialization. */
    private static final ObjectMapper mapper;

    /** Logger for AppHealthMonitor operations. */
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

    /**
     * Returns a list of configuration and algorithm parameter objects.
     * Used for reporting application properties.
     *
     * @return list of configuration and parameter objects
     */
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

    /**
     * Returns a summary of available health endpoints as a map of links.
     *
     * @return response entity containing a map of endpoint names to URLs
     */
    @GetMapping
    public @ResponseBody ResponseEntity<Object> summary() {

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
                "map-store",
                "topologies"
            );
        return getResponse(linkMap);

    }

    /**
     * Adds endpoint links to the provided map.
     *
     * @param map   the map to populate
     * @param paths endpoint paths to add
     */
    private void addLinks(Map<String, String> map, String... paths) {
        for (String path : paths) {
            var link = String.format("%s/health/%s", baseUrl(), path);
            map.put(path, link);
        }
    }

    /**
     * Lists the default configuration map.
     *
     * @return response entity containing the default configuration map
     */
    @GetMapping(value = "/config/default")
    public @ResponseBody ResponseEntity<DefaultConfigMap> listDefaultConfig() {

        return getResponse(configTopology.mapDefaultConfigs());

    }

    /**
     * Lists the intersection-specific configuration map.
     *
     * @return response entity containing the intersection configuration map
     */
    @GetMapping(value = "/config/intersection")
    public @ResponseBody ResponseEntity<IntersectionConfigMap> listIntersectionConfig() {
        return getResponse(configTopology.mapIntersectionConfigs());
    }
    
    /**
     * Lists Kafka topics created by this application that currently exist.
     *
     * @return response entity containing a map of topic names to descriptions
     */
    @GetMapping(value = "/topics")
    public @ResponseBody ResponseEntity<TreeMap<String, String>> listTopics() {

        var existingTopics = new TreeMap<String, String>();

        var topicNames = kafkaConfiguration.getCreateTopics().stream()
            .filter(topic -> topic.keySet().contains("name"))
            .map(topic -> (String)topic.get("name"))
            .collect(Collectors.toUnmodifiableList());

        var topicDescMap = kafkaAdmin.describeTopics(topicNames.toArray(new String[0]));

        for (var entry : topicDescMap.entrySet()) {
            existingTopics.put(entry.getKey(), entry.getValue().toString());
        }

        return getResponse(existingTopics);

    }

    /**
     * Lists application configuration and algorithm properties.
     *
     * @return response entity containing a map of property names to values
     */
    @GetMapping(value = "/properties")
    public @ResponseBody ResponseEntity<TreeMap<String, Object>> listProperties() {
        var propMap = new TreeMap<String, Object>();

        for (var params : parameterObjects()) {
           propMap.put(params.getClass().getSimpleName(), params);
        }

        return getResponse(propMap);
    }

    /**
     * Lists all Kafka Streams topologies and their states.
     *
     * @return response entity containing a map of stream names to stream info
     */
    @GetMapping(value = "/streams")
    public @ResponseBody ResponseEntity<StreamsInfoMap> listStreams() {
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
        return getResponse(result);
    }

    /**
     * Lists all Kafka Streams topologies and provides links to their details and DOT graph representations.
     *
     * @return response entity containing a map of topology names to info links
     */
    @GetMapping(value = "/topologies")
    public @ResponseBody ResponseEntity<TreeMap<String, TopologyInfoLinks>> listTopologies() {
        var topoMap = getTopologies();
        String baseUrl = baseUrl();
        var result = new TreeMap<String, TopologyInfoLinks>();
        for (Map.Entry<String, Topology> entry : topoMap.entrySet()) {
            String name = entry.getKey();
            String detailUrl = String.format("%s/health/topologies/detail/%s", baseUrl, name);
            String simpleGraphUrl = String.format("%s/health/topologies/simple/%s", baseUrl, name);
            result.put(name, new TopologyInfoLinks(detailUrl, simpleGraphUrl));
        }
        String allUrl = String.format("%s/health/topologies/simple/all", baseUrl);
        result.put("all", new TopologyInfoLinks("n/a", allUrl));
        return getResponse(result);
    }

    /**
     * Returns the detailed description of a specific topology.
     *
     * @param name the name of the topology
     * @return response entity containing the topology description as plain text
     */
    @GetMapping(value = "/topologies/detail/{name}")
    @Produces(MediaType.TEXT_PLAIN_VALUE)
    public @ResponseBody ResponseEntity<String> topologyDetails(@PathVariable String name) {
        var topoMap = getTopologies();
        if (!topoMap.containsKey(name)) {
            throw new RuntimeException("The topology map doesn't contain an object named " + name);
        }
        Topology topology = topoMap.get(name);
        TopologyDescription description = topology.describe();
        return ResponseEntity.ok().contentType(MediaType.TEXT_PLAIN).body(description.toString());
    }

    /**
     * Returns a DOT graph representation of a specific topology or all topologies.
     *
     * @param name the name of the topology or "all" for all topologies
     * @return response entity containing the DOT graph as plain text
     */
    @GetMapping(value = "/topologies/simple/{name}")
    @Produces(MediaType.TEXT_PLAIN_VALUE)
    public @ResponseBody ResponseEntity<String> topologySimpleGraph(@PathVariable String name) {
        TopologyGraph graph;
        var topoMap = getTopologies();
        if ("all".equals(name)) {
            graph = new TopologyGraph(topoMap);
        } else {
            if (!topoMap.containsKey(name)) {
                throw new RuntimeException("The topology map doesn't contain an object named " + name);
            }
            Topology topology = topoMap.get(name);
            graph = new TopologyGraph(name, topology);
        }
        return ResponseEntity.ok()
                .contentType(MediaType.TEXT_PLAIN)
                .header("Content-Disposition", String.format("inline; filename=\"%s.dot\"", name))
                .body(graph.exportDOT());
    }

    /**
     * Returns metrics for a named Kafka Streams instance.
     *
     * @param name the name of the streams instance
     * @return response entity containing a map of metrics grouped by metric group
     */
    @GetMapping(value = "/streams/{name}")
    public @ResponseBody ResponseEntity<MetricsGroupMap> namedStreams(@PathVariable String name) {

        Map<String, KafkaStreams> streamsMap = getKafkaStreamsMap();
        if (streamsMap == null)
            throw new RuntimeException("The streams map is null.");
        if (!streamsMap.containsKey(name))
            throw new RuntimeException("The streams map doesn't contain an object named " + name);
        KafkaStreams streams = streamsMap.get(name);
        if (streams == null)
            throw new RuntimeException("The KafkaStreams object is null");

        
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
        return getResponse(result);
       
    }

    /**
     * Returns the status of Kafka Connect connectors.
     *
     * @return response entity containing the connectors' status as a JSON string
     */
    @GetMapping(value = "/connectors")
    public @ResponseBody ResponseEntity<String> connectors() {
        final var restTemplate = new RestTemplate();
        final var url = String.format("%s/connectors?expand=status", 
                conflictMonitorProperties.getConnectURL());
        ResponseEntity<String> response = restTemplate.getForEntity(url, String.class);
        return response;
    }

    /**
     * Returns all items in the spatial index (quadtree).
     *
     * @return response entity containing a list of all spatial index items
     */
    @GetMapping(value = "/spatial-indexes")
    public @ResponseBody ResponseEntity<List> spatial() {
        if (mapIndex == null) {
            throw new RuntimeException("The map index is null");
        }

        Quadtree quadtree = mapIndex.getQuadtree();
        var allItems = quadtree.queryAll();
        return getResponse(allItems);
    }

    /**
     * Returns the contents of the MAP store.
     *
     * @return response entity containing a map of intersection keys to processed MAPs
     */
    @GetMapping(value = "/map-store")
    public @ResponseBody ResponseEntity<TreeMap<String, ProcessedMap<LineString>>> mapStore() {
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
        return getResponse(mapMap);
    }

    /**
     * Returns the contents of the SPaT window store, grouped by intersection and window.
     *
     * @return response entity containing a nested map of intersection IDs, windows, and processed SPaTs
     */
    @GetMapping(value = "/spat-window-store")
    public @ResponseBody ResponseEntity<IntersectionSpatMap> spatWindowStore() {
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
       return getResponse(intersectionMap);
    }

    /**
     * Returns the contents of the BSM window store, grouped by vehicle and window.
     *
     * @return response entity containing a nested map of vehicle IDs, windows, and BSM data
     */
   @GetMapping(value = "/bsm-window-store")
   public @ResponseBody ResponseEntity<IntersectionBsm> bsmWindowStore() {
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
                ProcessedBsm<Point> value = kvp.value;
                // Integer intersectionId = value.();
                String vehicleId = BsmUtils.getVehicleId(value);
                TreeMap<String, TreeMap<String, ProcessedBsm<Point>>> bsms = null;
                if (intersectionMap.containsKey(vehicleId)) {
                    bsms = intersectionMap.get(vehicleId);
                } else {
                    bsms = new TreeMap<>();
                    intersectionMap.put(vehicleId, bsms);
                }
                String window = String.format("%s / %s", formatter.format(startTime.atZone(ZoneOffset.UTC)), formatter.format(endTime.atZone(ZoneOffset.UTC)));
                TreeMap<String, ProcessedBsm<Point>> bsmList = null;
                if (bsms.containsKey(window)) {
                    bsmList = bsms.get(window);
                } else {
                    bsmList = new TreeMap<>();
                    bsms.put(window, bsmList);
                }
                bsmList.put(theKey.toString(), value);
            }
        }
        return getResponse(intersectionMap);
   }

    /**
     * Returns a map of Kafka Streams instances keyed by algorithm name.
     *
     * @return map of stream names to KafkaStreams instances
     */
    private Map<String, KafkaStreams> getKafkaStreamsMap() {

        var streamsMap = new TreeMap<String, KafkaStreams>();

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

    /**
     * Returns a map of Kafka Streams topologies keyed by algorithm name.
     *
     * @return map of topology names to Topology objects
     */
    private Map<String, Topology> getTopologies() {
        var topoMap = new TreeMap<String, Topology>();
        for (Map.Entry<String, StreamsTopology> algoEntry : monitorServiceController.getAlgoMap().entrySet()) {
            String key = algoEntry.getKey();
            StreamsTopology streamsTopology = algoEntry.getValue();
            Topology topology = streamsTopology.getTopology();
            if (topology != null) {
                topoMap.put(key, topology);
            } else {
                logger.error("Topology is not created in {}", streamsTopology);
            }
        }
        return topoMap;
    }

    /**
     * Helper method to create a ResponseEntity with HTTP 200 and JSON content type.
     *
     * @param message the response body
     * @param <T>     the type of the response body
     * @return ResponseEntity with the given message
     */
    private <T> ResponseEntity<T> getResponse(T message) {
        return ResponseEntity.status(HttpStatus.OK).contentType(MediaType.APPLICATION_JSON).body(message);
    }

    /**
     * Exception handler for all exceptions, returning a JSON error message.
     *
     * @param request the HTTP request
     * @param ex      the exception thrown
     * @return ResponseEntity with error message and HTTP 500
     */
    @ExceptionHandler(Exception.class)
    private ResponseEntity<String> getErrorJson(HttpServletRequest request, Exception ex) {
        try {
            var errMap = Map.of("error", ex.getMessage());
            String errJson = mapper.writeValueAsString(errMap);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).contentType(MediaType.APPLICATION_JSON).body(errJson);
        } catch (Exception e) {
            logger.error("Exception in exception handler", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).contentType(MediaType.APPLICATION_JSON).body("{ \"error\": \"unknown error\" }");
        }
        
    }

    /** Map of stream names to StreamsInfo objects. */
    public class StreamsInfoMap extends TreeMap<String, StreamsInfo> {}

    /**
     * Information about a Kafka Streams instance, including state and details URL.
     */
    @Getter
    @Setter
    public class StreamsInfo {
        State state;
        String detailsUrl;      
    }

    /** Map of metric group names to MetricsGroup objects. */
    public class MetricsGroupMap extends TreeMap<String, MetricsGroup> { }

    /** Map of metric names to metric values for a given group. */
    public class MetricsGroup extends TreeMap<String, Object> { }

    /**
     * Returns the base URL for the current servlet context.
     *
     * @return base URL as a string
     */
    private String baseUrl() {
        return ServletUriComponentsBuilder.fromCurrentContextPath().build().toUriString();
    }

    /** Nested map structure for SPaT window store: intersectionId -> window -> key -> ProcessedSpat. */
    public class IntersectionSpatMap extends TreeMap<Integer, TreeMap<String, TreeMap<String, ProcessedSpat>>> {}
    public class IntersectionBsm extends TreeMap<String, TreeMap<String, TreeMap<String, ProcessedBsm<Point>>>> {}

    /**
     * Record containing URLs for topology details and simple DOT graph.
     *
     * @param detailsUrl     URL for topology details
     * @param simpleGraphUrl URL for DOT graph
     */
    public record TopologyInfoLinks(String detailsUrl, String simpleGraphUrl) {}

}

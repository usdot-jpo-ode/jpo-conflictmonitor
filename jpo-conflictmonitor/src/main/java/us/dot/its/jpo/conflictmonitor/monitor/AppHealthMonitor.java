package us.dot.its.jpo.conflictmonitor.monitor;

import java.util.ArrayList;
import java.util.Formatter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import org.apache.kafka.clients.admin.TopicDescription;
import org.apache.kafka.common.Metric;
import org.apache.kafka.common.MetricName;
import org.apache.kafka.streams.KafkaStreams;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.kafka.core.KafkaAdmin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;
import java.util.Optional;
import java.util.stream.Collectors;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import lombok.Getter;
import lombok.Setter;
import us.dot.its.jpo.conflictmonitor.KafkaConfiguration;
import us.dot.its.jpo.conflictmonitor.monitor.algorithms.StreamsTopology;

@RestController
@RequestMapping(path = "/health")
public class AppHealthMonitor {

    private final ObjectMapper mapper = new ObjectMapper();

    private static final Logger logger = LoggerFactory.getLogger(AppHealthMonitor.class);

    @Autowired
    @Getter
    @Setter
    private MonitorServiceController monitorServiceController;

    @Autowired
    @Getter
    @Setter
    private KafkaAdmin kafkaAdmin;

    @Autowired
    @Getter
    @Setter
    private KafkaConfiguration kafkaConfiguration;

    /**
     * @return JSON map of kafka topics created by this app that currently exist
     */
    @GetMapping(value = "/topics", produces = "application/json")
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
            String jsonResult = mapper.writeValueAsString(existingTopics);
            return ResponseEntity.status(HttpStatus.OK).contentType(MediaType.APPLICATION_JSON).body(jsonResult);
        } catch (Exception ex) {
            String errorJson = String.format("{ \"error\": \"%s\" }", ex.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).contentType(MediaType.APPLICATION_JSON).body(errorJson);
        }
    }

    @GetMapping(value = "/streams", produces = "*/*")
    public @ResponseBody ResponseEntity<String> listStreams() {
        var streamsMap = getKafkaStreamsMap();
        String baseUrl = ServletUriComponentsBuilder.fromCurrentContextPath().build().toUriString();
        var result = new StreamsInfoMap();
        for (String name : streamsMap.keySet()) {
            var streamsInfo = new StreamsInfo();
            String url = String.format("%s/health/streams/%s", baseUrl, name);
            streamsInfo.setDetailsUrl(url);
            result.put(name, streamsInfo);

            KafkaStreams streams = streamsMap.get(name);
            Object state = null;
            if (streams == null) {
                state = "KafkaStreams object is null";
                continue;
            } 
            Optional<? extends Metric> statusMetric = streams.metrics().values().stream()
                .filter(metric -> 
                    "stream-metrics".equals(metric.metricName().group()) &&
                    "state".equals(metric.metricName().name()))
                .findFirst();
            state = statusMetric.isPresent() ? statusMetric.get().metricValue() : "Stream state metric not found.  Streams is probably shut down.";
            streamsInfo.setState(state);
            
        }
        return getJsonResponse(result);     
    }

    @GetMapping(value = "/streams/{name}", produces = "*/*")
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

    private Map<String, KafkaStreams> getKafkaStreamsMap() {

        
        var streamsMap = new HashMap<String, KafkaStreams>();

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
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).contentType(MediaType.TEXT_PLAIN).body(jpe.getMessage());
        }
        return ResponseEntity.status(HttpStatus.OK).contentType(MediaType.APPLICATION_JSON).body(json);
    }

    public class StreamsInfoMap extends TreeMap<String, StreamsInfo> {
    }

    @Getter
    @Setter
    public class StreamsInfo {
        Object state;
        String detailsUrl;      
    }

    public class MetricsGroupMap extends TreeMap<String, MetricsGroup> { }

    public class MetricsGroup extends TreeMap<String, Object> { }

    

}

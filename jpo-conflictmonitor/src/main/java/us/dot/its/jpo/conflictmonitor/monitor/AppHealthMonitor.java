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
import org.apache.kafka.streams.KafkaStreams.State;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.DependsOn;
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
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;

import lombok.Getter;
import lombok.Setter;
import us.dot.its.jpo.conflictmonitor.ConflictMonitorProperties;
import us.dot.its.jpo.conflictmonitor.KafkaConfiguration;

import us.dot.its.jpo.conflictmonitor.monitor.algorithms.StreamsTopology;
import us.dot.its.jpo.conflictmonitor.monitor.algorithms.connection_of_travel.ConnectionOfTravelParameters;
import us.dot.its.jpo.conflictmonitor.monitor.algorithms.connection_of_travel_assessment.ConnectionOfTravelAssessmentParameters;
import us.dot.its.jpo.conflictmonitor.monitor.algorithms.lane_direction_of_travel.LaneDirectionOfTravelParameters;
import us.dot.its.jpo.conflictmonitor.monitor.algorithms.lane_direction_of_travel_assessment.LaneDirectionOfTravelAssessmentParameters;
import us.dot.its.jpo.conflictmonitor.monitor.algorithms.repartition.RepartitionParameters;
import us.dot.its.jpo.conflictmonitor.monitor.algorithms.signal_state_event_assessment.SignalStateEventAssessmentParameters;
import us.dot.its.jpo.conflictmonitor.monitor.algorithms.signal_state_vehicle_crosses.SignalStateVehicleCrossesParameters;
import us.dot.its.jpo.conflictmonitor.monitor.algorithms.signal_state_vehicle_stops.SignalStateVehicleStopsParameters;
import us.dot.its.jpo.conflictmonitor.monitor.algorithms.time_change_details.map.MapTimeChangeDetailsParameters;
import us.dot.its.jpo.conflictmonitor.monitor.algorithms.time_change_details.spat.SpatTimeChangeDetailsParameters;
import us.dot.its.jpo.conflictmonitor.monitor.algorithms.validation.map.MapValidationParameters;
import us.dot.its.jpo.conflictmonitor.monitor.algorithms.validation.spat.SpatValidationParameters;
import us.dot.its.jpo.conflictmonitor.monitor.algorithms.map_spat_message_assessment.MapSpatMessageAssessmentParameters;

@Getter
@Setter
@RestController
@RequestMapping(path = "/health")
@DependsOn("createKafkaTopics")
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

    @Autowired ConnectionOfTravelParameters connectionParams;
    @Autowired ConnectionOfTravelAssessmentParameters connectionAssessmentParams;
    @Autowired LaneDirectionOfTravelParameters laneParameters;
    @Autowired LaneDirectionOfTravelAssessmentParameters laneAssessmentParams;
    @Autowired MapSpatMessageAssessmentParameters mapSpatAssessmentParams;
    @Autowired RepartitionParameters repartitionParams;
    @Autowired SignalStateEventAssessmentParameters signalStateParams;
    @Autowired SignalStateVehicleCrossesParameters crossesParams;
    @Autowired SignalStateVehicleStopsParameters stopsParams;
    @Autowired MapTimeChangeDetailsParameters mapTimeChangeParams;
    @Autowired SpatTimeChangeDetailsParameters spatTimeChangeParams;
    @Autowired MapValidationParameters mapValidationParams;
    @Autowired SpatValidationParameters spatValidationparams;
    

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

            return getJsonResponse(existingTopics);
        } catch (Exception ex) {
            return getErrorJson(ex);
        }
    }


    


    @GetMapping(value = "/properties", produces = "application/json")
    public @ResponseBody ResponseEntity<String> listProperties() {
        try {
            var propMap = new TreeMap<String, Object>();
            propMap.put(conflictMonitorProperties.getClass().getSimpleName(), conflictMonitorProperties);
            propMap.put(kafkaConfiguration.getClass().getSimpleName(), kafkaConfiguration);
            propMap.put(connectionParams.getClass().getSimpleName(), connectionParams);
            propMap.put(connectionAssessmentParams.getClass().getSimpleName(), connectionAssessmentParams);
            propMap.put(laneParameters.getClass().getSimpleName(), laneParameters);
            propMap.put(laneAssessmentParams.getClass().getSimpleName(), laneAssessmentParams);
            propMap.put(mapSpatAssessmentParams.getClass().getSimpleName(), mapSpatAssessmentParams);
            propMap.put(repartitionParams.getClass().getSimpleName(), repartitionParams);
            propMap.put(signalStateParams.getClass().getSimpleName(), signalStateParams);
            propMap.put(crossesParams.getClass().getSimpleName(), crossesParams);
            propMap.put(stopsParams.getClass().getSimpleName(), stopsParams);
            propMap.put(mapTimeChangeParams.getClass().getSimpleName(), mapTimeChangeParams);
            propMap.put(spatTimeChangeParams.getClass().getSimpleName(), spatTimeChangeParams);
            propMap.put(mapValidationParams.getClass().getSimpleName(), mapValidationParams);
            propMap.put(spatValidationparams.getClass().getSimpleName(), spatValidationparams);
            
            return getJsonResponse(propMap);
        } catch (Exception ex) {
            return getErrorJson(ex);
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
            if (streams == null) {
                continue;
            } 
            var state = streams.state();
            streamsInfo.setState(state != null ? state : null);
            
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
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).contentType(MediaType.APPLICATION_JSON).body("{ \"error\": \"error\" }");
        }
        
    }

    public class StreamsInfoMap extends TreeMap<String, StreamsInfo> {
    }

    @Getter
    @Setter
    public class StreamsInfo {
        State state;
        String detailsUrl;      
    }

    public class MetricsGroupMap extends TreeMap<String, MetricsGroup> { }

    public class MetricsGroup extends TreeMap<String, Object> { }

    

}

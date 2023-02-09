package us.dot.its.jpo.conflictmonitor.monitor;

import java.util.concurrent.ConcurrentHashMap;

import org.apache.kafka.streams.KafkaStreams;
import org.apache.kafka.streams.StoreQueryParameters;
import org.apache.kafka.streams.Topology;
import org.apache.kafka.streams.KafkaStreams.State;
import org.apache.kafka.streams.state.QueryableStoreTypes;
import org.apache.kafka.streams.state.ReadOnlyKeyValueStore;
import org.apache.kafka.streams.state.ReadOnlyWindowStore;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.DependsOn;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Controller;

import com.fasterxml.jackson.databind.ObjectMapper;

import us.dot.its.jpo.conflictmonitor.ConflictMonitorProperties;
import us.dot.its.jpo.conflictmonitor.monitor.algorithms.Algorithm;
import us.dot.its.jpo.conflictmonitor.monitor.algorithms.StreamsTopology;
import us.dot.its.jpo.conflictmonitor.monitor.algorithms.connection_of_travel.ConnectionOfTravelAlgorithm;
import us.dot.its.jpo.conflictmonitor.monitor.algorithms.connection_of_travel.ConnectionOfTravelAlgorithmFactory;
import us.dot.its.jpo.conflictmonitor.monitor.algorithms.connection_of_travel.ConnectionOfTravelParameters;
import us.dot.its.jpo.conflictmonitor.monitor.algorithms.connection_of_travel_assessment.ConnectionOfTravelAssessmentAlgorithm;
import us.dot.its.jpo.conflictmonitor.monitor.algorithms.connection_of_travel_assessment.ConnectionOfTravelAssessmentAlgorithmFactory;
import us.dot.its.jpo.conflictmonitor.monitor.algorithms.connection_of_travel_assessment.ConnectionOfTravelAssessmentParameters;
import us.dot.its.jpo.conflictmonitor.monitor.algorithms.intersection_event.IntersectionEventAlgorithm;
import us.dot.its.jpo.conflictmonitor.monitor.algorithms.intersection_event.IntersectionEventAlgorithmFactory;
import us.dot.its.jpo.conflictmonitor.monitor.algorithms.intersection_event.IntersectionEventStreamsAlgorithm;
import us.dot.its.jpo.conflictmonitor.monitor.algorithms.lane_direction_of_travel.LaneDirectionOfTravelAlgorithm;
import us.dot.its.jpo.conflictmonitor.monitor.algorithms.lane_direction_of_travel.LaneDirectionOfTravelAlgorithmFactory;
import us.dot.its.jpo.conflictmonitor.monitor.algorithms.lane_direction_of_travel.LaneDirectionOfTravelParameters;
import us.dot.its.jpo.conflictmonitor.monitor.algorithms.lane_direction_of_travel_assessment.LaneDirectionOfTravelAssessmentAlgorithm;
import us.dot.its.jpo.conflictmonitor.monitor.algorithms.lane_direction_of_travel_assessment.LaneDirectionOfTravelAssessmentAlgorithmFactory;
import us.dot.its.jpo.conflictmonitor.monitor.algorithms.lane_direction_of_travel_assessment.LaneDirectionOfTravelAssessmentParameters;
import us.dot.its.jpo.conflictmonitor.monitor.algorithms.map_spat_message_assessment.MapSpatMessageAssessmentAlgorithm;
import us.dot.its.jpo.conflictmonitor.monitor.algorithms.map_spat_message_assessment.MapSpatMessageAssessmentAlgorithmFactory;
import us.dot.its.jpo.conflictmonitor.monitor.algorithms.map_spat_message_assessment.MapSpatMessageAssessmentParameters;
import us.dot.its.jpo.conflictmonitor.monitor.algorithms.repartition.RepartitionAlgorithm;
import us.dot.its.jpo.conflictmonitor.monitor.algorithms.repartition.RepartitionAlgorithmFactory;
import us.dot.its.jpo.conflictmonitor.monitor.algorithms.repartition.RepartitionParameters;
import us.dot.its.jpo.conflictmonitor.monitor.algorithms.signal_state_event_assessment.SignalStateEventAssessmentAlgorithm;
import us.dot.its.jpo.conflictmonitor.monitor.algorithms.signal_state_event_assessment.SignalStateEventAssessmentAlgorithmFactory;
import us.dot.its.jpo.conflictmonitor.monitor.algorithms.signal_state_event_assessment.SignalStateEventAssessmentParameters;
import us.dot.its.jpo.conflictmonitor.monitor.algorithms.signal_state_vehicle_crosses.SignalStateVehicleCrossesAlgorithm;
import us.dot.its.jpo.conflictmonitor.monitor.algorithms.signal_state_vehicle_crosses.SignalStateVehicleCrossesAlgorithmFactory;
import us.dot.its.jpo.conflictmonitor.monitor.algorithms.signal_state_vehicle_crosses.SignalStateVehicleCrossesParameters;
import us.dot.its.jpo.conflictmonitor.monitor.algorithms.signal_state_vehicle_stops.SignalStateVehicleStopsAlgorithm;
import us.dot.its.jpo.conflictmonitor.monitor.algorithms.signal_state_vehicle_stops.SignalStateVehicleStopsAlgorithmFactory;
import us.dot.its.jpo.conflictmonitor.monitor.algorithms.signal_state_vehicle_stops.SignalStateVehicleStopsParameters;
import us.dot.its.jpo.conflictmonitor.monitor.algorithms.time_change_details.spat.SpatTimeChangeDetailsAlgorithm;
import us.dot.its.jpo.conflictmonitor.monitor.algorithms.time_change_details.spat.SpatTimeChangeDetailsAlgorithmFactory;
import us.dot.its.jpo.conflictmonitor.monitor.algorithms.time_change_details.spat.SpatTimeChangeDetailsParameters;
import us.dot.its.jpo.conflictmonitor.monitor.algorithms.validation.map.MapValidationAlgorithm;
import us.dot.its.jpo.conflictmonitor.monitor.algorithms.validation.map.MapValidationAlgorithmFactory;
import us.dot.its.jpo.conflictmonitor.monitor.algorithms.validation.map.MapValidationParameters;
import us.dot.its.jpo.conflictmonitor.monitor.algorithms.validation.spat.SpatValidationAlgorithm;
import us.dot.its.jpo.conflictmonitor.monitor.algorithms.validation.spat.SpatValidationParameters;
import us.dot.its.jpo.conflictmonitor.monitor.algorithms.validation.spat.SpatValidationStreamsAlgorithmFactory;
import us.dot.its.jpo.conflictmonitor.monitor.models.events.KafkaStreamsStateChangeEvent;
import us.dot.its.jpo.conflictmonitor.monitor.topologies.BsmEventTopology;
import us.dot.its.jpo.conflictmonitor.monitor.topologies.MessageIngestTopology;
import us.dot.its.jpo.geojsonconverter.pojos.geojson.map.ProcessedMap;
import us.dot.its.jpo.geojsonconverter.pojos.spat.ProcessedSpat;
import us.dot.its.jpo.conflictmonitor.monitor.topologies.IntersectionEventTopology;
import us.dot.its.jpo.ode.model.OdeBsmData;
import lombok.Getter;

/**
 * Launches ToGeoJsonFromJsonConverter service
 */
@Controller
@DependsOn("createKafkaTopics")
public class MonitorServiceController {

    private static final Logger logger = LoggerFactory.getLogger(MonitorServiceController.class);
    org.apache.kafka.common.serialization.Serdes bas;

    // Temporary for KafkaStreams that don't implement the Algorithm interface
    @Getter
    final ConcurrentHashMap<String, KafkaStreams> streamsMap = new ConcurrentHashMap<String, KafkaStreams>();

    @Getter
    final ConcurrentHashMap<String, StreamsTopology> algoMap = new ConcurrentHashMap<String, StreamsTopology>();

   
    
    @Autowired
    public MonitorServiceController(ConflictMonitorProperties conflictMonitorProps, KafkaTemplate<String, String> kafkaTemplate) {
       
        String bsmStoreName = "BsmWindowStore";
        String spatStoreName = "SpatWindowStore";
        String mapStoreName = "ProcessedMapWindowStore";

        final String stateChangeTopic = conflictMonitorProps.getKafkaStateChangeEventTopic();

        try {
            logger.info("Starting {}", this.getClass().getSimpleName());

            final String repartition = "repartition";
            RepartitionAlgorithmFactory repartitionAlgoFactory = conflictMonitorProps.getRepartitionAlgorithmFactory();
            String repAlgo = conflictMonitorProps.getRepartitionAlgorithm();
            RepartitionAlgorithm repartitionAlgo = repartitionAlgoFactory.getAlgorithm(repAlgo);
            RepartitionParameters repartitionParams = conflictMonitorProps.getRepartitionAlgorithmParameters();
            if (repartitionAlgo instanceof StreamsTopology) {     
                ((StreamsTopology)repartitionAlgo).setStreamsProperties(conflictMonitorProps.createStreamProperties(repartition));
                algoMap.put(repartition, (StreamsTopology)repartitionAlgo);
            }
            repartitionAlgo.setParameters(repartitionParams);
            Runtime.getRuntime().addShutdownHook(new Thread(repartitionAlgo::stop));
            repartitionAlgo.start();
           

            // Map Broadcast Rate Topology
            // Sends "MAP Broadcast Rate" events when the number of MAPs per rolling period is too low or too high
            final String mapBroadcastRate = "mapBroadcastRate";
            MapValidationAlgorithmFactory mapAlgoFactory = conflictMonitorProps.getMapValidationAlgorithmFactory();
            String mapAlgo = conflictMonitorProps.getMapValidationAlgorithm();
            MapValidationAlgorithm mapCountAlgo = mapAlgoFactory.getAlgorithm(mapAlgo);
            MapValidationParameters mapCountParams = conflictMonitorProps.getMapValidationParameters();
            logger.info("Map params {}", mapCountParams);
            if (mapCountAlgo instanceof StreamsTopology) {
                var mapStreamsAlgo = (StreamsTopology)mapCountAlgo;
                mapStreamsAlgo.setStreamsProperties(conflictMonitorProps.createStreamProperties(mapBroadcastRate));
                mapStreamsAlgo.registerStateListener((newState, oldState) -> stateChangeEvent(kafkaTemplate, stateChangeTopic, mapBroadcastRate, newState, oldState));
                algoMap.put(mapBroadcastRate, mapStreamsAlgo);
            }
            mapCountAlgo.setParameters(mapCountParams);
            Runtime.getRuntime().addShutdownHook(new Thread(mapCountAlgo::stop));
            mapCountAlgo.start();
           

            
            // Spat Broadcast Rate Topology
            // Sends "SPAT Broadcast Rate" events when the number of SPATs per rolling period is too low or too high
            final String spatBroadcastRate = "spatBroadcastRate";
            SpatValidationStreamsAlgorithmFactory spatAlgoFactory = conflictMonitorProps.getSpatValidationAlgorithmFactory();
            String spatAlgo = conflictMonitorProps.getSpatValidationAlgorithm();
            SpatValidationAlgorithm spatCountAlgo = spatAlgoFactory.getAlgorithm(spatAlgo);
            SpatValidationParameters spatCountParams = conflictMonitorProps.getSpatValidationParameters();
            if (spatCountAlgo instanceof StreamsTopology) {
                ((StreamsTopology)spatCountAlgo).setStreamsProperties(conflictMonitorProps.createStreamProperties(spatBroadcastRate));
                algoMap.put(spatBroadcastRate, (StreamsTopology)spatCountAlgo);
            }
            spatCountAlgo.setParameters(spatCountParams);
            Runtime.getRuntime().addShutdownHook(new Thread(spatCountAlgo::stop));
            spatCountAlgo.start();
            


            // Spat Time Change Details Assessment
            //Sends Time Change Details Events when the time deltas in spat messages are incorrect
            final String spatTimeChangeDetails = "spatTimeChangeDetails";
            SpatTimeChangeDetailsAlgorithmFactory spatTCDAlgoFactory = conflictMonitorProps.getSpatTimeChangeDetailsAlgorithmFactory();
            String spatTCDAlgo = conflictMonitorProps.getSpatTimeChangeDetailsAlgorithm();
            SpatTimeChangeDetailsAlgorithm spatTimeChangeDetailsAlgo = spatTCDAlgoFactory.getAlgorithm(spatTCDAlgo);
            SpatTimeChangeDetailsParameters spatTimeChangeDetailsParams = conflictMonitorProps.getSpatTimeChangeDetailsParameters();
            if (spatTimeChangeDetailsAlgo instanceof StreamsTopology) {
                ((StreamsTopology)spatTimeChangeDetailsAlgo).setStreamsProperties(conflictMonitorProps.createStreamProperties(spatTimeChangeDetails));
                algoMap.put(spatTimeChangeDetails, (StreamsTopology)spatTimeChangeDetailsAlgo);
            }
            spatTimeChangeDetailsAlgo.setParameters(spatTimeChangeDetailsParams);
            Runtime.getRuntime().addShutdownHook(new Thread(spatTimeChangeDetailsAlgo::stop));
            spatTimeChangeDetailsAlgo.start();
            
            



            //Map Spat Alignment Topology
            final String mapSpatAlignment = "mapSpatAlignment";
            MapSpatMessageAssessmentAlgorithmFactory mapSpatAlgoFactory = conflictMonitorProps.getMapSpatMessageAssessmentAlgorithmFactory();
            String mapSpatAlgo = conflictMonitorProps.getMapSpatMessageAssessmentAlgorithm();
            MapSpatMessageAssessmentAlgorithm mapSpatAlignmentAlgo = mapSpatAlgoFactory.getAlgorithm(mapSpatAlgo);
            MapSpatMessageAssessmentParameters mapSpatAlignmentParams = conflictMonitorProps.getMapSpatMessageAssessmentParameters();
            if (mapSpatAlignmentAlgo instanceof StreamsTopology) {
                ((StreamsTopology)mapSpatAlignmentAlgo).setStreamsProperties(conflictMonitorProps.createStreamProperties(mapSpatAlignment));
                algoMap.put(mapSpatAlignment, (StreamsTopology)mapSpatAlignmentAlgo);
            }
            mapSpatAlignmentAlgo.setParameters(mapSpatAlignmentParams);
            Runtime.getRuntime().addShutdownHook(new Thread(mapSpatAlignmentAlgo::stop));
            mapSpatAlignmentAlgo.start();



            //BSM Topology sends a message every time a vehicle drives through the intersection. 
            final String bsmEvent = "bsmEvent";
            Topology topology = BsmEventTopology.build(conflictMonitorProps.getKafkaTopicOdeBsmJson(), conflictMonitorProps.getKafkaTopicCmBsmEvent());
            KafkaStreams streams = new KafkaStreams(topology, conflictMonitorProps.createStreamProperties(bsmEvent));
            Runtime.getRuntime().addShutdownHook(new Thread(streams::close));
            streams.start(); 
            streamsMap.put(bsmEvent, streams);


            // // the message ingest topology tracks and stores incoming messages for further processing
            final String messageIngest = "messageIngest";
            var messageIngestTopology = MessageIngestTopology.build(
                conflictMonitorProps.getKafkaTopicOdeBsmJson(),
                bsmStoreName,
                conflictMonitorProps.getKafkaTopicProcessedSpat(),
                spatStoreName,
                conflictMonitorProps.getKafkaTopicProcessedMap(),
                mapStoreName
            );
            
            var messageIngestStreams = new KafkaStreams(messageIngestTopology, conflictMonitorProps.createStreamProperties(messageIngest));
            
            Runtime.getRuntime().addShutdownHook(new Thread(messageIngestStreams::close));
            messageIngestStreams.start();
            streamsMap.put(messageIngest, messageIngestStreams);

            
            Thread.sleep(20000);
            
            ReadOnlyWindowStore<String, OdeBsmData> bsmWindowStore =
            messageIngestStreams.store(StoreQueryParameters.fromNameAndType(bsmStoreName, QueryableStoreTypes.windowStore()));

            ReadOnlyWindowStore<String, ProcessedSpat> spatWindowStore =
            messageIngestStreams.store(StoreQueryParameters.fromNameAndType(spatStoreName, QueryableStoreTypes.windowStore()));

            ReadOnlyKeyValueStore<String, ProcessedMap> mapKeyValueStore =
            messageIngestStreams.store(StoreQueryParameters.fromNameAndType(mapStoreName, QueryableStoreTypes.keyValueStore()));

            
            
            // Get Algorithms used by intersection event topology

            // Setup Lane Direction of Travel Factory
            LaneDirectionOfTravelAlgorithmFactory ldotAlgoFactory = conflictMonitorProps.getLaneDirectionOfTravelAlgorithmFactory();
            String ldotAlgo = conflictMonitorProps.getLaneDirectionOfTravelAlgorithm();
            LaneDirectionOfTravelAlgorithm laneDirectionOfTravelAlgorithm = ldotAlgoFactory.getAlgorithm(ldotAlgo);
            LaneDirectionOfTravelParameters ldotParams = conflictMonitorProps.getLaneDirectionOfTravelParameters();
            
            // Setup Connection of Travel Factory
            ConnectionOfTravelAlgorithmFactory cotAlgoFactory = conflictMonitorProps.getConnectionOfTravelAlgorithmFactory();
            String cotAlgo = conflictMonitorProps.getConnectionOfTravelAlgorithm();
            ConnectionOfTravelAlgorithm connectionOfTravelAlgorithm = cotAlgoFactory.getAlgorithm(cotAlgo);
            ConnectionOfTravelParameters cotParams = conflictMonitorProps.getConnectionOfTravelParameters();
            
            // Setup Signal State Vehicle Crosses Factory
            SignalStateVehicleCrossesAlgorithmFactory ssvcAlgoFactory = conflictMonitorProps.getSignalStateVehicleCrossesAlgorithmFactory();
            String ssvcAlgo = conflictMonitorProps.getSignalStateVehicleCrossesAlgorithm();
            SignalStateVehicleCrossesAlgorithm signalStateVehicleCrossesAlgorithm = ssvcAlgoFactory.getAlgorithm(ssvcAlgo);
            SignalStateVehicleCrossesParameters ssvcParams = conflictMonitorProps.getSignalStateVehicleCrossesParameters();
            
            // Setup Signal State Vehicle Stops Factory
            SignalStateVehicleStopsAlgorithmFactory ssvsAlgoFactory = conflictMonitorProps.getSignalStateVehicleStopsAlgorithmFactory();
            String ssvsAlgo = conflictMonitorProps.getSignalStateVehicleStopsAlgorithm();
            SignalStateVehicleStopsAlgorithm signalStateVehicleStopsAlgorithm = ssvsAlgoFactory.getAlgorithm(ssvsAlgo);
            SignalStateVehicleStopsParameters ssvsParams = conflictMonitorProps.getSignalStateVehicleStopsParameters();
            

            // The IntersectionEventTopology grabs snapshots of spat / map / bsm and processes data when a vehicle passes through
            final String intersectionEvent = "intersectionEvent";
            IntersectionEventAlgorithmFactory intersectionAlgoFactory = conflictMonitorProps.getIntersectionEventAlgorithmFactory();
            String intersectionAlgoKey = conflictMonitorProps.getIntersectionEventAlgorithm();
            IntersectionEventAlgorithm intersectionAlgo = intersectionAlgoFactory.getAlgorithm(intersectionAlgoKey);
            intersectionAlgo.setConflictMonitorProperties(conflictMonitorProps);
            intersectionAlgo.setLaneDirectionOfTravelAlgorithm(laneDirectionOfTravelAlgorithm);
            intersectionAlgo.setLaneDirectionOfTravelParams(ldotParams);
            intersectionAlgo.setConnectionOfTravelAlgorithm(connectionOfTravelAlgorithm);
            intersectionAlgo.setConnectionOfTravelParams(cotParams);
            intersectionAlgo.setSignalStateVehicleCrossesAlgorithm(signalStateVehicleCrossesAlgorithm);
            intersectionAlgo.setSignalStateVehicleCrossesParameters(ssvcParams);
            intersectionAlgo.setSignalStateVehicleStopsAlgorithm(signalStateVehicleStopsAlgorithm);
            intersectionAlgo.setSignalStateVehicleStopsParameters(ssvsParams);
            if (intersectionAlgo instanceof IntersectionEventStreamsAlgorithm) {
                var intersectionStreams = (IntersectionEventStreamsAlgorithm)intersectionAlgo;
                intersectionStreams.setStreamsProperties(conflictMonitorProps.createStreamProperties(intersectionEvent));
                intersectionStreams.setBsmWindowStore(bsmWindowStore);
                intersectionStreams.setSpatWindowStore(spatWindowStore);
                intersectionStreams.setMapStore(mapKeyValueStore);
                algoMap.put(intersectionEvent, intersectionStreams);
            }
            Runtime.getRuntime().addShutdownHook(new Thread(intersectionAlgo::stop));
            intersectionAlgo.start();
            logger.info("Started intersectionEvent topology");


            


            // Signal State Event Assessment Topology
            final String signalStateEventAssessment = "signalStateEventAssessment";
            SignalStateEventAssessmentAlgorithmFactory sseaAlgoFactory = conflictMonitorProps.getSignalStateEventAssessmentAlgorithmFactory();
            String signalStateEventAssessmentAlgorithm = conflictMonitorProps.getSignalStateEventAssessmentAlgorithm();
            SignalStateEventAssessmentAlgorithm signalStateEventAssesmentAlgo = sseaAlgoFactory.getAlgorithm(signalStateEventAssessmentAlgorithm);
            SignalStateEventAssessmentParameters signalStateEventAssessmenAlgoParams = conflictMonitorProps.getSignalStateEventAssessmentAlgorithmParameters();
            if (signalStateEventAssesmentAlgo instanceof StreamsTopology) {
                ((StreamsTopology)signalStateEventAssesmentAlgo).setStreamsProperties(conflictMonitorProps.createStreamProperties(signalStateEventAssessment));
                algoMap.put(signalStateEventAssessment, (StreamsTopology)signalStateEventAssesmentAlgo);
            }
            signalStateEventAssesmentAlgo.setParameters(signalStateEventAssessmenAlgoParams);
            Runtime.getRuntime().addShutdownHook(new Thread(signalStateEventAssesmentAlgo::stop));
            signalStateEventAssesmentAlgo.start();
            

            // Lane Direction Of Travel Assessment Topology
            final String laneDirectionOfTravelAssessment = "laneDirectionOfTravelAssessment";
            LaneDirectionOfTravelAssessmentAlgorithmFactory ldotaAlgoFactory = conflictMonitorProps.getLaneDirectionOfTravelAssessmentAlgorithmFactory();
            String laneDirectionOfTravelAssessmentAlgorithm = conflictMonitorProps.getLaneDirectionOfTravelAssessmentAlgorithm();
            LaneDirectionOfTravelAssessmentAlgorithm laneDirectionOfTravelAssesmentAlgo = ldotaAlgoFactory.getAlgorithm(laneDirectionOfTravelAssessmentAlgorithm);
            LaneDirectionOfTravelAssessmentParameters laneDirectionOfTravelAssessmenAlgoParams = conflictMonitorProps.getLaneDirectionOfTravelAssessmentAlgorithmParameters();
            if (laneDirectionOfTravelAssesmentAlgo instanceof StreamsTopology) {
                ((StreamsTopology)laneDirectionOfTravelAssesmentAlgo).setStreamsProperties(conflictMonitorProps.createStreamProperties(laneDirectionOfTravelAssessment));
                algoMap.put(laneDirectionOfTravelAssessment, (StreamsTopology)laneDirectionOfTravelAssesmentAlgo);
            }
            laneDirectionOfTravelAssesmentAlgo.setParameters(laneDirectionOfTravelAssessmenAlgoParams);
            Runtime.getRuntime().addShutdownHook(new Thread(laneDirectionOfTravelAssesmentAlgo::stop));
            laneDirectionOfTravelAssesmentAlgo.start();
            


            // Connection Of Travel Assessment Topology
            final String connectionOfTravelAssessment = "connectionOfTravelAssessment";
            ConnectionOfTravelAssessmentAlgorithmFactory cotaAlgoFactory = conflictMonitorProps.getConnectionOfTravelAssessmentAlgorithmFactory();
            String connectionOfTravelAssessmentAlgorithm = conflictMonitorProps.getMapSpatMessageAssessmentAlgorithm();
            ConnectionOfTravelAssessmentAlgorithm connectionofTravelAssessmentAlgo = cotaAlgoFactory.getAlgorithm(connectionOfTravelAssessmentAlgorithm);
            ConnectionOfTravelAssessmentParameters connectionOfTravelAssessmentAlgoParams = conflictMonitorProps.getConnectionOfTravelAssessmentAlgorithmParameters();
            if (connectionofTravelAssessmentAlgo instanceof StreamsTopology) {
                ((StreamsTopology)connectionofTravelAssessmentAlgo).setStreamsProperties(conflictMonitorProps.createStreamProperties(connectionOfTravelAssessment));
                algoMap.put(connectionOfTravelAssessment, (StreamsTopology)connectionofTravelAssessmentAlgo);
            }
            connectionofTravelAssessmentAlgo.setParameters(connectionOfTravelAssessmentAlgoParams);
            Runtime.getRuntime().addShutdownHook(new Thread(connectionofTravelAssessmentAlgo::stop));
            connectionofTravelAssessmentAlgo.start();
            
            


            
            logger.info("All services started!");
        } catch (Exception e) {
            logger.error("Encountered issue with creating topologies", e);
        }
    }

    final ObjectMapper mapper = new ObjectMapper();

    public void stateChangeEvent(KafkaTemplate<String, String> kafkaTemplate, String topic, String topology, State newState, State oldState) {
        try {
            var event = new KafkaStreamsStateChangeEvent(topology, newState.toString(), oldState.toString());
            var message = mapper.writeValueAsString(event);
            kafkaTemplate.send(topic, message);
        } catch (Exception ex) {
            logger.error("Exception sending kafka state change event", ex);
        }
    }
}
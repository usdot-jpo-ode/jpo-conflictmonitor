package us.dot.its.jpo.conflictmonitor.monitor;

import java.util.concurrent.ConcurrentHashMap;

import org.apache.kafka.streams.KafkaStreams;
import org.apache.kafka.streams.StoreQueryParameters;
import org.apache.kafka.streams.Topology;
import org.apache.kafka.streams.state.QueryableStoreTypes;
import org.apache.kafka.streams.state.ReadOnlyKeyValueStore;
import org.apache.kafka.streams.state.ReadOnlyWindowStore;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.DependsOn;
import org.springframework.context.annotation.Profile;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Controller;

import us.dot.its.jpo.conflictmonitor.ConflictMonitorProperties;
import us.dot.its.jpo.conflictmonitor.StateChangeHandler;
import us.dot.its.jpo.conflictmonitor.StreamsExceptionHandler;
import us.dot.its.jpo.conflictmonitor.monitor.algorithms.StreamsTopology;
import us.dot.its.jpo.conflictmonitor.monitor.algorithms.config.ConfigParameters;
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
import us.dot.its.jpo.conflictmonitor.monitor.mongo.ConfigInitializer;
import us.dot.its.jpo.conflictmonitor.monitor.mongo.ConnectSourceCreator;
import us.dot.its.jpo.conflictmonitor.monitor.topologies.BsmEventTopology;
import us.dot.its.jpo.conflictmonitor.monitor.topologies.ConfigTopology;
import us.dot.its.jpo.conflictmonitor.monitor.topologies.MessageIngestTopology;
import us.dot.its.jpo.geojsonconverter.pojos.geojson.map.ProcessedMap;
import us.dot.its.jpo.geojsonconverter.pojos.spat.ProcessedSpat;
import us.dot.its.jpo.ode.model.OdeBsmData;
import lombok.Getter;

/**
 * Launches ToGeoJsonFromJsonConverter service
 */
@Controller
@DependsOn("createKafkaTopics")
@Profile("!test")
public class MonitorServiceController {

    private static final Logger logger = LoggerFactory.getLogger(MonitorServiceController.class);
    org.apache.kafka.common.serialization.Serdes bas;

    // Temporary for KafkaStreams that don't implement the Algorithm interface
    @Getter
    final ConcurrentHashMap<String, KafkaStreams> streamsMap = new ConcurrentHashMap<String, KafkaStreams>();

    @Getter
    final ConcurrentHashMap<String, StreamsTopology> algoMap = new ConcurrentHashMap<String, StreamsTopology>();

   
    
    @Autowired
    public MonitorServiceController(final ConflictMonitorProperties conflictMonitorProps, 
        final KafkaTemplate<String, String> kafkaTemplate,
        final ConfigTopology configTopology, 
        final ConfigParameters configParameters,
        final ConfigInitializer configWriter,
        final ConnectSourceCreator connectSourceCreator) {
       
        String bsmStoreName = "BsmWindowStore";
        String spatStoreName = "SpatWindowStore";
        String mapStoreName = "ProcessedMapWindowStore";

        final String stateChangeTopic = conflictMonitorProps.getKafkaStateChangeEventTopic();
        final String healthTopic = conflictMonitorProps.getAppHealthNotificationTopic();

        try {
            logger.info("Starting {}", this.getClass().getSimpleName());
            
            // Configuration Topology
            // Start this first 
            final String config = "config";
            configTopology.setStreamsProperties(conflictMonitorProps.createStreamProperties(config));
            configTopology.setParameters(configParameters);
            configTopology.registerStateListener(new StateChangeHandler(kafkaTemplate, config, stateChangeTopic, healthTopic));
            configTopology.registerUncaughtExceptionHandler(new StreamsExceptionHandler(kafkaTemplate, config, healthTopic));
            algoMap.put(config, configTopology);
            Runtime.getRuntime().addShutdownHook(new Thread(configTopology::stop));
            configTopology.start();
            

            final String repartition = "repartition";
            final RepartitionAlgorithmFactory repartitionAlgoFactory = conflictMonitorProps.getRepartitionAlgorithmFactory();
            final String repAlgo = conflictMonitorProps.getRepartitionAlgorithm();
            final RepartitionAlgorithm repartitionAlgo = repartitionAlgoFactory.getAlgorithm(repAlgo);
            final RepartitionParameters repartitionParams = conflictMonitorProps.getRepartitionAlgorithmParameters();
            if (repartitionAlgo instanceof StreamsTopology) {     
                final var streamsAlgo = (StreamsTopology)repartitionAlgo;
                streamsAlgo.setStreamsProperties(conflictMonitorProps.createStreamProperties(repartition));
                streamsAlgo.registerStateListener(new StateChangeHandler(kafkaTemplate, repartition, stateChangeTopic, healthTopic));
                streamsAlgo.registerUncaughtExceptionHandler(new StreamsExceptionHandler(kafkaTemplate, repartition, healthTopic));
                algoMap.put(repartition, streamsAlgo);
            }
            repartitionAlgo.setParameters(repartitionParams);
            Runtime.getRuntime().addShutdownHook(new Thread(repartitionAlgo::stop));
            repartitionAlgo.start();
           

            // Map Broadcast Rate Topology
            // Sends "MAP Broadcast Rate" events when the number of MAPs per rolling period is too low or too high
            final String mapBroadcastRate = "mapBroadcastRate";
            final MapValidationAlgorithmFactory mapAlgoFactory = conflictMonitorProps.getMapValidationAlgorithmFactory();
            final String mapAlgo = conflictMonitorProps.getMapValidationAlgorithm();
            final MapValidationAlgorithm mapCountAlgo = mapAlgoFactory.getAlgorithm(mapAlgo);
            final MapValidationParameters mapCountParams = conflictMonitorProps.getMapValidationParameters();
            configTopology.registerConfigListeners(mapCountParams);
            logger.info("Map params {}", mapCountParams);
            if (mapCountAlgo instanceof StreamsTopology) {
                final var streamsAlgo = (StreamsTopology)mapCountAlgo;
                streamsAlgo.setStreamsProperties(conflictMonitorProps.createStreamProperties(mapBroadcastRate));
                streamsAlgo.registerStateListener(new StateChangeHandler(kafkaTemplate, mapBroadcastRate, stateChangeTopic, healthTopic));
                streamsAlgo.registerUncaughtExceptionHandler(new StreamsExceptionHandler(kafkaTemplate, mapBroadcastRate, healthTopic));
                algoMap.put(mapBroadcastRate, streamsAlgo);
            }
            mapCountAlgo.setParameters(mapCountParams);
            Runtime.getRuntime().addShutdownHook(new Thread(mapCountAlgo::stop));
            mapCountAlgo.start();
           

            
            // Spat Broadcast Rate Topology
            // Sends "SPAT Broadcast Rate" events when the number of SPATs per rolling period is too low or too high
            final String spatBroadcastRate = "spatBroadcastRate";
            final SpatValidationStreamsAlgorithmFactory spatAlgoFactory = conflictMonitorProps.getSpatValidationAlgorithmFactory();
            final String spatAlgo = conflictMonitorProps.getSpatValidationAlgorithm();
            final SpatValidationAlgorithm spatCountAlgo = spatAlgoFactory.getAlgorithm(spatAlgo);
            final SpatValidationParameters spatCountParams = conflictMonitorProps.getSpatValidationParameters();
            if (spatCountAlgo instanceof StreamsTopology) {
                final var streamsAlgo = (StreamsTopology)spatCountAlgo;
                streamsAlgo.setStreamsProperties(conflictMonitorProps.createStreamProperties(spatBroadcastRate));
                streamsAlgo.registerStateListener(new StateChangeHandler(kafkaTemplate, spatBroadcastRate, stateChangeTopic, healthTopic));
                streamsAlgo.registerUncaughtExceptionHandler(new StreamsExceptionHandler(kafkaTemplate, spatBroadcastRate, healthTopic));
                algoMap.put(spatBroadcastRate, streamsAlgo);
            }
            spatCountAlgo.setParameters(spatCountParams);
            Runtime.getRuntime().addShutdownHook(new Thread(spatCountAlgo::stop));
            spatCountAlgo.start();
            


            // Spat Time Change Details Assessment
            //Sends Time Change Details Events when the time deltas in spat messages are incorrect
            final String spatTimeChangeDetails = "spatTimeChangeDetails";
            final SpatTimeChangeDetailsAlgorithmFactory spatTCDAlgoFactory = conflictMonitorProps.getSpatTimeChangeDetailsAlgorithmFactory();
            final String spatTCDAlgo = conflictMonitorProps.getSpatTimeChangeDetailsAlgorithm();
            final SpatTimeChangeDetailsAlgorithm spatTimeChangeDetailsAlgo = spatTCDAlgoFactory.getAlgorithm(spatTCDAlgo);
            final SpatTimeChangeDetailsParameters spatTimeChangeDetailsParams = conflictMonitorProps.getSpatTimeChangeDetailsParameters();
            if (spatTimeChangeDetailsAlgo instanceof StreamsTopology) {
                final var streamsAlgo = (StreamsTopology)spatTimeChangeDetailsAlgo;
                streamsAlgo.setStreamsProperties(conflictMonitorProps.createStreamProperties(spatTimeChangeDetails));
                streamsAlgo.registerStateListener(new StateChangeHandler(kafkaTemplate, spatTimeChangeDetails, stateChangeTopic, healthTopic));
                streamsAlgo.registerUncaughtExceptionHandler(new StreamsExceptionHandler(kafkaTemplate, spatTimeChangeDetails, healthTopic));
                algoMap.put(spatTimeChangeDetails, streamsAlgo);
            }
            spatTimeChangeDetailsAlgo.setParameters(spatTimeChangeDetailsParams);
            Runtime.getRuntime().addShutdownHook(new Thread(spatTimeChangeDetailsAlgo::stop));
            spatTimeChangeDetailsAlgo.start();
            
            



            //Map Spat Alignment Topology
            final String mapSpatAlignment = "mapSpatAlignment";
            final MapSpatMessageAssessmentAlgorithmFactory mapSpatAlgoFactory = conflictMonitorProps.getMapSpatMessageAssessmentAlgorithmFactory();
            final String mapSpatAlgo = conflictMonitorProps.getMapSpatMessageAssessmentAlgorithm();
            final MapSpatMessageAssessmentAlgorithm mapSpatAlignmentAlgo = mapSpatAlgoFactory.getAlgorithm(mapSpatAlgo);
            final MapSpatMessageAssessmentParameters mapSpatAlignmentParams = conflictMonitorProps.getMapSpatMessageAssessmentParameters();
            if (mapSpatAlignmentAlgo instanceof StreamsTopology) {
                final var streamsAlgo = (StreamsTopology)mapSpatAlignmentAlgo;
                streamsAlgo.setStreamsProperties(conflictMonitorProps.createStreamProperties(mapSpatAlignment));
                streamsAlgo.registerStateListener(new StateChangeHandler(kafkaTemplate, mapSpatAlignment, stateChangeTopic, healthTopic));
                streamsAlgo.registerUncaughtExceptionHandler(new StreamsExceptionHandler(kafkaTemplate, mapSpatAlignment, healthTopic));
                algoMap.put(mapSpatAlignment, streamsAlgo);
            }
            mapSpatAlignmentAlgo.setParameters(mapSpatAlignmentParams);
            Runtime.getRuntime().addShutdownHook(new Thread(mapSpatAlignmentAlgo::stop));
            mapSpatAlignmentAlgo.start();



            //BSM Topology sends a message every time a vehicle drives through the intersection. 
            final String bsmEvent = "bsmEvent";
            final Topology topology = BsmEventTopology.build(conflictMonitorProps.getKafkaTopicOdeBsmJson(), conflictMonitorProps.getKafkaTopicCmBsmEvent());
            final KafkaStreams streams = new KafkaStreams(topology, conflictMonitorProps.createStreamProperties(bsmEvent));
            streams.setStateListener(new StateChangeHandler(kafkaTemplate, bsmEvent, stateChangeTopic, healthTopic));
            streams.setUncaughtExceptionHandler(new StreamsExceptionHandler(kafkaTemplate, bsmEvent, healthTopic));
            Runtime.getRuntime().addShutdownHook(new Thread(streams::close));
            streams.start(); 
            streamsMap.put(bsmEvent, streams);


            // // the message ingest topology tracks and stores incoming messages for further processing
            final String messageIngest = "messageIngest";
            final var messageIngestTopology = MessageIngestTopology.build(
                conflictMonitorProps.getKafkaTopicOdeBsmJson(),
                bsmStoreName,
                conflictMonitorProps.getKafkaTopicProcessedSpat(),
                spatStoreName,
                conflictMonitorProps.getKafkaTopicProcessedMap(),
                mapStoreName
            );
            
            final var messageIngestStreams = new KafkaStreams(messageIngestTopology, conflictMonitorProps.createStreamProperties(messageIngest));
            messageIngestStreams.setStateListener(new StateChangeHandler(kafkaTemplate, messageIngest, stateChangeTopic, healthTopic));
            messageIngestStreams.setUncaughtExceptionHandler(new StreamsExceptionHandler(kafkaTemplate, messageIngest, healthTopic));
            Runtime.getRuntime().addShutdownHook(new Thread(messageIngestStreams::close));
            messageIngestStreams.start();
            streamsMap.put(messageIngest, messageIngestStreams);

            
            Thread.sleep(20000);
            
            final ReadOnlyWindowStore<String, OdeBsmData> bsmWindowStore =
                messageIngestStreams.store(StoreQueryParameters.fromNameAndType(bsmStoreName, QueryableStoreTypes.windowStore()));

            final ReadOnlyWindowStore<String, ProcessedSpat> spatWindowStore =
                messageIngestStreams.store(StoreQueryParameters.fromNameAndType(spatStoreName, QueryableStoreTypes.windowStore()));

            final ReadOnlyKeyValueStore<String, ProcessedMap> mapKeyValueStore =
                messageIngestStreams.store(StoreQueryParameters.fromNameAndType(mapStoreName, QueryableStoreTypes.keyValueStore()));

            
            
            // Get Algorithms used by intersection event topology

            // Setup Lane Direction of Travel Factory
            final LaneDirectionOfTravelAlgorithmFactory ldotAlgoFactory = conflictMonitorProps.getLaneDirectionOfTravelAlgorithmFactory();
            final String ldotAlgo = conflictMonitorProps.getLaneDirectionOfTravelAlgorithm();
            final LaneDirectionOfTravelAlgorithm laneDirectionOfTravelAlgorithm = ldotAlgoFactory.getAlgorithm(ldotAlgo);
            final LaneDirectionOfTravelParameters ldotParams = conflictMonitorProps.getLaneDirectionOfTravelParameters();
            
            // Setup Connection of Travel Factory
            final ConnectionOfTravelAlgorithmFactory cotAlgoFactory = conflictMonitorProps.getConnectionOfTravelAlgorithmFactory();
            final String cotAlgo = conflictMonitorProps.getConnectionOfTravelAlgorithm();
            final ConnectionOfTravelAlgorithm connectionOfTravelAlgorithm = cotAlgoFactory.getAlgorithm(cotAlgo);
            final ConnectionOfTravelParameters cotParams = conflictMonitorProps.getConnectionOfTravelParameters();
            
            // Setup Signal State Vehicle Crosses Factory
            final SignalStateVehicleCrossesAlgorithmFactory ssvcAlgoFactory = conflictMonitorProps.getSignalStateVehicleCrossesAlgorithmFactory();
            final String ssvcAlgo = conflictMonitorProps.getSignalStateVehicleCrossesAlgorithm();
            final SignalStateVehicleCrossesAlgorithm signalStateVehicleCrossesAlgorithm = ssvcAlgoFactory.getAlgorithm(ssvcAlgo);
            final SignalStateVehicleCrossesParameters ssvcParams = conflictMonitorProps.getSignalStateVehicleCrossesParameters();
            
            // Setup Signal State Vehicle Stops Factory
            final SignalStateVehicleStopsAlgorithmFactory ssvsAlgoFactory = conflictMonitorProps.getSignalStateVehicleStopsAlgorithmFactory();
            final String ssvsAlgo = conflictMonitorProps.getSignalStateVehicleStopsAlgorithm();
            final SignalStateVehicleStopsAlgorithm signalStateVehicleStopsAlgorithm = ssvsAlgoFactory.getAlgorithm(ssvsAlgo);
            final SignalStateVehicleStopsParameters ssvsParams = conflictMonitorProps.getSignalStateVehicleStopsParameters();
            

            // The IntersectionEventTopology grabs snapshots of spat / map / bsm and processes data when a vehicle passes through
            final String intersectionEvent = "intersectionEvent";
            final IntersectionEventAlgorithmFactory intersectionAlgoFactory = conflictMonitorProps.getIntersectionEventAlgorithmFactory();
            final String intersectionAlgoKey = conflictMonitorProps.getIntersectionEventAlgorithm();
            final IntersectionEventAlgorithm intersectionAlgo = intersectionAlgoFactory.getAlgorithm(intersectionAlgoKey);
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
                final var streamsAlgo = (IntersectionEventStreamsAlgorithm)intersectionAlgo;
                streamsAlgo.setStreamsProperties(conflictMonitorProps.createStreamProperties(intersectionEvent));
                streamsAlgo.setBsmWindowStore(bsmWindowStore);
                streamsAlgo.setSpatWindowStore(spatWindowStore);
                streamsAlgo.setMapStore(mapKeyValueStore);
                streamsAlgo.registerStateListener(new StateChangeHandler(kafkaTemplate, intersectionEvent, stateChangeTopic, healthTopic));
                streamsAlgo.registerUncaughtExceptionHandler(new StreamsExceptionHandler(kafkaTemplate, intersectionEvent, healthTopic));
                algoMap.put(intersectionEvent, streamsAlgo);
            }
            Runtime.getRuntime().addShutdownHook(new Thread(intersectionAlgo::stop));
            intersectionAlgo.start();
            logger.info("Started intersectionEvent topology");


            


            // Signal State Event Assessment Topology
            final String signalStateEventAssessment = "signalStateEventAssessment";
            final SignalStateEventAssessmentAlgorithmFactory sseaAlgoFactory = conflictMonitorProps.getSignalStateEventAssessmentAlgorithmFactory();
            final String signalStateEventAssessmentAlgorithm = conflictMonitorProps.getSignalStateEventAssessmentAlgorithm();
            final SignalStateEventAssessmentAlgorithm signalStateEventAssesmentAlgo = sseaAlgoFactory.getAlgorithm(signalStateEventAssessmentAlgorithm);
            final SignalStateEventAssessmentParameters signalStateEventAssessmenAlgoParams = conflictMonitorProps.getSignalStateEventAssessmentAlgorithmParameters();
            if (signalStateEventAssesmentAlgo instanceof StreamsTopology) {
                final var streamsAlgo = (StreamsTopology)signalStateEventAssesmentAlgo;
                streamsAlgo.setStreamsProperties(conflictMonitorProps.createStreamProperties(signalStateEventAssessment));
                streamsAlgo.registerStateListener(new StateChangeHandler(kafkaTemplate, signalStateEventAssessment, stateChangeTopic, healthTopic));
                streamsAlgo.registerUncaughtExceptionHandler(new StreamsExceptionHandler(kafkaTemplate, signalStateEventAssessment, healthTopic));
                algoMap.put(signalStateEventAssessment, streamsAlgo);
            }
            signalStateEventAssesmentAlgo.setParameters(signalStateEventAssessmenAlgoParams);
            Runtime.getRuntime().addShutdownHook(new Thread(signalStateEventAssesmentAlgo::stop));
            signalStateEventAssesmentAlgo.start();
            

            // Lane Direction Of Travel Assessment Topology
            final String laneDirectionOfTravelAssessment = "laneDirectionOfTravelAssessment";
            final LaneDirectionOfTravelAssessmentAlgorithmFactory ldotaAlgoFactory = conflictMonitorProps.getLaneDirectionOfTravelAssessmentAlgorithmFactory();
            final String laneDirectionOfTravelAssessmentAlgorithm = conflictMonitorProps.getLaneDirectionOfTravelAssessmentAlgorithm();
            final LaneDirectionOfTravelAssessmentAlgorithm laneDirectionOfTravelAssesmentAlgo = ldotaAlgoFactory.getAlgorithm(laneDirectionOfTravelAssessmentAlgorithm);
            final LaneDirectionOfTravelAssessmentParameters laneDirectionOfTravelAssessmenAlgoParams = conflictMonitorProps.getLaneDirectionOfTravelAssessmentAlgorithmParameters();
            if (laneDirectionOfTravelAssesmentAlgo instanceof StreamsTopology) {
                final var streamsAlgo = (StreamsTopology)laneDirectionOfTravelAssesmentAlgo;
                streamsAlgo.setStreamsProperties(conflictMonitorProps.createStreamProperties(laneDirectionOfTravelAssessment));
                streamsAlgo.registerStateListener(new StateChangeHandler(kafkaTemplate, laneDirectionOfTravelAssessment, stateChangeTopic, healthTopic));
                streamsAlgo.registerUncaughtExceptionHandler(new StreamsExceptionHandler(kafkaTemplate, laneDirectionOfTravelAssessment, healthTopic));
                algoMap.put(laneDirectionOfTravelAssessment, streamsAlgo);
            }
            laneDirectionOfTravelAssesmentAlgo.setParameters(laneDirectionOfTravelAssessmenAlgoParams);
            Runtime.getRuntime().addShutdownHook(new Thread(laneDirectionOfTravelAssesmentAlgo::stop));
            laneDirectionOfTravelAssesmentAlgo.start();
            


            // Connection Of Travel Assessment Topology
            final String connectionOfTravelAssessment = "connectionOfTravelAssessment";
            final ConnectionOfTravelAssessmentAlgorithmFactory cotaAlgoFactory = conflictMonitorProps.getConnectionOfTravelAssessmentAlgorithmFactory();
            final String connectionOfTravelAssessmentAlgorithm = conflictMonitorProps.getMapSpatMessageAssessmentAlgorithm();
            final ConnectionOfTravelAssessmentAlgorithm connectionofTravelAssessmentAlgo = cotaAlgoFactory.getAlgorithm(connectionOfTravelAssessmentAlgorithm);
            final ConnectionOfTravelAssessmentParameters connectionOfTravelAssessmentAlgoParams = conflictMonitorProps.getConnectionOfTravelAssessmentAlgorithmParameters();
            if (connectionofTravelAssessmentAlgo instanceof StreamsTopology) {
                final var streamsAlgo = (StreamsTopology)connectionofTravelAssessmentAlgo;
                streamsAlgo.setStreamsProperties(conflictMonitorProps.createStreamProperties(connectionOfTravelAssessment));
                streamsAlgo.registerStateListener(new StateChangeHandler(kafkaTemplate, connectionOfTravelAssessment, stateChangeTopic, healthTopic));
                streamsAlgo.registerUncaughtExceptionHandler(new StreamsExceptionHandler(kafkaTemplate, connectionOfTravelAssessment, healthTopic));
                algoMap.put(connectionOfTravelAssessment, streamsAlgo);
            }
            connectionofTravelAssessmentAlgo.setParameters(connectionOfTravelAssessmentAlgoParams);
            Runtime.getRuntime().addShutdownHook(new Thread(connectionofTravelAssessmentAlgo::stop));
            connectionofTravelAssessmentAlgo.start();
            
            
            // Write initial configuration to MongoDB and create source connectors
            try {
                configWriter.createCollections();
                connectSourceCreator.createDefaultConfigConnector();
                configWriter.initializeDefaultConfigs();
                connectSourceCreator.createIntersectionConfigConnector();
                logger.info("Initialzed MongoDB configuration and source connectors.");
            } catch (Exception ex) {
                logger.error("Failed writing to MongoDB", ex);
            }
            
            // Restore properties
            configTopology.initializeProperties();
            logger.info("Initialized properties from MongoDB");

            
            logger.info("All services started!");
        } catch (Exception e) {
            logger.error("Encountered issue with creating topologies", e);
        }
    }

    
}
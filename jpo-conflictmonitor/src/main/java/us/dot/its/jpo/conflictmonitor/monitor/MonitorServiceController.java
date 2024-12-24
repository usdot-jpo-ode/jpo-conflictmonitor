package us.dot.its.jpo.conflictmonitor.monitor;

import lombok.Getter;
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
import us.dot.its.jpo.conflictmonitor.monitor.algorithms.bsm_event.BsmEventAlgorithm;
import us.dot.its.jpo.conflictmonitor.monitor.algorithms.bsm_event.BsmEventAlgorithmFactory;
import us.dot.its.jpo.conflictmonitor.monitor.algorithms.bsm_event.BsmEventParameters;
import us.dot.its.jpo.conflictmonitor.monitor.algorithms.config.ConfigParameters;
import us.dot.its.jpo.conflictmonitor.monitor.algorithms.connection_of_travel.ConnectionOfTravelAlgorithm;
import us.dot.its.jpo.conflictmonitor.monitor.algorithms.connection_of_travel.ConnectionOfTravelAlgorithmFactory;
import us.dot.its.jpo.conflictmonitor.monitor.algorithms.connection_of_travel.ConnectionOfTravelParameters;
import us.dot.its.jpo.conflictmonitor.monitor.algorithms.connection_of_travel_assessment.ConnectionOfTravelAssessmentAlgorithm;
import us.dot.its.jpo.conflictmonitor.monitor.algorithms.connection_of_travel_assessment.ConnectionOfTravelAssessmentAlgorithmFactory;
import us.dot.its.jpo.conflictmonitor.monitor.algorithms.connection_of_travel_assessment.ConnectionOfTravelAssessmentParameters;
import us.dot.its.jpo.conflictmonitor.monitor.algorithms.event.EventAlgorithm;
import us.dot.its.jpo.conflictmonitor.monitor.algorithms.event.EventAlgorithmFactory;
import us.dot.its.jpo.conflictmonitor.monitor.algorithms.event.EventParameters;
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
import us.dot.its.jpo.conflictmonitor.monitor.algorithms.message_ingest.MessageIngestAlgorithm;
import us.dot.its.jpo.conflictmonitor.monitor.algorithms.message_ingest.MessageIngestAlgorithmFactory;
import us.dot.its.jpo.conflictmonitor.monitor.algorithms.message_ingest.MessageIngestParameters;
import us.dot.its.jpo.conflictmonitor.monitor.algorithms.notification.NotificationAlgorithm;
import us.dot.its.jpo.conflictmonitor.monitor.algorithms.notification.NotificationAlgorithmFactory;
import us.dot.its.jpo.conflictmonitor.monitor.algorithms.notification.NotificationParameters;
import us.dot.its.jpo.conflictmonitor.monitor.algorithms.event_state_progression.EventStateProgressionAlgorithm;
import us.dot.its.jpo.conflictmonitor.monitor.algorithms.stop_line_passage.StopLinePassageAlgorithm;
import us.dot.its.jpo.conflictmonitor.monitor.algorithms.stop_line_passage.StopLinePassageAlgorithmFactory;
import us.dot.its.jpo.conflictmonitor.monitor.algorithms.stop_line_passage.StopLinePassageParameters;
import us.dot.its.jpo.conflictmonitor.monitor.algorithms.stop_line_passage_assessment.StopLinePassageAssessmentAlgorithm;
import us.dot.its.jpo.conflictmonitor.monitor.algorithms.stop_line_passage_assessment.StopLinePassageAssessmentAlgorithmFactory;
import us.dot.its.jpo.conflictmonitor.monitor.algorithms.stop_line_passage_assessment.StopLinePassageAssessmentParameters;
import us.dot.its.jpo.conflictmonitor.monitor.algorithms.stop_line_stop.StopLineStopAlgorithm;
import us.dot.its.jpo.conflictmonitor.monitor.algorithms.stop_line_stop.StopLineStopAlgorithmFactory;
import us.dot.its.jpo.conflictmonitor.monitor.algorithms.stop_line_stop.StopLineStopParameters;
import us.dot.its.jpo.conflictmonitor.monitor.algorithms.stop_line_stop_assessment.StopLineStopAssessmentAlgorithm;
import us.dot.its.jpo.conflictmonitor.monitor.algorithms.stop_line_stop_assessment.StopLineStopAssessmentAlgorithmFactory;
import us.dot.its.jpo.conflictmonitor.monitor.algorithms.stop_line_stop_assessment.StopLineStopAssessmentParameters;
import us.dot.its.jpo.conflictmonitor.monitor.algorithms.time_change_details.spat.SpatTimeChangeDetailsAlgorithm;
import us.dot.its.jpo.conflictmonitor.monitor.algorithms.time_change_details.spat.SpatTimeChangeDetailsAlgorithmFactory;
import us.dot.its.jpo.conflictmonitor.monitor.algorithms.time_change_details.spat.SpatTimeChangeDetailsParameters;
import us.dot.its.jpo.conflictmonitor.monitor.algorithms.map_message_count_progression.MapMessageCountProgressionAlgorithm;
import us.dot.its.jpo.conflictmonitor.monitor.algorithms.map_message_count_progression.MapMessageCountProgressionAlgorithmFactory;
import us.dot.its.jpo.conflictmonitor.monitor.algorithms.map_message_count_progression.MapMessageCountProgressionParameters;
import us.dot.its.jpo.conflictmonitor.monitor.algorithms.spat_message_count_progression.SpatMessageCountProgressionAlgorithm;
import us.dot.its.jpo.conflictmonitor.monitor.algorithms.spat_message_count_progression.SpatMessageCountProgressionAlgorithmFactory;
import us.dot.its.jpo.conflictmonitor.monitor.algorithms.spat_message_count_progression.SpatMessageCountProgressionParameters;
import us.dot.its.jpo.conflictmonitor.monitor.algorithms.bsm_message_count_progression.BsmMessageCountProgressionAlgorithm;
import us.dot.its.jpo.conflictmonitor.monitor.algorithms.bsm_message_count_progression.BsmMessageCountProgressionAlgorithmFactory;
import us.dot.its.jpo.conflictmonitor.monitor.algorithms.bsm_message_count_progression.BsmMessageCountProgressionParameters;
import us.dot.its.jpo.conflictmonitor.monitor.algorithms.timestamp_delta.map.MapTimestampDeltaAlgorithm;
import us.dot.its.jpo.conflictmonitor.monitor.algorithms.timestamp_delta.spat.SpatTimestampDeltaAlgorithm;
import us.dot.its.jpo.conflictmonitor.monitor.algorithms.validation.map.MapValidationAlgorithm;
import us.dot.its.jpo.conflictmonitor.monitor.algorithms.validation.map.MapValidationAlgorithmFactory;
import us.dot.its.jpo.conflictmonitor.monitor.algorithms.validation.map.MapValidationParameters;
import us.dot.its.jpo.conflictmonitor.monitor.algorithms.validation.spat.SpatValidationAlgorithm;
import us.dot.its.jpo.conflictmonitor.monitor.algorithms.validation.spat.SpatValidationParameters;
import us.dot.its.jpo.conflictmonitor.monitor.algorithms.validation.spat.SpatValidationStreamsAlgorithmFactory;
import us.dot.its.jpo.conflictmonitor.monitor.models.map.MapIndex;
import us.dot.its.jpo.conflictmonitor.monitor.topologies.config.ConfigInitializer;
import us.dot.its.jpo.conflictmonitor.monitor.topologies.config.ConfigTopology;

import java.util.concurrent.ConcurrentHashMap;

/**
 * Launches ToGeoJsonFromJsonConverter service
 */
@Controller
@DependsOn("createKafkaTopics")
@Profile("!test && !testConfig")
public class MonitorServiceController {

    private static final Logger logger = LoggerFactory.getLogger(MonitorServiceController.class);
    org.apache.kafka.common.serialization.Serdes bas;

    @Getter
    final ConcurrentHashMap<String, StreamsTopology> algoMap = new ConcurrentHashMap<String, StreamsTopology>();

    
    @Autowired
    public MonitorServiceController(final ConflictMonitorProperties conflictMonitorProps, 
            final KafkaTemplate<String, String> kafkaTemplate,
            final ConfigTopology configTopology,
            final ConfigParameters configParameters,
            final ConfigInitializer configInitializer,
            final MapIndex mapIndex) {
       


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
            configTopology.setKafkaTemplate(kafkaTemplate);
            configTopology.start();


            final String notification = "notification";
            final NotificationAlgorithmFactory notificationAlgoFactory = conflictMonitorProps.getNotificationAlgorithmFactory();
            final String notAlgo = conflictMonitorProps.getNotificationAlgorithm();
            final NotificationAlgorithm notificationAlgo = notificationAlgoFactory.getAlgorithm(notAlgo);
            final NotificationParameters notificationParams = conflictMonitorProps.getNotificationAlgorithmParameters();
            configTopology.registerConfigListeners(notificationParams);
            if (notificationAlgo instanceof StreamsTopology) {     
                final var streamsAlgo = (StreamsTopology)notificationAlgo;
                streamsAlgo.setStreamsProperties(conflictMonitorProps.createStreamProperties(notification));
                streamsAlgo.registerStateListener(new StateChangeHandler(kafkaTemplate, notification, stateChangeTopic, healthTopic));
                streamsAlgo.registerUncaughtExceptionHandler(new StreamsExceptionHandler(kafkaTemplate, notification, healthTopic));
                algoMap.put(notification, streamsAlgo);
            }
            notificationAlgo.setParameters(notificationParams);
            Runtime.getRuntime().addShutdownHook(new Thread(notificationAlgo::stop));
            notificationAlgo.start();
           

            // Map Validation Topology
            final String mapValidation = "mapValidation";
            final MapValidationAlgorithmFactory mapAlgoFactory = conflictMonitorProps.getMapValidationAlgorithmFactory();
            final String mapAlgo = conflictMonitorProps.getMapValidationAlgorithm();
            final MapValidationAlgorithm mapValidationAlgo = mapAlgoFactory.getAlgorithm(mapAlgo);
            final MapValidationParameters mapValidationParams = conflictMonitorProps.getMapValidationParameters();
            configTopology.registerConfigListeners(mapValidationParams);
            logger.info("Map params {}", mapValidationParams);
            if (mapValidationAlgo instanceof StreamsTopology streamsAlgo) {
                streamsAlgo.setStreamsProperties(conflictMonitorProps.createStreamProperties(mapValidation));
                streamsAlgo.registerStateListener(new StateChangeHandler(kafkaTemplate, mapValidation, stateChangeTopic, healthTopic));
                streamsAlgo.registerUncaughtExceptionHandler(new StreamsExceptionHandler(kafkaTemplate, mapValidation, healthTopic));
                algoMap.put(mapValidation, streamsAlgo);
            }
            mapValidationAlgo.setParameters(mapValidationParams);
            // Plugin timestamp delta algorithm
            final MapTimestampDeltaAlgorithm mapTimestampAlgo = getMapTimestampDeltaAlgorithm(conflictMonitorProps);
            mapValidationAlgo.setTimestampDeltaAlgorithm(mapTimestampAlgo);
            Runtime.getRuntime().addShutdownHook(new Thread(mapValidationAlgo::stop));
            mapValidationAlgo.start();
           

            
            // Spat Validation Topology
            final String spatValidation = "spatValidation";
            final SpatValidationStreamsAlgorithmFactory spatAlgoFactory = conflictMonitorProps.getSpatValidationAlgorithmFactory();
            final String spatAlgo = conflictMonitorProps.getSpatValidationAlgorithm();
            final SpatValidationAlgorithm spatValidationAlgo = spatAlgoFactory.getAlgorithm(spatAlgo);
            final SpatValidationParameters spatValidationParams = conflictMonitorProps.getSpatValidationParameters();
            configTopology.registerConfigListeners(spatValidationParams);
            if (spatValidationAlgo instanceof StreamsTopology streamsAlgo) {
                streamsAlgo.setStreamsProperties(conflictMonitorProps.createStreamProperties(spatValidation));
                streamsAlgo.registerStateListener(new StateChangeHandler(kafkaTemplate, spatValidation, stateChangeTopic, healthTopic));
                streamsAlgo.registerUncaughtExceptionHandler(new StreamsExceptionHandler(kafkaTemplate, spatValidation, healthTopic));
                algoMap.put(spatValidation, streamsAlgo);
            }
            spatValidationAlgo.setParameters(spatValidationParams);
            // Plugin timestamp delta algorithm
            final SpatTimestampDeltaAlgorithm spatTimestampAlgo = getSpatTimestampDeltaAlgorithm(conflictMonitorProps);
            spatValidationAlgo.setTimestampDeltaAlgorithm(spatTimestampAlgo);
            Runtime.getRuntime().addShutdownHook(new Thread(spatValidationAlgo::stop));
            spatValidationAlgo.start();
            


            // Spat Time Change Details Assessment
            //Sends Time Change Details Events when the time deltas in spat messages are incorrect
            final String spatTimeChangeDetails = "spatTimeChangeDetails";
            final SpatTimeChangeDetailsAlgorithmFactory spatTCDAlgoFactory = conflictMonitorProps.getSpatTimeChangeDetailsAlgorithmFactory();
            final String spatTCDAlgo = conflictMonitorProps.getSpatTimeChangeDetailsAlgorithm();
            final SpatTimeChangeDetailsAlgorithm spatTimeChangeDetailsAlgo = spatTCDAlgoFactory.getAlgorithm(spatTCDAlgo);
            final SpatTimeChangeDetailsParameters spatTimeChangeDetailsParams = conflictMonitorProps.getSpatTimeChangeDetailsParameters();
            configTopology.registerConfigListeners(spatTimeChangeDetailsParams);
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
            configTopology.registerConfigListeners(mapSpatAlignmentParams);
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
            final BsmEventParameters bsmEventParams = conflictMonitorProps.getBsmEventParameters();
            configTopology.registerConfigListeners(bsmEventParams);
            final String bsmEventAlgorithmName = bsmEventParams.getAlgorithm();
            final BsmEventAlgorithmFactory bsmEventAlgorithmFactory = conflictMonitorProps.getBsmEventAlgorithmFactory();
            final BsmEventAlgorithm bsmEventAlgorithm = bsmEventAlgorithmFactory.getAlgorithm(bsmEventAlgorithmName);
            bsmEventAlgorithm.setMapIndex(mapIndex);
            if (bsmEventAlgorithm instanceof StreamsTopology) {
                final var streamsAlgo = (StreamsTopology)bsmEventAlgorithm;
                streamsAlgo.setStreamsProperties(conflictMonitorProps.createStreamProperties(bsmEvent));
                streamsAlgo.registerStateListener(new StateChangeHandler(kafkaTemplate, bsmEvent, stateChangeTopic, healthTopic));
                streamsAlgo.registerUncaughtExceptionHandler(new StreamsExceptionHandler(kafkaTemplate, bsmEvent, healthTopic));
                algoMap.put(bsmEvent, streamsAlgo);
            }
            bsmEventAlgorithm.setParameters(bsmEventParams);
            Runtime.getRuntime().addShutdownHook(new Thread(bsmEventAlgorithm::stop));
            bsmEventAlgorithm.start();


            // The message ingest topology tracks and stores incoming messages for further processing
            // It is a sub-topology of the IntersectionEvent Topology
            final MessageIngestParameters messageIngestParams = conflictMonitorProps.getMessageIngestParameters();
            configTopology.registerConfigListeners(messageIngestParams);
            final String messageIngestAlgorithmName = messageIngestParams.getAlgorithm();
            final MessageIngestAlgorithmFactory messageIngestAlgorithmFactory = conflictMonitorProps.getMessageIngestAlgorithmFactory();
            final MessageIngestAlgorithm messageIngestAlgorithm = messageIngestAlgorithmFactory.getAlgorithm(messageIngestAlgorithmName);
            messageIngestAlgorithm.setMapIndex(mapIndex);
            messageIngestAlgorithm.setParameters(messageIngestParams);
            // Plugin Spat Transition algorithm
            final EventStateProgressionAlgorithm spatTransitionAlgorithm = getSpatTransitionAlgorithm(conflictMonitorProps);
            messageIngestAlgorithm.setEventStateProgressionAlgorithm(spatTransitionAlgorithm);


            // Get Algorithms used by intersection event topology
            
            // Setup Lane Direction of Travel Factory
            final LaneDirectionOfTravelAlgorithmFactory ldotAlgoFactory = conflictMonitorProps.getLaneDirectionOfTravelAlgorithmFactory();
            final String ldotAlgo = conflictMonitorProps.getLaneDirectionOfTravelAlgorithm();
            final LaneDirectionOfTravelAlgorithm laneDirectionOfTravelAlgorithm = ldotAlgoFactory.getAlgorithm(ldotAlgo);
            final LaneDirectionOfTravelParameters ldotParams = conflictMonitorProps.getLaneDirectionOfTravelParameters();
            configTopology.registerConfigListeners(ldotParams);
            
            // Setup Connection of Travel Factory
            final ConnectionOfTravelAlgorithmFactory cotAlgoFactory = conflictMonitorProps.getConnectionOfTravelAlgorithmFactory();
            final String cotAlgo = conflictMonitorProps.getConnectionOfTravelAlgorithm();
            final ConnectionOfTravelAlgorithm connectionOfTravelAlgorithm = cotAlgoFactory.getAlgorithm(cotAlgo);
            final ConnectionOfTravelParameters cotParams = conflictMonitorProps.getConnectionOfTravelParameters();
            configTopology.registerConfigListeners(cotParams);
            
            // Setup Signal State Vehicle Crosses Factory
            final StopLinePassageAlgorithmFactory ssvcAlgoFactory = conflictMonitorProps.getSignalStateVehicleCrossesAlgorithmFactory();
            final String ssvcAlgo = conflictMonitorProps.getSignalStateVehicleCrossesAlgorithm();
            final StopLinePassageAlgorithm signalStateVehicleCrossesAlgorithm = ssvcAlgoFactory.getAlgorithm(ssvcAlgo);
            final StopLinePassageParameters ssvcParams = conflictMonitorProps.getSignalStateVehicleCrossesParameters();
            configTopology.registerConfigListeners(ssvcParams);
            
            // Setup Signal State Vehicle Stops Factory
            final StopLineStopAlgorithmFactory ssvsAlgoFactory = conflictMonitorProps.getSignalStateVehicleStopsAlgorithmFactory();
            final String ssvsAlgo = conflictMonitorProps.getSignalStateVehicleStopsAlgorithm();
            final StopLineStopAlgorithm signalStateVehicleStopsAlgorithm = ssvsAlgoFactory.getAlgorithm(ssvsAlgo);
            final StopLineStopParameters ssvsParams = conflictMonitorProps.getSignalStateVehicleStopsParameters();
            configTopology.registerConfigListeners(ssvsParams);

            // The IntersectionEventTopology grabs snapshots of spat / map / bsm and processes data when a vehicle passes through
            final String intersectionEvent = "intersectionEvent";
            final IntersectionEventAlgorithmFactory intersectionAlgoFactory = conflictMonitorProps.getIntersectionEventAlgorithmFactory();
            final String intersectionAlgoKey = conflictMonitorProps.getIntersectionEventAlgorithm();
            final IntersectionEventAlgorithm intersectionAlgo = intersectionAlgoFactory.getAlgorithm(intersectionAlgoKey);
            intersectionAlgo.setConflictMonitorProperties(conflictMonitorProps);
            intersectionAlgo.setMessageIngestAlgorithm(messageIngestAlgorithm);
            intersectionAlgo.setLaneDirectionOfTravelAlgorithm(laneDirectionOfTravelAlgorithm);
            intersectionAlgo.setLaneDirectionOfTravelParams(ldotParams);
            intersectionAlgo.setConnectionOfTravelAlgorithm(connectionOfTravelAlgorithm);
            intersectionAlgo.setConnectionOfTravelParams(cotParams);
            intersectionAlgo.setSignalStateVehicleCrossesAlgorithm(signalStateVehicleCrossesAlgorithm);
            intersectionAlgo.setStopLinePassageParameters(ssvcParams);
            intersectionAlgo.setSignalStateVehicleStopsAlgorithm(signalStateVehicleStopsAlgorithm);
            intersectionAlgo.setStopLineStopParameters(ssvsParams);
            if (intersectionAlgo instanceof IntersectionEventStreamsAlgorithm) {
                final var streamsAlgo = (IntersectionEventStreamsAlgorithm)intersectionAlgo;
                streamsAlgo.setStreamsProperties(conflictMonitorProps.createStreamProperties(intersectionEvent));
                streamsAlgo.registerStateListener(new StateChangeHandler(kafkaTemplate, intersectionEvent, stateChangeTopic, healthTopic));
                streamsAlgo.registerUncaughtExceptionHandler(new StreamsExceptionHandler(kafkaTemplate, intersectionEvent, healthTopic));
                algoMap.put(intersectionEvent, streamsAlgo);
            }
            Runtime.getRuntime().addShutdownHook(new Thread(intersectionAlgo::stop));
            intersectionAlgo.start();
            logger.info("Started intersectionEvent topology");



            // Signal State Event Assessment Topology
            final String signalStateEventAssessment = "signalStateEventAssessment";
            final StopLinePassageAssessmentAlgorithmFactory sseaAlgoFactory = conflictMonitorProps.getSignalStateEventAssessmentAlgorithmFactory();
            final String signalStateEventAssessmentAlgorithm = conflictMonitorProps.getSignalStateEventAssessmentAlgorithm();
            final StopLinePassageAssessmentAlgorithm signalStateEventAssesmentAlgo = sseaAlgoFactory.getAlgorithm(signalStateEventAssessmentAlgorithm);
            final StopLinePassageAssessmentParameters signalStateEventAssessmenAlgoParams = conflictMonitorProps.getSignalStateEventAssessmentAlgorithmParameters();
            configTopology.registerConfigListeners(signalStateEventAssessmenAlgoParams);
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

            // // Stop Line Stop Assessment Topology
            final String stopLineStopAssessment = "stopLineStopAssessment";
            final StopLineStopAssessmentAlgorithmFactory slsaAlgoFactory = conflictMonitorProps.getStopLineStopAssessmentAlgorithmFactory();
            final String stopLineStopAssessmentAlgorithm = conflictMonitorProps.getStopLineStopAssessmentAlgorithm();
            final StopLineStopAssessmentAlgorithm stopLineStopAssesmentAlgo = slsaAlgoFactory.getAlgorithm(stopLineStopAssessmentAlgorithm);
            final StopLineStopAssessmentParameters stopLineStopAssessmenAlgoParams = conflictMonitorProps.getStopLineStopAssessmentAlgorithmParameters();
            if (stopLineStopAssesmentAlgo instanceof StreamsTopology) {
                final var streamsAlgo = (StreamsTopology)stopLineStopAssesmentAlgo;
                streamsAlgo.setStreamsProperties(conflictMonitorProps.createStreamProperties(stopLineStopAssessment));
                streamsAlgo.registerStateListener(new StateChangeHandler(kafkaTemplate, stopLineStopAssessment, stateChangeTopic, healthTopic));
                streamsAlgo.registerUncaughtExceptionHandler(new StreamsExceptionHandler(kafkaTemplate, stopLineStopAssessment, healthTopic));
                algoMap.put(stopLineStopAssessment, streamsAlgo);
            }
            stopLineStopAssesmentAlgo.setParameters(stopLineStopAssessmenAlgoParams);
            Runtime.getRuntime().addShutdownHook(new Thread(stopLineStopAssesmentAlgo::stop));
            stopLineStopAssesmentAlgo.start();
            

            // Lane Direction Of Travel Assessment Topology
            final String laneDirectionOfTravelAssessment = "laneDirectionOfTravelAssessment";
            final LaneDirectionOfTravelAssessmentAlgorithmFactory ldotaAlgoFactory = conflictMonitorProps.getLaneDirectionOfTravelAssessmentAlgorithmFactory();
            final String laneDirectionOfTravelAssessmentAlgorithm = conflictMonitorProps.getLaneDirectionOfTravelAssessmentAlgorithm();
            final LaneDirectionOfTravelAssessmentAlgorithm laneDirectionOfTravelAssesmentAlgo = ldotaAlgoFactory.getAlgorithm(laneDirectionOfTravelAssessmentAlgorithm);
            final LaneDirectionOfTravelAssessmentParameters laneDirectionOfTravelAssessmenAlgoParams = conflictMonitorProps.getLaneDirectionOfTravelAssessmentAlgorithmParameters();
            configTopology.registerConfigListeners(laneDirectionOfTravelAssessmenAlgoParams);
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
            final String connectionOfTravelAssessmentAlgorithm = conflictMonitorProps.getConnectionOfTravelAssessmentAlgorithm();
            final ConnectionOfTravelAssessmentAlgorithm connectionofTravelAssessmentAlgo = cotaAlgoFactory.getAlgorithm(connectionOfTravelAssessmentAlgorithm);
            final ConnectionOfTravelAssessmentParameters connectionOfTravelAssessmentAlgoParams = conflictMonitorProps.getConnectionOfTravelAssessmentAlgorithmParameters();
            configTopology.registerConfigListeners(connectionOfTravelAssessmentAlgoParams);
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


            //Map Message Count Progression Topology
            final String mapMessageCountProgression = "mapMessageCountProgression";
            final MapMessageCountProgressionAlgorithmFactory mapMessageCountProgressionAlgoFactory = conflictMonitorProps.getMapMessageCountProgressionAlgorithmFactory();
            final String mapMessageCountProgressionAlgorithm = conflictMonitorProps.getMapMessageCountProgressionAlgorithm();
            final MapMessageCountProgressionAlgorithm mapMessageCountProgressionAlgo = mapMessageCountProgressionAlgoFactory.getAlgorithm(mapMessageCountProgressionAlgorithm);
            final MapMessageCountProgressionParameters mapMessageCountProgressionParams = conflictMonitorProps.getMapMessageCountProgressionAlgorithmParameters();
            configTopology.registerConfigListeners(mapMessageCountProgressionParams);
            if (mapMessageCountProgressionAlgo instanceof StreamsTopology) {     
                final var streamsAlgo = (StreamsTopology)mapMessageCountProgressionAlgo;
                streamsAlgo.setStreamsProperties(conflictMonitorProps.createStreamProperties(mapMessageCountProgression));
                streamsAlgo.registerStateListener(new StateChangeHandler(kafkaTemplate, mapMessageCountProgression, stateChangeTopic, healthTopic));
                streamsAlgo.registerUncaughtExceptionHandler(new StreamsExceptionHandler(kafkaTemplate, mapMessageCountProgression, healthTopic));
                algoMap.put(mapMessageCountProgression, streamsAlgo);
            }
            mapMessageCountProgressionAlgo.setParameters(mapMessageCountProgressionParams);
            Runtime.getRuntime().addShutdownHook(new Thread(mapMessageCountProgressionAlgo::stop));
            mapMessageCountProgressionAlgo.start();

            //Spat Message Count Progression Topology
            final String spatMessageCountProgression = "spatMessageCountProgression";
            final SpatMessageCountProgressionAlgorithmFactory spatMessageCountProgressionAlgoFactory = conflictMonitorProps.getSpatMessageCountProgressionAlgorithmFactory();
            final String spatMessageCountProgressionAlgorithm = conflictMonitorProps.getSpatMessageCountProgressionAlgorithm();
            final SpatMessageCountProgressionAlgorithm spatMessageCountProgressionAlgo = spatMessageCountProgressionAlgoFactory.getAlgorithm(spatMessageCountProgressionAlgorithm);
            final SpatMessageCountProgressionParameters spatMessageCountProgressionParams = conflictMonitorProps.getSpatMessageCountProgressionAlgorithmParameters();
            configTopology.registerConfigListeners(spatMessageCountProgressionParams);
            if (spatMessageCountProgressionAlgo instanceof StreamsTopology) {     
                final var streamsAlgo = (StreamsTopology)spatMessageCountProgressionAlgo;
                streamsAlgo.setStreamsProperties(conflictMonitorProps.createStreamProperties(spatMessageCountProgression));
                streamsAlgo.registerStateListener(new StateChangeHandler(kafkaTemplate, spatMessageCountProgression, stateChangeTopic, healthTopic));
                streamsAlgo.registerUncaughtExceptionHandler(new StreamsExceptionHandler(kafkaTemplate, spatMessageCountProgression, healthTopic));
                algoMap.put(spatMessageCountProgression, streamsAlgo);
            }
            spatMessageCountProgressionAlgo.setParameters(spatMessageCountProgressionParams);
            Runtime.getRuntime().addShutdownHook(new Thread(spatMessageCountProgressionAlgo::stop));
            spatMessageCountProgressionAlgo.start();
            
            //Bsm Message Count Progression Topology
            final String bsmMessageCountProgression = "bsmMessageCountProgression";
            final BsmMessageCountProgressionAlgorithmFactory bsmMessageCountProgressionAlgoFactory = conflictMonitorProps.getBsmMessageCountProgressionAlgorithmFactory();
            final String bsmMessageCountProgressionAlgorithm = conflictMonitorProps.getBsmMessageCountProgressionAlgorithm();
            final BsmMessageCountProgressionAlgorithm bsmMessageCountProgressionAlgo = bsmMessageCountProgressionAlgoFactory.getAlgorithm(bsmMessageCountProgressionAlgorithm);
            final BsmMessageCountProgressionParameters bsmMessageCountProgressionParams = conflictMonitorProps.getBsmMessageCountProgressionAlgorithmParameters();
            configTopology.registerConfigListeners(bsmMessageCountProgressionParams);
            if (bsmMessageCountProgressionAlgo instanceof StreamsTopology) {     
                final var streamsAlgo = (StreamsTopology)bsmMessageCountProgressionAlgo;
                streamsAlgo.setStreamsProperties(conflictMonitorProps.createStreamProperties(bsmMessageCountProgression));
                streamsAlgo.registerStateListener(new StateChangeHandler(kafkaTemplate, bsmMessageCountProgression, stateChangeTopic, healthTopic));
                streamsAlgo.registerUncaughtExceptionHandler(new StreamsExceptionHandler(kafkaTemplate, bsmMessageCountProgression, healthTopic));
                algoMap.put(bsmMessageCountProgression, streamsAlgo);
            }
            bsmMessageCountProgressionAlgo.setParameters(bsmMessageCountProgressionParams);
            Runtime.getRuntime().addShutdownHook(new Thread(bsmMessageCountProgressionAlgo::stop));
            bsmMessageCountProgressionAlgo.start();

            // Combined Event Topology
            final String event = "event";
            final EventAlgorithmFactory eventAlgorithmFactory = conflictMonitorProps.getEventAlgorithmFactory();
            final String eventAlgorithmName = conflictMonitorProps.getEventAlgorithm();
            final EventAlgorithm eventAlgorithm = eventAlgorithmFactory.getAlgorithm(eventAlgorithmName);
            final EventParameters eventParams = conflictMonitorProps.getEventParameters();
            configTopology.registerConfigListeners(eventParams);
            if (eventAlgorithm instanceof StreamsTopology streamsAlgo) {
                streamsAlgo.setStreamsProperties(conflictMonitorProps.createStreamProperties(event));
                streamsAlgo.registerStateListener(new StateChangeHandler(kafkaTemplate, event, stateChangeTopic, healthTopic));
                streamsAlgo.registerUncaughtExceptionHandler(new StreamsExceptionHandler(kafkaTemplate, event, healthTopic));
                algoMap.put(event, streamsAlgo);
            }
            eventAlgorithm.setParameters(eventParams);
            Runtime.getRuntime().addShutdownHook(new Thread(eventAlgorithm::stop));
            eventAlgorithm.start();

            // Restore properties
            configInitializer.initializeDefaultConfigs();
            configTopology.initializePropertiesAsync();
            logger.info("Started initializing configuration properties");

            
            logger.info("All services started!");
        } catch (Exception e) {
            logger.error("Encountered issue with creating topologies", e);
        }
    }



    private static MapTimestampDeltaAlgorithm getMapTimestampDeltaAlgorithm(ConflictMonitorProperties props) {
        final var factory = props.getMapTimestampDeltaAlgorithmFactory();
        final String algorithmName = props.getMapTimestampDeltaAlgorithm();
        final var algorithm = factory.getAlgorithm(algorithmName);
        final var parameters = props.getMapTimestampDeltaParameters();
        algorithm.setParameters(parameters);
        return algorithm;
    }

    private static SpatTimestampDeltaAlgorithm getSpatTimestampDeltaAlgorithm(ConflictMonitorProperties props) {
        final var factory = props.getSpatTimestampDeltaAlgorithmFactory();
        final String algorithmName = props.getSpatTimestampDeltaAlgorithm();
        final var algorithm = factory.getAlgorithm(algorithmName);
        final var parameters = props.getSpatTimestampDeltaParameters();
        algorithm.setParameters(parameters);
        return algorithm;
    }

    private static EventStateProgressionAlgorithm getSpatTransitionAlgorithm(ConflictMonitorProperties props) {
        final var factory = props.getSpatTransitionAlgorithmFactory();
        final String algorithmName = props.getSpatTransitionAlgorithm();
        final var algorithm = factory.getAlgorithm(algorithmName);
        final var parameters = props.getSpatTransitionParameters();
        algorithm.setParameters(parameters);
        return algorithm;
    }
}
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
import org.springframework.stereotype.Controller;

import us.dot.its.jpo.conflictmonitor.ConflictMonitorProperties;
import us.dot.its.jpo.conflictmonitor.monitor.algorithms.Algorithm;
import us.dot.its.jpo.conflictmonitor.monitor.algorithms.StreamsTopology;
import us.dot.its.jpo.conflictmonitor.monitor.algorithms.connection_of_travel.ConnectionOfTravelAlgorithm;
import us.dot.its.jpo.conflictmonitor.monitor.algorithms.connection_of_travel.ConnectionOfTravelAlgorithmFactory;
import us.dot.its.jpo.conflictmonitor.monitor.algorithms.connection_of_travel.ConnectionOfTravelParameters;
import us.dot.its.jpo.conflictmonitor.monitor.algorithms.connection_of_travel_assessment.ConnectionOfTravelAssessmentAlgorithm;
import us.dot.its.jpo.conflictmonitor.monitor.algorithms.connection_of_travel_assessment.ConnectionOfTravelAssessmentAlgorithmFactory;
import us.dot.its.jpo.conflictmonitor.monitor.algorithms.connection_of_travel_assessment.ConnectionOfTravelAssessmentParameters;
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
    final ConcurrentHashMap<String, Algorithm<?>> algoMap = new ConcurrentHashMap<String, Algorithm<?>>();
    
    @Autowired
    public MonitorServiceController(ConflictMonitorProperties conflictMonitorProps) {
        

       
        String bsmStoreName = "BsmWindowStore";
        String spatStoreName = "SpatWindowStore";
        String mapStoreName = "ProcessedMapWindowStore";

        try {
            logger.info("Starting {}", this.getClass().getSimpleName());

            final String repartition = "repartition";
            RepartitionAlgorithmFactory repartitionAlgoFactory = conflictMonitorProps.getRepartitionAlgorithmFactory();
            String repAlgo = conflictMonitorProps.getRepartitionAlgorithm();
            RepartitionAlgorithm repartitionAlgo = repartitionAlgoFactory.getAlgorithm(repAlgo);
            RepartitionParameters repartitionParams = conflictMonitorProps.getRepartitionAlgorithmParameters();
            if (repartitionAlgo instanceof StreamsTopology) {     
                ((StreamsTopology)repartitionAlgo).setStreamsProperties(conflictMonitorProps.createStreamProperties(repartition));
            }
            repartitionAlgo.setParameters(repartitionParams);
            Runtime.getRuntime().addShutdownHook(new Thread(repartitionAlgo::stop));
            repartitionAlgo.start();
            algoMap.put(repartition, repartitionAlgo);
            
            
           
           

            // Map Broadcast Rate Topology
            // Sends "MAP Broadcast Rate" events when the number of MAPs per rolling period is too low or too high
            final String mapBroadcastRate = "mapBroadcastRate";
            MapValidationAlgorithmFactory mapAlgoFactory = conflictMonitorProps.getMapValidationAlgorithmFactory();
            String mapAlgo = conflictMonitorProps.getMapValidationAlgorithm();
            MapValidationAlgorithm mapCountAlgo = mapAlgoFactory.getAlgorithm(mapAlgo);
            MapValidationParameters mapCountParams = conflictMonitorProps.getMapValidationParameters();
            logger.info("Map params {}", mapCountParams);
            if (mapCountAlgo instanceof StreamsTopology) {
                ((StreamsTopology)mapCountAlgo).setStreamsProperties(conflictMonitorProps.createStreamProperties(mapBroadcastRate));
            }
            mapCountAlgo.setParameters(mapCountParams);
            Runtime.getRuntime().addShutdownHook(new Thread(mapCountAlgo::stop));
            mapCountAlgo.start();
            algoMap.put(mapBroadcastRate, mapCountAlgo);

            
            // Spat Broadcast Rate Topology
            // Sends "SPAT Broadcast Rate" events when the number of SPATs per rolling period is too low or too high
            final String spatBroadcastRate = "spatBroadcastRate";
            SpatValidationStreamsAlgorithmFactory spatAlgoFactory = conflictMonitorProps.getSpatValidationAlgorithmFactory();
            String spatAlgo = conflictMonitorProps.getSpatValidationAlgorithm();
            SpatValidationAlgorithm spatCountAlgo = spatAlgoFactory.getAlgorithm(spatAlgo);
            SpatValidationParameters spatCountParams = conflictMonitorProps.getSpatValidationParameters();
            if (spatCountAlgo instanceof StreamsTopology) {
                ((StreamsTopology)spatCountAlgo).setStreamsProperties(conflictMonitorProps.createStreamProperties(spatBroadcastRate));
            }
            spatCountAlgo.setParameters(spatCountParams);
            Runtime.getRuntime().addShutdownHook(new Thread(spatCountAlgo::stop));
            spatCountAlgo.start();
            algoMap.put(spatBroadcastRate, spatCountAlgo);


            // Spat Time Change Details Assessment
            //Sends Time Change Details Events when the time deltas in spat messages are incorrect
            final String spatTimeChangeDetails = "spatTimeChangeDetails";
            SpatTimeChangeDetailsAlgorithmFactory spatTCDAlgoFactory = conflictMonitorProps.getSpatTimeChangeDetailsAlgorithmFactory();
            String spatTCDAlgo = conflictMonitorProps.getSpatTimeChangeDetailsAlgorithm();
            SpatTimeChangeDetailsAlgorithm spatTimeChangeDetailsAlgo = spatTCDAlgoFactory.getAlgorithm(spatTCDAlgo);
            SpatTimeChangeDetailsParameters spatTimeChangeDetailsParams = conflictMonitorProps.getSpatTimeChangeDetailsParameters();
            if (spatTimeChangeDetailsAlgo instanceof StreamsTopology) {
                ((StreamsTopology)spatTimeChangeDetailsAlgo).setStreamsProperties(conflictMonitorProps.createStreamProperties(spatTimeChangeDetails));
            }
            spatTimeChangeDetailsAlgo.setParameters(spatTimeChangeDetailsParams);
            Runtime.getRuntime().addShutdownHook(new Thread(spatTimeChangeDetailsAlgo::stop));
            spatTimeChangeDetailsAlgo.start();
            algoMap.put(spatTimeChangeDetails, spatTimeChangeDetailsAlgo);
            



            //Map Spat Alignment Topology
            final String mapSpatAlignment = "mapSpatAlignment";
            MapSpatMessageAssessmentAlgorithmFactory mapSpatAlgoFactory = conflictMonitorProps.getMapSpatMessageAssessmentAlgorithmFactory();
            String mapSpatAlgo = conflictMonitorProps.getMapSpatMessageAssessmentAlgorithm();
            MapSpatMessageAssessmentAlgorithm mapSpatAlignmentAlgo = mapSpatAlgoFactory.getAlgorithm(mapSpatAlgo);
            MapSpatMessageAssessmentParameters mapSpatAlignmentParams = conflictMonitorProps.getMapSpatMessageAssessmentParameters();
            if (mapSpatAlignmentAlgo instanceof StreamsTopology) {
                ((StreamsTopology)mapSpatAlignmentAlgo).setStreamsProperties(conflictMonitorProps.createStreamProperties(mapSpatAlignment));
            }
            mapSpatAlignmentAlgo.setParameters(mapSpatAlignmentParams);
            Runtime.getRuntime().addShutdownHook(new Thread(mapSpatAlignmentAlgo::stop));
            mapSpatAlignmentAlgo.start();
            algoMap.put(mapSpatAlignment, mapSpatAlignmentAlgo);


            





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

            // // Setup Lane Direction of Travel Factory
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
            var intersectionEventTopology = IntersectionEventTopology.build(
                conflictMonitorProps, 
                bsmWindowStore,
                spatWindowStore,
                mapKeyValueStore,
                laneDirectionOfTravelAlgorithm,
                ldotParams,
                connectionOfTravelAlgorithm,
                cotParams,
                signalStateVehicleCrossesAlgorithm,
                ssvcParams,
                signalStateVehicleStopsAlgorithm,
                ssvsParams
            );
            final String intersectionEvent = "intersectionEvent";
            var intersectionEventStreams = new KafkaStreams(intersectionEventTopology, conflictMonitorProps.createStreamProperties(intersectionEvent));
            Runtime.getRuntime().addShutdownHook(new Thread(intersectionEventStreams::close));
            intersectionEventStreams.start();
            streamsMap.put(intersectionEvent, intersectionEventStreams);


            


            // Signal State Event Assessment Topology
            final String signalStateEventAssessment = "signalStateEventAssessment";
            SignalStateEventAssessmentAlgorithmFactory sseaAlgoFactory = conflictMonitorProps.getSignalStateEventAssessmentAlgorithmFactory();
            String signalStateEventAssessmentAlgorithm = conflictMonitorProps.getSignalStateEventAssessmentAlgorithm();
            SignalStateEventAssessmentAlgorithm signalStateEventAssesmentAlgo = sseaAlgoFactory.getAlgorithm(signalStateEventAssessmentAlgorithm);
            SignalStateEventAssessmentParameters signalStateEventAssessmenAlgoParams = conflictMonitorProps.getSignalStateEventAssessmentAlgorithmParameters();
            if (signalStateEventAssesmentAlgo instanceof StreamsTopology) {
                ((StreamsTopology)signalStateEventAssesmentAlgo).setStreamsProperties(conflictMonitorProps.createStreamProperties(signalStateEventAssessment));
            }
            signalStateEventAssesmentAlgo.setParameters(signalStateEventAssessmenAlgoParams);
            Runtime.getRuntime().addShutdownHook(new Thread(signalStateEventAssesmentAlgo::stop));
            signalStateEventAssesmentAlgo.start();
            algoMap.put(signalStateEventAssessment, signalStateEventAssesmentAlgo);

            // Lane Direction Of Travel Assessment Topology
            final String laneDirectionOfTravelAssessment = "laneDirectionOfTravelAssessment";
            LaneDirectionOfTravelAssessmentAlgorithmFactory ldotaAlgoFactory = conflictMonitorProps.getLaneDirectionOfTravelAssessmentAlgorithmFactory();
            String laneDirectionOfTravelAssessmentAlgorithm = conflictMonitorProps.getLaneDirectionOfTravelAssessmentAlgorithm();
            LaneDirectionOfTravelAssessmentAlgorithm laneDirectionOfTravelAssesmentAlgo = ldotaAlgoFactory.getAlgorithm(laneDirectionOfTravelAssessmentAlgorithm);
            LaneDirectionOfTravelAssessmentParameters laneDirectionOfTravelAssessmenAlgoParams = conflictMonitorProps.getLaneDirectionOfTravelAssessmentAlgorithmParameters();
            if (laneDirectionOfTravelAssesmentAlgo instanceof StreamsTopology) {
                ((StreamsTopology)laneDirectionOfTravelAssesmentAlgo).setStreamsProperties(conflictMonitorProps.createStreamProperties(laneDirectionOfTravelAssessment));
            }
            laneDirectionOfTravelAssesmentAlgo.setParameters(laneDirectionOfTravelAssessmenAlgoParams);
            Runtime.getRuntime().addShutdownHook(new Thread(laneDirectionOfTravelAssesmentAlgo::stop));
            laneDirectionOfTravelAssesmentAlgo.start();
            algoMap.put(laneDirectionOfTravelAssessment, laneDirectionOfTravelAssesmentAlgo);


            // Connection Of Travel Assessment Topology
            final String connectionOfTravelAssessment = "connectionOfTravelAssessment";
            ConnectionOfTravelAssessmentAlgorithmFactory cotaAlgoFactory = conflictMonitorProps.getConnectionOfTravelAssessmentAlgorithmFactory();
            String connectionOfTravelAssessmentAlgorithm = conflictMonitorProps.getMapSpatMessageAssessmentAlgorithm();
            ConnectionOfTravelAssessmentAlgorithm connectionofTravelAssessmentAlgo = cotaAlgoFactory.getAlgorithm(connectionOfTravelAssessmentAlgorithm);
            ConnectionOfTravelAssessmentParameters connectionOfTravelAssessmentAlgoParams = conflictMonitorProps.getConnectionOfTravelAssessmentAlgorithmParameters();
            if (connectionofTravelAssessmentAlgo instanceof StreamsTopology) {
                ((StreamsTopology)connectionofTravelAssessmentAlgo).setStreamsProperties(conflictMonitorProps.createStreamProperties(connectionOfTravelAssessment));
            }
            connectionofTravelAssessmentAlgo.setParameters(connectionOfTravelAssessmentAlgoParams);
            Runtime.getRuntime().addShutdownHook(new Thread(connectionofTravelAssessmentAlgo::stop));
            connectionofTravelAssessmentAlgo.start();
            algoMap.put(connectionOfTravelAssessment, connectionofTravelAssessmentAlgo);
            

            // // the IntersectionEventTopology grabs snapshots of spat / map / bsm and processes data when a vehicle passes through
            // topology = IntersectionEventTopology.build(conflictMonitorProps, bsmWindowStore, spatWindowStore, mapKeyValueStore);
            // streams = new KafkaStreams(topology, conflictMonitorProps.createStreamProperties("intersectionEvent"));
            // Runtime.getRuntime().addShutdownHook(new Thread(streams::close));
            // streams.start();

            

            
            logger.info("All geoJSON conversion services started!");
        } catch (Exception e) {
            logger.error("Encountered issue with creating topologies", e);
        }
    }
}
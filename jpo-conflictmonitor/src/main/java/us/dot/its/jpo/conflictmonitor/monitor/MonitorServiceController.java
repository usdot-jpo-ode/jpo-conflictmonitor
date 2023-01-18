package us.dot.its.jpo.conflictmonitor.monitor;

import java.util.Map;

import org.apache.kafka.clients.admin.TopicDescription;
import org.apache.kafka.streams.KafkaStreams;
import org.apache.kafka.streams.StoreQueryParameters;
import org.apache.kafka.streams.Topology;
import org.apache.kafka.streams.state.KeyValueStore;
import org.apache.kafka.streams.state.QueryableStoreTypes;
import org.apache.kafka.streams.state.ReadOnlyKeyValueStore;
import org.apache.kafka.streams.state.ReadOnlyWindowStore;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.DependsOn;
import org.springframework.context.annotation.Profile;
import org.springframework.kafka.core.KafkaAdmin;
import org.springframework.stereotype.Controller;

import us.dot.its.jpo.conflictmonitor.ConflictMonitorProperties;
import us.dot.its.jpo.conflictmonitor.monitor.algorithms.broadcast_rate.map.MapBroadcastRateAlgorithm;
import us.dot.its.jpo.conflictmonitor.monitor.algorithms.broadcast_rate.map.MapBroadcastRateAlgorithmFactory;
import us.dot.its.jpo.conflictmonitor.monitor.algorithms.broadcast_rate.map.MapBroadcastRateParameters;
import us.dot.its.jpo.conflictmonitor.monitor.algorithms.broadcast_rate.map.MapBroadcastRateStreamsAlgorithm;
import us.dot.its.jpo.conflictmonitor.monitor.algorithms.broadcast_rate.spat.SpatBroadcastRateAlgorithm;
import us.dot.its.jpo.conflictmonitor.monitor.algorithms.broadcast_rate.spat.SpatBroadcastRateAlgorithmFactory;
import us.dot.its.jpo.conflictmonitor.monitor.algorithms.broadcast_rate.spat.SpatBroadcastRateParameters;
import us.dot.its.jpo.conflictmonitor.monitor.algorithms.broadcast_rate.spat.SpatBroadcastRateStreamsAlgorithm;
import us.dot.its.jpo.conflictmonitor.monitor.algorithms.connection_of_travel_assessment.ConnectionOfTravelAssessmentAlgorithm;
import us.dot.its.jpo.conflictmonitor.monitor.algorithms.connection_of_travel_assessment.ConnectionOfTravelAssessmentAlgorithmFactory;
import us.dot.its.jpo.conflictmonitor.monitor.algorithms.connection_of_travel_assessment.ConnectionOfTravelAssessmentParameters;
import us.dot.its.jpo.conflictmonitor.monitor.algorithms.connection_of_travel_assessment.ConnectionOfTravelAssessmentStreamsAlgorithm;
import us.dot.its.jpo.conflictmonitor.monitor.algorithms.lane_direction_of_travel_assessment.LaneDirectionOfTravelAssessmentAlgorithm;
import us.dot.its.jpo.conflictmonitor.monitor.algorithms.lane_direction_of_travel_assessment.LaneDirectionOfTravelAssessmentAlgorithmFactory;
import us.dot.its.jpo.conflictmonitor.monitor.algorithms.lane_direction_of_travel_assessment.LaneDirectionOfTravelAssessmentParameters;
import us.dot.its.jpo.conflictmonitor.monitor.algorithms.lane_direction_of_travel_assessment.LaneDirectionOfTravelAssessmentStreamsAlgorithm;
import us.dot.its.jpo.conflictmonitor.monitor.algorithms.map_spat_message_assessment.MapSpatMessageAssessmentAlgorithm;
import us.dot.its.jpo.conflictmonitor.monitor.algorithms.map_spat_message_assessment.MapSpatMessageAssessmentAlgorithmFactory;
import us.dot.its.jpo.conflictmonitor.monitor.algorithms.map_spat_message_assessment.MapSpatMessageAssessmentParameters;
import us.dot.its.jpo.conflictmonitor.monitor.algorithms.map_spat_message_assessment.MapSpatMessageAssessmentStreamsAlgorithm;
import us.dot.its.jpo.conflictmonitor.monitor.algorithms.repartition.RepartitionAlgorithm;
import us.dot.its.jpo.conflictmonitor.monitor.algorithms.repartition.RepartitionAlgorithmFactory;
import us.dot.its.jpo.conflictmonitor.monitor.algorithms.repartition.RepartitionParameters;
import us.dot.its.jpo.conflictmonitor.monitor.algorithms.repartition.RepartitionStreamsAlgorithm;
import us.dot.its.jpo.conflictmonitor.monitor.algorithms.signal_state_event_assessment.SignalStateEventAssessmentAlgorithm;
import us.dot.its.jpo.conflictmonitor.monitor.algorithms.signal_state_event_assessment.SignalStateEventAssessmentAlgorithmFactory;
import us.dot.its.jpo.conflictmonitor.monitor.algorithms.signal_state_event_assessment.SignalStateEventAssessmentParameters;
import us.dot.its.jpo.conflictmonitor.monitor.algorithms.signal_state_event_assessment.SignalStateEventAssessmentStreamsAlgorithm;
import us.dot.its.jpo.conflictmonitor.monitor.algorithms.time_change_details.spat.SpatTimeChangeDetailsAlgorithm;
import us.dot.its.jpo.conflictmonitor.monitor.algorithms.time_change_details.spat.SpatTimeChangeDetailsAlgorithmFactory;
import us.dot.its.jpo.conflictmonitor.monitor.algorithms.time_change_details.spat.SpatTimeChangeDetailsParameters;
import us.dot.its.jpo.conflictmonitor.monitor.algorithms.time_change_details.spat.SpatTimeChangeDetailsStreamsAlgorithm;
import us.dot.its.jpo.conflictmonitor.monitor.models.events.LaneDirectionOfTravelEvent;
import us.dot.its.jpo.conflictmonitor.monitor.topologies.BsmEventTopology;
import us.dot.its.jpo.conflictmonitor.monitor.topologies.MessageIngestTopology;
import us.dot.its.jpo.geojsonconverter.pojos.geojson.map.ProcessedMap;
import us.dot.its.jpo.geojsonconverter.pojos.spat.ProcessedSpat;
import us.dot.its.jpo.conflictmonitor.monitor.topologies.IntersectionEventTopology;
import us.dot.its.jpo.ode.model.OdeBsmData;

/**
 * Launches ToGeoJsonFromJsonConverter service
 */
@Controller
@DependsOn("createKafkaTopics")
public class MonitorServiceController {

    private static final Logger logger = LoggerFactory.getLogger(MonitorServiceController.class);
    org.apache.kafka.common.serialization.Serdes bas;

   
    

    
    @Autowired
    public MonitorServiceController(ConflictMonitorProperties conflictMonitorProps) {
        

       
        String bsmStoreName = "BsmWindowStore";
        String spatStoreName = "SpatWindowStore";
        String mapStoreName = "ProcessedMapWindowStore";

        try {
            logger.info("Starting {}", this.getClass().getSimpleName());

            RepartitionAlgorithmFactory repartitionAlgoFactory = conflictMonitorProps.getRepartitionAlgorithmFactory();
            String repAlgo = conflictMonitorProps.getRepartitionAlgorithm();
            RepartitionAlgorithm repartitionAlgo = repartitionAlgoFactory.getAlgorithm(repAlgo);
            RepartitionParameters repartitionParams = conflictMonitorProps.getRepartitionAlgorithmParameters();
            if (repartitionAlgo instanceof RepartitionAlgorithm) {
                ((RepartitionStreamsAlgorithm)repartitionAlgo).setStreamsProperties(conflictMonitorProps.createStreamProperties("repartition"));
            }
            repartitionAlgo.setParameters(repartitionParams);
            Runtime.getRuntime().addShutdownHook(new Thread(repartitionAlgo::stop));
            repartitionAlgo.start();
            
           
           

            // // Map Broadcast Rate Topology
            // // Sends "MAP Broadcast Rate" events when the number of MAPs per rolling period is too low or too high
            // MapBroadcastRateAlgorithmFactory mapAlgoFactory = conflictMonitorProps.getMapBroadcastRateAlgorithmFactory();
            // String mapAlgo = conflictMonitorProps.getMapBroadcastRateAlgorithm();
            // MapBroadcastRateAlgorithm mapCountAlgo = mapAlgoFactory.getAlgorithm(mapAlgo);
            // MapBroadcastRateParameters mapCountParams = conflictMonitorProps.getMapBroadcastRateParameters();
            // logger.info("Map params {}", mapCountParams);
            // if (mapCountAlgo instanceof MapBroadcastRateStreamsAlgorithm) {
            //     ((MapBroadcastRateStreamsAlgorithm)mapCountAlgo).setStreamsProperties(conflictMonitorProps.createStreamProperties("mapBroadcastRate"));
            // }
            // mapCountAlgo.setParameters(mapCountParams);
            // Runtime.getRuntime().addShutdownHook(new Thread(mapCountAlgo::stop));
            // mapCountAlgo.start();

            
            // // Spat Broadcast Rate Topology
            // // Sends "SPAT Broadcast Rate" events when the number of SPATs per rolling period is too low or too high
            // SpatBroadcastRateAlgorithmFactory spatAlgoFactory = conflictMonitorProps.getSpatBroadcastRateAlgorithmFactory();
            // String spatAlgo = conflictMonitorProps.getSpatBroadcastRateAlgorithm();
            // SpatBroadcastRateAlgorithm spatCountAlgo = spatAlgoFactory.getAlgorithm(spatAlgo);
            // SpatBroadcastRateParameters spatCountParams = conflictMonitorProps.getSpatBroadcastRateParameters();
            // if (spatCountAlgo instanceof SpatBroadcastRateStreamsAlgorithm) {
            //     ((SpatBroadcastRateStreamsAlgorithm)spatCountAlgo).setStreamsProperties(conflictMonitorProps.createStreamProperties("spatBroadcastRate"));
            // }
            // spatCountAlgo.setParameters(spatCountParams);
            // Runtime.getRuntime().addShutdownHook(new Thread(spatCountAlgo::stop));
            // spatCountAlgo.start();


            // Spat Time Change Details Assessment
            //Sends Time Change Details Events when the time deltas in spat messages are incorrect
            SpatTimeChangeDetailsAlgorithmFactory spatTCDAlgoFactory = conflictMonitorProps.getSpatTimeChangeDetailsAlgorithmFactory();
            String spatTCDAlgo = conflictMonitorProps.getSpatTimeChangeDetailsAlgorithm();
            SpatTimeChangeDetailsAlgorithm spatTimeChangeDetailsAlgo = spatTCDAlgoFactory.getAlgorithm(spatTCDAlgo);
            SpatTimeChangeDetailsParameters spatTimeChangeDetailsParams = conflictMonitorProps.getSpatTimeChangeDetailsParameters();
            if (spatTimeChangeDetailsAlgo instanceof SpatTimeChangeDetailsAlgorithm) {
                ((SpatTimeChangeDetailsStreamsAlgorithm)spatTimeChangeDetailsAlgo).setStreamsProperties(conflictMonitorProps.createStreamProperties("spatTimeChangeDetails"));
            }
            spatTimeChangeDetailsAlgo.setParameters(spatTimeChangeDetailsParams);
            Runtime.getRuntime().addShutdownHook(new Thread(spatTimeChangeDetailsAlgo::stop));
            spatTimeChangeDetailsAlgo.start();

            



            //Map Spat Alignment Topology
            MapSpatMessageAssessmentAlgorithmFactory mapSpatAlgoFactory = conflictMonitorProps.getMapSpatMessageAssessmentAlgorithmFactory();
            String mapSpatAlgo = conflictMonitorProps.getMapSpatMessageAssessmentAlgorithm();
            MapSpatMessageAssessmentAlgorithm mapSpatAlignmentAlgo = mapSpatAlgoFactory.getAlgorithm(mapSpatAlgo);
            MapSpatMessageAssessmentParameters mapSpatAlignmentParams = conflictMonitorProps.getMapSpatMessageAssessmentParameters();
            if (mapSpatAlignmentAlgo instanceof MapSpatMessageAssessmentStreamsAlgorithm) {
                ((MapSpatMessageAssessmentStreamsAlgorithm)mapSpatAlignmentAlgo).setStreamsProperties(conflictMonitorProps.createStreamProperties("mapSpatAlignment"));
            }
            mapSpatAlignmentAlgo.setParameters(mapSpatAlignmentParams);
            Runtime.getRuntime().addShutdownHook(new Thread(mapSpatAlignmentAlgo::stop));
            mapSpatAlignmentAlgo.start();


            





            //BSM Topology sends a message every time a vehicle drives through the intersection. 
            Topology topology = BsmEventTopology.build(conflictMonitorProps.getKafkaTopicOdeBsmJson(), conflictMonitorProps.getKafkaTopicCmBsmEvent());
            KafkaStreams streams = new KafkaStreams(topology, conflictMonitorProps.createStreamProperties("bsmEvent"));
            Runtime.getRuntime().addShutdownHook(new Thread(streams::close));
            streams.start(); 


            // // the message ingest topology tracks and stores incoming messages for further processing
            topology = MessageIngestTopology.build(
                conflictMonitorProps.getKafkaTopicOdeBsmJson(),
                bsmStoreName,
                conflictMonitorProps.getKafkaTopicProcessedSpat(),
                spatStoreName,
                conflictMonitorProps.getKafkaTopicProcessedMap(),
                mapStoreName
            );
            streams = new KafkaStreams(topology, conflictMonitorProps.createStreamProperties("messageIngest"));
            Runtime.getRuntime().addShutdownHook(new Thread(streams::close));
            streams.start();

            
            Thread.sleep(15000);
            
            ReadOnlyWindowStore<String, OdeBsmData> bsmWindowStore =
                streams.store(StoreQueryParameters.fromNameAndType(bsmStoreName, QueryableStoreTypes.windowStore()));

            ReadOnlyWindowStore<String, ProcessedSpat> spatWindowStore =
                streams.store(StoreQueryParameters.fromNameAndType(spatStoreName, QueryableStoreTypes.windowStore()));

            ReadOnlyKeyValueStore<String, ProcessedMap> mapKeyValueStore =
                streams.store(StoreQueryParameters.fromNameAndType(mapStoreName, QueryableStoreTypes.keyValueStore()));

            // //the IntersectionEventTopology grabs snapshots of spat / map / bsm and processes data when a vehicle passes through
            topology = IntersectionEventTopology.build(
                conflictMonitorProps, 
                bsmWindowStore,
                spatWindowStore,
                mapKeyValueStore
            );
            streams = new KafkaStreams(topology, conflictMonitorProps.createStreamProperties("intersectionEvent"));
            Runtime.getRuntime().addShutdownHook(new Thread(streams::close));
            streams.start();


            


            // Signal State Event Assessment Topology
            SignalStateEventAssessmentAlgorithmFactory sseaAlgoFactory = conflictMonitorProps.getSignalStateEventAssessmentAlgorithmFactory();
            String signalStateEventAssessmentAlgorithm = conflictMonitorProps.getSignalStateEventAssessmentAlgorithm();
            SignalStateEventAssessmentAlgorithm signalStateEventAssesmentAlgo = sseaAlgoFactory.getAlgorithm(signalStateEventAssessmentAlgorithm);
            SignalStateEventAssessmentParameters signalStateEventAssessmenAlgoParams = conflictMonitorProps.getSignalStateEventAssessmentAlgorithmParameters();
            if (signalStateEventAssesmentAlgo instanceof SignalStateEventAssessmentStreamsAlgorithm) {
                ((SignalStateEventAssessmentStreamsAlgorithm)signalStateEventAssesmentAlgo).setStreamsProperties(conflictMonitorProps.createStreamProperties("signalStateEventAssessment"));
            }
            signalStateEventAssesmentAlgo.setParameters(signalStateEventAssessmenAlgoParams);
            Runtime.getRuntime().addShutdownHook(new Thread(signalStateEventAssesmentAlgo::stop));
            signalStateEventAssesmentAlgo.start();

            // Lane Direction Of Travel Assessment Topology
            LaneDirectionOfTravelAssessmentAlgorithmFactory ldotaAlgoFactory = conflictMonitorProps.getLaneDirectionOfTravelAssessmentAlgorithmFactory();
            String laneDirectionOfTravelAssessmentAlgorithm = conflictMonitorProps.getLaneDirectionOfTravelAssessmentAlgorithm();
            LaneDirectionOfTravelAssessmentAlgorithm laneDirectionOfTravelAssesmentAlgo = ldotaAlgoFactory.getAlgorithm(laneDirectionOfTravelAssessmentAlgorithm);
            LaneDirectionOfTravelAssessmentParameters laneDirectionOfTravelAssessmenAlgoParams = conflictMonitorProps.getLaneDirectionOfTravelAssessmentAlgorithmParameters();
            if (laneDirectionOfTravelAssesmentAlgo instanceof LaneDirectionOfTravelAssessmentStreamsAlgorithm) {
                ((LaneDirectionOfTravelAssessmentStreamsAlgorithm)laneDirectionOfTravelAssesmentAlgo).setStreamsProperties(conflictMonitorProps.createStreamProperties("laneDirectionOfTravelAssessment"));
            }
            laneDirectionOfTravelAssesmentAlgo.setParameters(laneDirectionOfTravelAssessmenAlgoParams);
            Runtime.getRuntime().addShutdownHook(new Thread(laneDirectionOfTravelAssesmentAlgo::stop));
            laneDirectionOfTravelAssesmentAlgo.start();


            // Connection Of Travel Assessment Topology
            ConnectionOfTravelAssessmentAlgorithmFactory cotaAlgoFactory = conflictMonitorProps.getConnectionOfTravelAssessmentAlgorithmFactory();
            String connectionOfTravelAssessmentAlgorithm = conflictMonitorProps.getMapSpatMessageAssessmentAlgorithm();
            ConnectionOfTravelAssessmentAlgorithm connectionofTravelAssessmentAlgo = cotaAlgoFactory.getAlgorithm(connectionOfTravelAssessmentAlgorithm);
            ConnectionOfTravelAssessmentParameters connectionOfTravelAssessmenAlgoParams = conflictMonitorProps.getConnectionOfTravelAssessmentAlgorithmParameters();
            if (connectionofTravelAssessmentAlgo instanceof ConnectionOfTravelAssessmentStreamsAlgorithm) {
                ((ConnectionOfTravelAssessmentStreamsAlgorithm)connectionofTravelAssessmentAlgo).setStreamsProperties(conflictMonitorProps.createStreamProperties("connectionOfTravelAssessment"));
            }
            connectionofTravelAssessmentAlgo.setParameters(connectionOfTravelAssessmenAlgoParams);
            Runtime.getRuntime().addShutdownHook(new Thread(connectionofTravelAssessmentAlgo::stop));
            connectionofTravelAssessmentAlgo.start();
            

            //the IntersectionEventTopology grabs snapshots of spat / map / bsm and processes data when a vehicle passes through
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
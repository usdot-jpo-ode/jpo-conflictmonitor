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
import us.dot.its.jpo.conflictmonitor.monitor.topologies.BsmEventTopology;
import us.dot.its.jpo.conflictmonitor.monitor.topologies.MessageIngestTopology;
import us.dot.its.jpo.geojsonconverter.pojos.geojson.map.MapFeatureCollection;
import us.dot.its.jpo.geojsonconverter.pojos.spat.ProcessedSpat;
import us.dot.its.jpo.conflictmonitor.monitor.topologies.IntersectionEventTopology;
import us.dot.its.jpo.ode.model.OdeBsmData;

/**
 * Launches ToGeoJsonFromJsonConverter service
 */
@Controller
@DependsOn("createKafkaTopics")
@Profile("!test")    // Don't start in test profile
public class MonitorServiceController {

    private static final Logger logger = LoggerFactory.getLogger(MonitorServiceController.class);
    org.apache.kafka.common.serialization.Serdes bas;

   
    

    
    @Autowired
    public MonitorServiceController(ConflictMonitorProperties conflictMonitorProps) {
        

       
        String bsmStoreName = "BsmWindowStore";
        String spatStoreName = "SpatWindowStore";
        String mapStoreName = "MapWindowStore";

        try {
            logger.info("Starting {}", this.getClass().getSimpleName());
            
           
           

            // Map Broadcast Rate Topology
            // Sends "MAP Broadcast Rate" events when the number of MAPs per rolling period is too low or too high
            MapBroadcastRateAlgorithmFactory mapAlgoFactory = conflictMonitorProps.getMapBroadcastRateAlgorithmFactory();
            String mapAlgo = conflictMonitorProps.getMapBroadcastRateAlgorithm();
            MapBroadcastRateAlgorithm mapCountAlgo = mapAlgoFactory.getAlgorithm(mapAlgo);
            MapBroadcastRateParameters mapCountParams = conflictMonitorProps.getMapBroadcastRateParameters();
            logger.info("Map params {}", mapCountParams);
            if (mapCountAlgo instanceof MapBroadcastRateStreamsAlgorithm) {
                ((MapBroadcastRateStreamsAlgorithm)mapCountAlgo).setStreamsProperties(conflictMonitorProps.createStreamProperties("mapBroadcastRate"));
            }
            mapCountAlgo.setParameters(mapCountParams);
            Runtime.getRuntime().addShutdownHook(new Thread(mapCountAlgo::stop));
            mapCountAlgo.start();

            
            // Spat Broadcast Rate Topology
            // Sends "SPAT Broadcast Rate" events when the number of SPATs per rolling period is too low or too high
            SpatBroadcastRateAlgorithmFactory spatAlgoFactory = conflictMonitorProps.getSpatBroadcastRateAlgorithmFactory();
            String spatAlgo = conflictMonitorProps.getSpatBroadcastRateAlgorithm();
            SpatBroadcastRateAlgorithm spatCountAlgo = spatAlgoFactory.getAlgorithm(spatAlgo);
            SpatBroadcastRateParameters spatCountParams = conflictMonitorProps.getSpatBroadcastRateParameters();
            if (spatCountAlgo instanceof SpatBroadcastRateStreamsAlgorithm) {
                ((SpatBroadcastRateStreamsAlgorithm)spatCountAlgo).setStreamsProperties(conflictMonitorProps.createStreamProperties("spatBroadcastRate"));
            }
            spatCountAlgo.setParameters(spatCountParams);
            Runtime.getRuntime().addShutdownHook(new Thread(spatCountAlgo::stop));
            spatCountAlgo.start();




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
                conflictMonitorProps.getKafkaTopicMapGeoJson(),
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

            ReadOnlyKeyValueStore<String, MapFeatureCollection> mapKeyValueStore =
                streams.store(StoreQueryParameters.fromNameAndType(mapStoreName, QueryableStoreTypes.keyValueStore()));

            //the IntersectionEventTopology grabs snapshots of spat / map / bsm and processes data when a vehicle passes through
            topology = IntersectionEventTopology.build(
                conflictMonitorProps, 
                bsmWindowStore,
                spatWindowStore,
                mapKeyValueStore
            );
            streams = new KafkaStreams(topology, conflictMonitorProps.createStreamProperties("intersectionEvent"));
            Runtime.getRuntime().addShutdownHook(new Thread(streams::close));
            streams.start();

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
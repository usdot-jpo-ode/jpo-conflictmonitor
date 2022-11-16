package us.dot.its.jpo.conflictmonitor.monitor;

import org.apache.kafka.streams.KafkaStreams;
import org.apache.kafka.streams.Topology;
import org.apache.kafka.streams.state.QueryableStoreTypes;
import org.apache.kafka.streams.state.ReadOnlyWindowStore;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;

import us.dot.its.jpo.conflictmonitor.ConflictMonitorProperties;
import us.dot.its.jpo.conflictmonitor.monitor.algorithms.broadcast_rate.BroadcastRateParameters;
import us.dot.its.jpo.conflictmonitor.monitor.algorithms.broadcast_rate.map.MapBroadcastRateAlgorithm;
import us.dot.its.jpo.conflictmonitor.monitor.algorithms.broadcast_rate.map.MapBroadcastRateAlgorithmFactory;
import us.dot.its.jpo.conflictmonitor.monitor.algorithms.broadcast_rate.map.MapBroadcastRateParameters;
import us.dot.its.jpo.conflictmonitor.monitor.algorithms.broadcast_rate.map.MapBroadcastRateParametersFactory;
import us.dot.its.jpo.conflictmonitor.monitor.algorithms.broadcast_rate.spat.SpatBroadcastRateAlgorithm;
import us.dot.its.jpo.conflictmonitor.monitor.algorithms.broadcast_rate.spat.SpatBroadcastRateAlgorithmFactory;
import us.dot.its.jpo.conflictmonitor.monitor.algorithms.broadcast_rate.spat.SpatBroadcastRateParameters;
import us.dot.its.jpo.conflictmonitor.monitor.algorithms.broadcast_rate.spat.SpatBroadcastRateParametersFactory;
import us.dot.its.jpo.conflictmonitor.monitor.topologies.BsmEventTopology;
import us.dot.its.jpo.conflictmonitor.monitor.topologies.MessageIngestTopology;
import us.dot.its.jpo.conflictmonitor.monitor.topologies.IntersectionEventTopology;
import us.dot.its.jpo.ode.model.OdeBsmData;

/**
 * Launches ToGeoJsonFromJsonConverter service
 */
@Controller
public class MonitorServiceController {

    private static final Logger logger = LoggerFactory.getLogger(MonitorServiceController.class);
    org.apache.kafka.common.serialization.Serdes bas;

   
    
    // private MapBroadcastRateAlgorithmFactory mapBroadcastRateAlgorithmFactory;

    // @Autowired
    // public void setMapBroadcastRateAlgorithmFactory(MapBroadcastRateAlgorithmFactory factory) {
    //     this.mapBroadcastRateAlgorithmFactory = factory;
    // }

    

    
 
    
    @Autowired
    public MonitorServiceController(ConflictMonitorProperties conflictMonitorProps) {
        

        // logger.info("Starting {}", this.getClass().getSimpleName());

        // // Starting the MAP geoJSON converter Kafka message consumer
        // logger.info("Creating the MAP geoJSON Converter MessageConsumer");
        
        // MapHandler mapConverter = new MapHandler(conflictMonitorProps);
        // MessageConsumer<String, String> mapJsonConsumer = MessageConsumer.defaultStringMessageConsumer(
        //     conflictMonitorProps.getKafkaBrokers(), this.getClass().getSimpleName(), mapConverter);
        // mapJsonConsumer.setName("MapJsonToGeoJsonConsumer");
        // mapConverter.start(mapJsonConsumer, conflictMonitorProps.getKafkaTopicOdeMapTxPojo());

        String bsmStoreName = "BsmWindowStore";
        String spatStoreName = "SpatWindowStore";
        String mapStoreName = "MapWindowStore";

        try {
            logger.info("Starting {}", this.getClass().getSimpleName());
            // Topology topology;
            // KafkaStreams streams;

            

            // MapBroadcastRate Topology
            // Sends "MAP Broadcast Rate" events when the number of MAPs per rolling period is too low or too high
            MapBroadcastRateAlgorithm mapBroadcastRateAlgorithm = 
                conflictMonitorProps.getMapBroadcastRateAlgorithmFactory()
                    .getAlgorithm(conflictMonitorProps.getMapBroadcastRateAlgorithm());
            MapBroadcastRateParameters mapBroadcastRateParameters = 
                conflictMonitorProps.getMapBroadcastRateParametersFactory()
                    .getParameters(conflictMonitorProps.getMapBroadcastRateParameters());
            mapBroadcastRateParameters.setStreamsProperties(conflictMonitorProps.createStreamProperties("mapBroadcastRate"));
            mapBroadcastRateAlgorithm.setParameters(mapBroadcastRateParameters);
            Runtime.getRuntime().addShutdownHook(new Thread(mapBroadcastRateAlgorithm::stop));
            mapBroadcastRateAlgorithm.start();

            // SpatBroadcastRate Topology
            // Sends "SPAT Broadcast Rate" events when the number of SPATs per rolling period is too low or too high
            // SpatBroadcastRateAlgorithm spatBroadcastRateAlgorithm = spatBroadcastRateAlgorithmFactory.getAlgorithm(conflictMonitorProps.getSpatBroadcastRateAlgorithm());
            // SpatBroadcastRateParameters spatBroadcastRateParameters = spatBroadcastRateParametersFactory.getParameters(conflictMonitorProps.getSpatBroadcastRateParameters());
            // spatBroadcastRateAlgorithm.setParameters(spatBroadcastRateParameters);
            // Runtime.getRuntime().addShutdownHook(new Thread(spatBroadcastRateAlgorithm::stop));
            // spatBroadcastRateAlgorithm.start();




            // BSM Topology sends a message every time a vehicle drives through the intersection. 
            // Topology topology = BsmEventTopology.build(conflictMonitorProps.getKafkaTopicOdeBsmJson(), conflictMonitorProps.getKafkaTopicCmBsmEvent());
            // KafkaStreams streams = new KafkaStreams(topology, conflictMonitorProps.createStreamProperties("bsmEvent"));
            // Runtime.getRuntime().addShutdownHook(new Thread(streams::close));
            // streams.start(); 


            // the message ingest topology tracks and stores incoming messages for further processing
            // topology = MessageIngestTopology.build(
            //     conflictMonitorProps.getKafkaTopicOdeBsmJson(),
            //     bsmStoreName,
            //     conflictMonitorProps.getKafkaTopicSpatGeoJson(),
            //     spatStoreName,
            //     conflictMonitorProps.getKafkaTopicMapGeoJson(),
            //     mapStoreName
            // );
            // streams = new KafkaStreams(topology, conflictMonitorProps.createStreamProperties("messageIngest"));
            // Runtime.getRuntime().addShutdownHook(new Thread(streams::close));
            // streams.start();

            
            // Thread.sleep(5000);
            
            // ReadOnlyWindowStore<String, OdeBsmData> windowStore =
            //     streams.store(bsmStoreName, QueryableStoreTypes.windowStore());


            // the IntersectionEventTopology grabs snapshots of spat / map / bsm and processes data when a vehicle passes through
            // topology = IntersectionEventTopology.build(conflictMonitorProps.getKafkaTopicCmBsmEvent(), windowStore);
            // streams = new KafkaStreams(topology, conflictMonitorProps.createStreamProperties("intersectionEvent"));
            // Runtime.getRuntime().addShutdownHook(new Thread(streams::close));
            // streams.start();

            

            
            logger.info("All geoJSON conversion services started!");
        } catch (Exception e) {
            logger.error("Encountered issue with creating topologies", e);
        }
    }
}
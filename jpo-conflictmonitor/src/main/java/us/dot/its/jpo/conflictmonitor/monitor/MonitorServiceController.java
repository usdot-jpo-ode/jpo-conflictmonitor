package us.dot.its.jpo.conflictmonitor.monitor;

import org.apache.kafka.streams.KafkaStreams;
import org.apache.kafka.streams.Topology;
import org.apache.kafka.streams.state.KeyValueStore;
import org.apache.kafka.streams.state.QueryableStoreTypes;
import org.apache.kafka.streams.state.ReadOnlyKeyValueStore;
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
import us.dot.its.jpo.conflictmonitor.monitor.algorithms.broadcast_rate.map.MapBroadcastRateStreamsAlgorithm;
import us.dot.its.jpo.conflictmonitor.monitor.algorithms.broadcast_rate.spat.SpatBroadcastRateAlgorithm;
import us.dot.its.jpo.conflictmonitor.monitor.algorithms.broadcast_rate.spat.SpatBroadcastRateAlgorithmFactory;
import us.dot.its.jpo.conflictmonitor.monitor.algorithms.broadcast_rate.spat.SpatBroadcastRateParameters;
import us.dot.its.jpo.conflictmonitor.monitor.algorithms.broadcast_rate.spat.SpatBroadcastRateParametersFactory;
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

            

            // Map Broadcast Rate Topology
            // Sends "MAP Broadcast Rate" events when the number of MAPs per rolling period is too low or too high
            MapBroadcastRateAlgorithmFactory mapAlgoFactory = conflictMonitorProps.getMapBroadcastRateAlgorithmFactory();
            MapBroadcastRateParametersFactory mapParamsFactory = conflictMonitorProps.getMapBroadcastRateParametersFactory();
            String mapAlgo = conflictMonitorProps.getMapBroadcastRateAlgorithm();
            String mapParams = conflictMonitorProps.getMapBroadcastRateParameters();
            MapBroadcastRateAlgorithm mapCountAlgo = mapAlgoFactory.getAlgorithm(mapAlgo);
            MapBroadcastRateParameters mapCountParams = mapParamsFactory.getParameters(mapParams);
            if (mapCountAlgo instanceof MapBroadcastRateStreamsAlgorithm) {
                ((MapBroadcastRateStreamsAlgorithm)mapCountAlgo).setStreamsProperties(conflictMonitorProps.createStreamProperties("mapBroadcastRate"));
            }
            mapCountAlgo.setParameters(mapCountParams);
            Runtime.getRuntime().addShutdownHook(new Thread(mapCountAlgo::stop));
            mapCountAlgo.start();

            
            // Spat Broadcast Rate Topology
            // Sends "SPAT Broadcast Rate" events when the number of SPATs per rolling period is too low or too high
            SpatBroadcastRateAlgorithmFactory spatAlgoFactory = conflictMonitorProps.getSpatBroadcastRateAlgorithmFactory();
            SpatBroadcastRateParametersFactory spatParamsFactory = conflictMonitorProps.getSpatBroadcastRateParametersFactory();
            String spatAlgo = conflictMonitorProps.getSpatBroadcastRateAlgorithm();
            String spatParams = conflictMonitorProps.getSpatBroadcastRateParameters();
            SpatBroadcastRateAlgorithm spatCountAlgo = spatAlgoFactory.getAlgorithm(spatAlgo);
            SpatBroadcastRateParameters spatCountParams = spatParamsFactory.getParameters(spatParams);
            if (spatCountAlgo instanceof SpatBroadcastRateStreamsAlgorithm) {
                ((SpatBroadcastRateStreamsAlgorithm)spatCountAlgo).setStreamsProperties(conflictMonitorProps.createStreamProperties("spatBroadcastRate"));
            }
            spatCountAlgo.setParameters(spatCountParams);
            Runtime.getRuntime().addShutdownHook(new Thread(spatCountAlgo::stop));
            spatCountAlgo.start();




            // BSM Topology sends a message every time a vehicle drives through the intersection. 
            Topology topology = BsmEventTopology.build(conflictMonitorProps.getKafkaTopicOdeBsmJson(), conflictMonitorProps.getKafkaTopicCmBsmEvent());
            KafkaStreams streams = new KafkaStreams(topology, conflictMonitorProps.createStreamProperties("bsmEvent"));
            Runtime.getRuntime().addShutdownHook(new Thread(streams::close));
            streams.start(); 


            // the message ingest topology tracks and stores incoming messages for further processing
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

            
            Thread.sleep(5000);
            
            ReadOnlyWindowStore<String, OdeBsmData> bsmWindowStore =
                streams.store(bsmStoreName, QueryableStoreTypes.windowStore());

            ReadOnlyWindowStore<String, ProcessedSpat> spatWindowStore =
                streams.store(spatStoreName, QueryableStoreTypes.windowStore());

            ReadOnlyKeyValueStore<String, MapFeatureCollection> mapKeyValueStore =
                streams.store(mapStoreName, QueryableStoreTypes.keyValueStore());


            //the IntersectionEventTopology grabs snapshots of spat / map / bsm and processes data when a vehicle passes through
            topology = IntersectionEventTopology.build(conflictMonitorProps.getKafkaTopicCmBsmEvent(), bsmWindowStore, spatWindowStore, mapKeyValueStore);
            streams = new KafkaStreams(topology, conflictMonitorProps.createStreamProperties("intersectionEvent"));
            Runtime.getRuntime().addShutdownHook(new Thread(streams::close));
            streams.start();

            

            
            logger.info("All geoJSON conversion services started!");
        } catch (Exception e) {
            logger.error("Encountered issue with creating topologies", e);
        }
    }
}
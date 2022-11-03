package us.dot.its.jpo.conflictmonitor.monitor;

import org.apache.kafka.streams.KafkaStreams;
import org.apache.kafka.streams.Topology;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;

import us.dot.its.jpo.conflictmonitor.ConflictMonitorProperties;
import us.dot.its.jpo.ode.wrapper.MessageConsumer;

/**
 * Launches ToGeoJsonFromJsonConverter service
 */
@Controller
public class MonitorServiceController {

    private static final Logger logger = LoggerFactory.getLogger(MonitorServiceController.class);
    org.apache.kafka.common.serialization.Serdes bas;

    @Autowired
    public MonitorServiceController(ConflictMonitorProperties conflictMonitorProps) {
        super();

        // logger.info("Starting {}", this.getClass().getSimpleName());

        // // Starting the MAP geoJSON converter Kafka message consumer
        // logger.info("Creating the MAP geoJSON Converter MessageConsumer");
        
        // MapHandler mapConverter = new MapHandler(conflictMonitorProps);
        // MessageConsumer<String, String> mapJsonConsumer = MessageConsumer.defaultStringMessageConsumer(
        //     conflictMonitorProps.getKafkaBrokers(), this.getClass().getSimpleName(), mapConverter);
        // mapJsonConsumer.setName("MapJsonToGeoJsonConsumer");
        // mapConverter.start(mapJsonConsumer, conflictMonitorProps.getKafkaTopicOdeMapTxPojo());


        try {
            logger.info("Starting {}", this.getClass().getSimpleName());
            Topology topology;
            KafkaStreams streams;

            // MAP
            logger.info("Creating the MAP geoJSON Kafka-Streams topology");
            topology = ConflictTopology.build(conflictMonitorProps.getKafkaTopicMapGeoJson(), conflictMonitorProps.getKafkaTopicSpatGeoJson());
            System.out.println("Topology Description " + topology.describe());
            streams = new KafkaStreams(topology, conflictMonitorProps.createStreamProperties("conflictmonitor"));
            Runtime.getRuntime().addShutdownHook(new Thread(streams::close));
            streams.start();

            // // SPaT
            // logger.info("Creating the SPaT geoJSON Kafka-Streams topology");
            // topology = SpatTopology.build(geojsonProps.getKafkaTopicOdeSpatJson(), geojsonProps.getKafkaTopicSpatGeoJson(), geojsonProps.getKafkaTopicMapGeoJson());
            // streams = new KafkaStreams(topology, geojsonProps.createStreamProperties("spatgeojson"));
            // Runtime.getRuntime().addShutdownHook(new Thread(streams::close));
            // streams.start();

            logger.info("All geoJSON conversion services started!");
        } catch (Exception e) {
            logger.error("Encountered issue with creating topologies", e);
        }
    }
}
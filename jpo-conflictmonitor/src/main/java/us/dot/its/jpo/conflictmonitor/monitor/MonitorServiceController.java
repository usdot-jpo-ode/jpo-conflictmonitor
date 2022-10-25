package us.dot.its.jpo.conflictmonitor.monitor;

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
    public MonitorServiceController(ConflictMonitorProperties geojsonProps) {
        super();

        logger.info("Starting {}", this.getClass().getSimpleName());

        // Starting the MAP geoJSON converter Kafka message consumer
        logger.info("Creating the MAP geoJSON Converter MessageConsumer");
        
        MapHandler mapConverter = new MapHandler(geojsonProps);
        MessageConsumer<String, String> mapJsonConsumer = MessageConsumer.defaultStringMessageConsumer(
            geojsonProps.getKafkaBrokers(), this.getClass().getSimpleName(), mapConverter);
        mapJsonConsumer.setName("MapJsonToGeoJsonConsumer");
        mapConverter.start(mapJsonConsumer, geojsonProps.getKafkaTopicOdeMapTxPojo());
    }
}
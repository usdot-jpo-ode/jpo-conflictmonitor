package us.dot.its.jpo.conflictmonitor.monitor.topologies.map_spat_validation;

import static org.apache.commons.lang3.StringUtils.isNotBlank;

import java.time.Instant;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;

import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.streams.processor.TimestampExtractor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import us.dot.its.jpo.geojsonconverter.pojos.geojson.map.ProcessedMap;


public class MapTimestampExtractor implements TimestampExtractor {

    private static final Logger logger = LoggerFactory.getLogger(MapTimestampExtractor.class);

    @Override
    public long extract(ConsumerRecord<Object, Object> record, long partitionTime) {
        ProcessedMap map = (ProcessedMap) record.value();
        if (map != null && map.getProperties() != null && map.getProperties().getOdeReceivedAt() != null) {
            var timestamp = extractTimestamp(map);
            if (timestamp > -1) {
                return timestamp;
            }
        } 
        logger.warn("Failed to extract timestamp, using clock time");
        // Partition time is invalid, return current clock time
        return Instant.now().toEpochMilli();
     }

    public static long extractTimestamp(ProcessedMap map) {
        try{
            ZonedDateTime zdt = map.getProperties().getOdeReceivedAt();
            long timestamp =  zdt.toInstant().toEpochMilli();
            return timestamp;
        } catch (Exception e){
            logger.error("Timestamp Parsing Failed", e);
            return -1;
        }
    }
    
}

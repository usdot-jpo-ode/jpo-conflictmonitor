package us.dot.its.jpo.conflictmonitor.monitor.topologies.broadcast_rate;

import static org.apache.commons.lang3.StringUtils.isNotBlank;

import java.time.Instant;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;

import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.streams.processor.TimestampExtractor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import us.dot.its.jpo.ode.model.OdeMapData;

public class MapTimestampExtractor implements TimestampExtractor {

    private static final Logger logger = LoggerFactory.getLogger(MapTimestampExtractor.class);

    @Override
    public long extract(ConsumerRecord<Object, Object> record, long partitionTime) {
        OdeMapData map = (OdeMapData) record.value();
        if (map != null && map.getMetadata() != null && isNotBlank(map.getMetadata().getOdeReceivedAt())) {
            var timestamp = extractTimestamp(map);
            if (timestamp > -1) {
                return timestamp;
            }
        } 
        logger.warn("Failed to extract timestamp, using clock time");
        // Partition time is invalid, return current clock time
        return Instant.now().toEpochMilli();
     }

    public static long extractTimestamp(OdeMapData map) {
        try{
            ZonedDateTime zdt = ZonedDateTime.parse(map.getMetadata().getOdeReceivedAt(), DateTimeFormatter.ISO_DATE_TIME);
            long timestamp =  zdt.toInstant().toEpochMilli();
            return timestamp;
        } catch (DateTimeParseException e){
            logger.error("Timestamp Parsing Failed", e);
            return -1;
        }
    }
    
}

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

import us.dot.its.jpo.ode.model.OdeSpatData;

public class SpatTimestampExtractor implements TimestampExtractor {
    
    private static final Logger logger = LoggerFactory.getLogger(MapTimestampExtractor.class);

    @Override
    public long extract(ConsumerRecord<Object, Object> record, long partitionTime) {
        OdeSpatData spat = (OdeSpatData) record.value();
        if (spat != null && spat.getMetadata() != null && isNotBlank(spat.getMetadata().getOdeReceivedAt())) {
            var timestamp = extractTimestamp(spat);
            if (timestamp > -1) {
                return timestamp;
            }
        } 
        logger.warn("Failed to extract timestamp, using clock time");
        // Partition time is invalid, return current clock time
        return Instant.now().toEpochMilli();
     }

    public static long extractTimestamp(OdeSpatData spat) {
        try{
            ZonedDateTime zdt = ZonedDateTime.parse(spat.getMetadata().getOdeReceivedAt(), DateTimeFormatter.ISO_DATE_TIME);
            long timestamp =  zdt.toInstant().toEpochMilli();
            return timestamp;
        } catch (DateTimeParseException e){
            logger.error("Timestamp Parsing Failed", e);
            return -1;
        }
    }
}

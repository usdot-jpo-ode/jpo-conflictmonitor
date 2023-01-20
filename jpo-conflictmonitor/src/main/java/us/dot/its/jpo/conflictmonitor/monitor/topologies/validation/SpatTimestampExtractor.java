package us.dot.its.jpo.conflictmonitor.monitor.topologies.validation;

import static org.apache.commons.lang3.StringUtils.isNotBlank;

import java.time.Instant;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;

import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.streams.processor.TimestampExtractor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import us.dot.its.jpo.geojsonconverter.pojos.spat.ProcessedSpat;
import us.dot.its.jpo.ode.model.OdeSpatData;

public class SpatTimestampExtractor implements TimestampExtractor {
    
    private static final Logger logger = LoggerFactory.getLogger(SpatTimestampExtractor.class);

    @Override
    public long extract(ConsumerRecord<Object, Object> record, long partitionTime) {
        ProcessedSpat spat = (ProcessedSpat) record.value();
        if (spat != null) {
            var timestamp = extractTimestamp(spat);
            if (timestamp > -1) {
                return timestamp;
            }
        } 
        logger.warn("Failed to extract timestamp, using clock time");
        // Partition time is invalid, return current clock time
        return Instant.now().toEpochMilli();
     }

    public static long extractTimestamp(ProcessedSpat spat) {
        try{
            ZonedDateTime zdt = spat.getUtcTimeStamp();
            long timestamp =  zdt.toInstant().toEpochMilli();
            return timestamp;
        } catch (Exception e){
            logger.error("Timestamp Parsing Failed", e);
            return -1;
        }
    }
}

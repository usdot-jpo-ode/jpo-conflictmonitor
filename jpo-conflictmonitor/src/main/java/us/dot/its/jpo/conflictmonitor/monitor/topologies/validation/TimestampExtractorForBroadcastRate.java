package us.dot.its.jpo.conflictmonitor.monitor.topologies.validation;

import java.time.Instant;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;

import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.streams.processor.TimestampExtractor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import us.dot.its.jpo.geojsonconverter.pojos.geojson.map.ProcessedMap;
import us.dot.its.jpo.geojsonconverter.pojos.spat.ProcessedSpat;

/**
 * Timestamp Extractor for Broadcast Rate monitoring for Processed Map and Spat messages.
 * 
 * <p>Uses the ODE Received time to get an accurate rate count in case the 
 * timestamp fields embedded in the messages are missing.
 */
public class TimestampExtractorForBroadcastRate implements TimestampExtractor {
    
    private static final Logger logger = LoggerFactory.getLogger(TimestampExtractorForBroadcastRate.class);

    @Override
    public long extract(ConsumerRecord<Object, Object> record, long partitionTime) {
        var value = record.value();
        
        if (value instanceof ProcessedSpat) {
            var timestamp = extractTimestamp((ProcessedSpat)value);
            if (timestamp > -1) {
                return timestamp;
            }
        } 
        
        if (value instanceof ProcessedMap) {
            var timestamp = extractTimestamp((ProcessedMap)value);
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
            ZonedDateTime zdt = ZonedDateTime.parse(spat.getOdeReceivedAt(), DateTimeFormatter.ISO_DATE_TIME);
            long timestamp =  zdt.toInstant().toEpochMilli();
            return timestamp;
        } catch (Exception e){
            logger.error("Timestamp Parsing Failed", e);
            return -1;
        }
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

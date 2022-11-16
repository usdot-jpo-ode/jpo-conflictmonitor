package us.dot.its.jpo.conflictmonitor.monitor.models.broadcast_rate;

import static org.apache.commons.lang3.StringUtils.isNotBlank;

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
        if (map != null && map.getMetadata() != null && isNotBlank(map.getMetadata().getRecordGeneratedAt())) {
            return extractMapTimestamp(map);
        }
        return partitionTime;
    }

    public static long extractMapTimestamp(OdeMapData map) {
        try{
            ZonedDateTime zdt = ZonedDateTime.parse(map.getMetadata().getOdeReceivedAt(), DateTimeFormatter.ISO_DATE_TIME);
            long timestamp =  zdt.toInstant().toEpochMilli();
            logger.info("Timestamp {}", timestamp);
            return timestamp;
        } catch (DateTimeParseException e){
            logger.error("Timestamp Parsing Failed", e);
            return -1;
        }
    }
    
}

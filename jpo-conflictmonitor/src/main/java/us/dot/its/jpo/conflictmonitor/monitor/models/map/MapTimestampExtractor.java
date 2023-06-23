package us.dot.its.jpo.conflictmonitor.monitor.models.map;

import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;

import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.streams.processor.TimestampExtractor;

import us.dot.its.jpo.geojsonconverter.pojos.geojson.map.ProcessedMap;
import us.dot.its.jpo.ode.model.OdeMapData;

public class MapTimestampExtractor implements TimestampExtractor{
    
    @Override
    public long extract(ConsumerRecord<Object, Object> record, long partitionTime) {
        OdeMapData map = (OdeMapData) record.value();
        if(map != null && map.getMetadata().getRecordGeneratedAt() != null){
            return getMapTimestamp(map);
        }

        return partitionTime;
    }

    public static long getMapTimestamp(OdeMapData map){
        try{
            ZonedDateTime time = ZonedDateTime.parse(map.getMetadata().getRecordGeneratedAt(), DateTimeFormatter.ISO_ZONED_DATE_TIME);
            return time.toInstant().toEpochMilli();
        }catch (DateTimeParseException e){
            System.out.println("Timestamp Parsing Failed");
            return -1;
        }
    }

    public static long getProcessedMapTimestamp(ProcessedMap map){
        try{
            ZonedDateTime time = map.getProperties().getTimeStamp();
            return time.toInstant().toEpochMilli();
        }catch (DateTimeParseException e){
            System.out.println("Timestamp Parsing Failed");
            return -1;
        }
    }
}

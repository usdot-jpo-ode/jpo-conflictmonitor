package us.dot.its.jpo.conflictmonitor.monitor.models.spat;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;

import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.streams.processor.TimestampExtractor;

import us.dot.its.jpo.geojsonconverter.pojos.spat.ProcessedSpat;
import us.dot.its.jpo.ode.model.OdeBsmData;


public class SpatTimestampExtractor implements TimestampExtractor {

    @Override
    public long extract(ConsumerRecord<Object, Object> record, long partitionTime) {
        ProcessedSpat spat = (ProcessedSpat) record.value();
        if(spat != null ){
            return getSpatTimestamp(spat);
        }

        return partitionTime;
    }

    public static long getSpatTimestamp(ProcessedSpat spat){
        try{
            if(spat.getUtcTimeStamp() != null){
                ZonedDateTime time = ZonedDateTime.parse(spat.getUtcTimeStamp(), DateTimeFormatter.ISO_ZONED_DATE_TIME);
                return time.toInstant().toEpochMilli();
            }else{
                System.out.println("Spat Timestamp Parsing Failed. Input Timestamp was null");
                return -1;
            }
            
        }catch (DateTimeParseException e){
            System.out.println("Spat Timestamp Parsing Failed");
            return -1;
        }
    }
}

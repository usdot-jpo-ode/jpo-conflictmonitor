package us.dot.its.jpo.conflictmonitor.monitor.models.bsm;

import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;

import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.streams.processor.TimestampExtractor;

import us.dot.its.jpo.ode.model.OdeBsmData;

public class BsmTimestampExtractor implements TimestampExtractor {

    @Override
    public long extract(ConsumerRecord<Object, Object> record, long partitionTime) {
        OdeBsmData bsm = (OdeBsmData) record.value();
        if(bsm != null && !bsm.getMetadata().getRecordGeneratedAt().equals("")){
            return getBsmTimestamp(bsm);
        }

        return partitionTime;
    }

    public static long getBsmTimestamp(OdeBsmData bsm){
        try{
            ZonedDateTime time = ZonedDateTime.parse(bsm.getMetadata().getRecordGeneratedAt(), DateTimeFormatter.ISO_ZONED_DATE_TIME);
            return time.toInstant().toEpochMilli();
        }catch (DateTimeParseException e){
            System.out.println("Timestamp Parsing Failed");
            return -1;
        }
    }
}

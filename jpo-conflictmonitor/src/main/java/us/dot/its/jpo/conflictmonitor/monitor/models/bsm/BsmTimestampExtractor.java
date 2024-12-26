package us.dot.its.jpo.conflictmonitor.monitor.models.bsm;

import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.streams.processor.TimestampExtractor;
import us.dot.its.jpo.conflictmonitor.monitor.utils.BsmUtils;
import us.dot.its.jpo.geojsonconverter.pojos.geojson.bsm.BsmProperties;
import us.dot.its.jpo.geojsonconverter.pojos.geojson.bsm.ProcessedBsm;


import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;

@Slf4j
public class BsmTimestampExtractor implements TimestampExtractor {


    @Override
    public long extract(ConsumerRecord<Object, Object> record, long partitionTime) {
        if (record.value() instanceof ProcessedBsm<?> processedBsm) {
            return getBsmTimestamp(processedBsm);
        }
        return partitionTime;
    }

    public static long getBsmTimestamp(ProcessedBsm<?> bsm){
        try{
            ZonedDateTime time = ZonedDateTime.parse(bsm.getOdeReceivedAt(), DateTimeFormatter.ISO_ZONED_DATE_TIME);
            BsmProperties properties = BsmUtils.getProperties(bsm).orElseThrow();
            final int secMark = properties.getSecMark();
            final ZonedDateTime refTime = getRefTime(secMark, time);
            return refTime.toInstant().toEpochMilli();
        } catch (Exception e){
            log.error("Timestamp Parsing Failed", e);
            return -1;
        }
    }

    private static ZonedDateTime getRefTime(int secMark, ZonedDateTime time) {
        final int second = secMark / 1000;
        final int nanosecond = (secMark % 1000) * 1000000;

        ZonedDateTime refTime = ZonedDateTime.of(time.getYear(), time.getMonthValue(), time.getDayOfMonth(),
                time.getHour(), time.getMinute(), second, nanosecond, time.getZone());

        // If the secondmark is close to the rollover point of the minute and the ODE received at time is at the
        // beginning of the minute, subtract a minute.
        if(secMark > 50000 && time.getSecond() < 10){
            refTime = refTime.minusMinutes(1);
        }
        return refTime;
    }

}

package us.dot.its.jpo.conflictmonitor.monitor.models.bsm;

import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;

import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.streams.processor.TimestampExtractor;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import us.dot.its.jpo.ode.model.OdeBsmData;
import us.dot.its.jpo.ode.plugin.j2735.J2735Bsm;

public class BsmTimestampExtractor implements TimestampExtractor {

    private static final Logger logger = LoggerFactory.getLogger(BsmTimestampExtractor.class);

    /** 
     * @param record A Kafka consumer record. The value of this record should be a BSM message
     * @param partitionTime A UTC timestamp in milliseconds of when the record was added to the Kafka partition. 
     * @return a long represting the UTC timestamp in milliseconds from the consumer record. If the record type is a BSM the BSM time used. Otherwise the partition time is used.
     */
    @Override
    public long extract(ConsumerRecord<Object, Object> record, long partitionTime) {
        OdeBsmData bsm = (OdeBsmData) record.value();
        if(bsm != null){
            return getBsmTimestamp(bsm);
        }

        return partitionTime;
    }

    /** 
     * @return A long representing the UTC timestamp in milliseconds from the BSM message. The year is assumed to be the current year.
     */
    public static long getBsmTimestamp(OdeBsmData bsm){
        try{
            ZonedDateTime time = ZonedDateTime.parse(bsm.getMetadata().getOdeReceivedAt(), DateTimeFormatter.ISO_ZONED_DATE_TIME);
            int secmark = ((J2735Bsm)bsm.getPayload().getData()).getCoreData().getSecMark();
            int second = (int)secmark / 1000;
            int nanosecond = (secmark % 1000) * 1000000;

            ZonedDateTime refTime = ZonedDateTime.of(time.getYear(), time.getMonthValue(), time.getDayOfMonth(), time.getHour(), time.getMinute(), second, nanosecond, time.getZone());

            // If the secondmark is close to the rollover point of the minute and the ODE received at time is at the beginning of the minute. Subtract a minute.
            if(secmark > 50000 && time.getSecond() < 10){
                refTime = refTime.minus(1, ChronoUnit.MINUTES);
            }

            return refTime.toInstant().toEpochMilli();
        } catch (Exception e){
            logger.error("Timestamp Parsing Failed", e);
            return -1;
        }
    }
}

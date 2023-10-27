package us.dot.its.jpo.conflictmonitor.monitor.models.events.TimestampExtractors;

import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.streams.processor.TimestampExtractor;

import us.dot.its.jpo.conflictmonitor.monitor.models.events.StopLineStopEvent;


public class StopLineStopTimestampExtractor implements TimestampExtractor {

    @Override
    public long extract(ConsumerRecord<Object, Object> record, long partitionTime) {
        StopLineStopEvent event = (StopLineStopEvent) record.value();
        if(event != null){
            return event.getFinalTimestamp();
        }

        return partitionTime;
    }
}


package us.dot.its.jpo.conflictmonitor.monitor.models.events.TimestampExtractors;

import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.streams.processor.TimestampExtractor;

import us.dot.its.jpo.conflictmonitor.monitor.models.events.StopLinePassageEvent;


public class SignalStateTimestampExtractor implements TimestampExtractor {

    @Override
    public long extract(ConsumerRecord<Object, Object> record, long partitionTime) {
        StopLinePassageEvent event = (StopLinePassageEvent) record.value();
        if(event != null){
            return event.getTimestamp();
        }

        return partitionTime;
    }
}


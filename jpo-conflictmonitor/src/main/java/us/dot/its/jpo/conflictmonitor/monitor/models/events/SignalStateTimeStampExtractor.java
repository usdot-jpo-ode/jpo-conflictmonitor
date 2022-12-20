package us.dot.its.jpo.conflictmonitor.monitor.models.events;

import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.streams.processor.TimestampExtractor;


public class SignalStateTimeStampExtractor implements TimestampExtractor {

    @Override
    public long extract(ConsumerRecord<Object, Object> record, long partitionTime) {
        SignalStateEvent event = (SignalStateEvent) record.value();
        if(event != null){
            return event.getTimestamp();
        }

        return partitionTime;
    }
}


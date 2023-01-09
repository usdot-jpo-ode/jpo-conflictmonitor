package us.dot.its.jpo.conflictmonitor.monitor.models.events.TimestampExtractors;

import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.streams.processor.TimestampExtractor;

import us.dot.its.jpo.conflictmonitor.monitor.models.events.ConnectionOfTravelEvent;


public class ConnectionOfTravelTimestampExtractor implements TimestampExtractor {

    @Override
    public long extract(ConsumerRecord<Object, Object> record, long partitionTime) {
        ConnectionOfTravelEvent event = (ConnectionOfTravelEvent) record.value();
        if(event != null){
            return event.getTimestamp();
        }

        return partitionTime;
    }
}


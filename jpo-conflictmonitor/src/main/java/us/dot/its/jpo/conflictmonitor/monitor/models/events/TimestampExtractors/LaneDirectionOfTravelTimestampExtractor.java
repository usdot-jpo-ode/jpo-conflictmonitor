package us.dot.its.jpo.conflictmonitor.monitor.models.events.TimestampExtractors;

import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.streams.processor.TimestampExtractor;
import us.dot.its.jpo.conflictmonitor.monitor.models.events.LaneDirectionOfTravelEvent;

public class LaneDirectionOfTravelTimestampExtractor implements TimestampExtractor {

    @Override
    public long extract(ConsumerRecord<Object, Object> record, long partitionTime) {
        LaneDirectionOfTravelEvent event = (LaneDirectionOfTravelEvent) record.value();
        if(event != null){
            return event.getTimestamp();
        }

        return partitionTime;
    }
}


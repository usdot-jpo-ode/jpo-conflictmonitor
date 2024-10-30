package us.dot.its.jpo.conflictmonitor.monitor.processors.aggregation;

import org.apache.kafka.streams.processor.api.ContextualProcessor;
import org.apache.kafka.streams.processor.api.Record;

public class EventAggregationProcessor<TKey, TEvent, TAggEvent> extends ContextualProcessor<TKey, TEvent, TKey, TAggEvent> {



    @Override
    public void process(Record<TKey, TEvent> record) {

    }
}

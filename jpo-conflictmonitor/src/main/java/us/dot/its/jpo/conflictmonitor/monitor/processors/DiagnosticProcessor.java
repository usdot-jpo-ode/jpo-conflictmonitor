package us.dot.its.jpo.conflictmonitor.monitor.processors;

import org.apache.kafka.streams.processor.api.ContextualProcessor;
import org.apache.kafka.streams.processor.api.Record;
import org.slf4j.Logger;

/***
 * Processor that logs diagnostic info for a streams record: partition, offset etc. for testing/debugging.
 *
 * @param <K> - input key type
 * @param <V> - input value type
 */
public class DiagnosticProcessor<K,V> extends ContextualProcessor<K,V,Void,Void> {

    final Logger logger;
    final String description;

    public DiagnosticProcessor(String description, Logger logger) {
        this.description = description;
        this.logger = logger;
    }

    @Override
    public void process(Record<K, V> record) {
        if (context().recordMetadata().isPresent()) {
            var metadata = context().recordMetadata().get();
            logger.info("{}: topic: {}, offset: {}, key: {}, partition: {}, timestamp: {}", description,
                    metadata.topic(), metadata.offset(), record.key(), metadata.partition(), record.timestamp());
        }
    }
}

package us.dot.its.jpo.conflictmonitor;

import java.util.Map;

import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.streams.errors.ProductionExceptionHandler;

public class AlwaysContinueProductionExceptionHandler implements ProductionExceptionHandler {
    @Override
    public void configure(Map<String, ?> configs) {
        // Nothing to configure
    }
    @Override
    public ProductionExceptionHandlerResponse handle(ProducerRecord<byte[], byte[]> record, Exception exception) {
        return ProductionExceptionHandlerResponse.CONTINUE;
    }
}
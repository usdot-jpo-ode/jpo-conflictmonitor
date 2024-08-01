package us.dot.its.jpo.deduplicator.deduplicator.processors.suppliers;

import java.time.format.DateTimeFormatter;
import org.apache.kafka.streams.processor.api.Processor;
import org.apache.kafka.streams.processor.api.ProcessorSupplier;
import com.fasterxml.jackson.databind.JsonNode;

import us.dot.its.jpo.deduplicator.deduplicator.processors.OdeTimJsonProcessor;

public class OdeTimJsonProcessorSupplier implements ProcessorSupplier<String, JsonNode, String, JsonNode> {
    
    String storeName;
    DateTimeFormatter formatter = DateTimeFormatter.ISO_INSTANT;

    public OdeTimJsonProcessorSupplier(String storeName){
        this.storeName = storeName;
    }

    @Override
    public Processor<String, JsonNode, String, JsonNode> get() {
        return new OdeTimJsonProcessor(storeName);
    }
}
package us.dot.its.jpo.deduplicator.deduplicator.processors.suppliers;

import org.apache.kafka.streams.processor.api.Processor;
import org.apache.kafka.streams.processor.api.ProcessorSupplier;
import com.fasterxml.jackson.databind.JsonNode;

import us.dot.its.jpo.deduplicator.DeduplicatorProperties;
import us.dot.its.jpo.deduplicator.deduplicator.processors.OdeRawEncodedTimJsonProcessor;

public class OdeRawEncodedTimProcessorSupplier implements ProcessorSupplier<String, JsonNode, String, JsonNode> {
    
    DeduplicatorProperties props;

    public OdeRawEncodedTimProcessorSupplier(DeduplicatorProperties props){
        this.props = props;
    }

    @Override
    public Processor<String, JsonNode, String, JsonNode> get() {
        return new OdeRawEncodedTimJsonProcessor(props);
    }
}
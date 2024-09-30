package us.dot.its.jpo.deduplicator.deduplicator.processors.suppliers;

import java.time.format.DateTimeFormatter;
import org.apache.kafka.streams.processor.api.Processor;
import org.apache.kafka.streams.processor.api.ProcessorSupplier;

import us.dot.its.jpo.deduplicator.DeduplicatorProperties;
import us.dot.its.jpo.deduplicator.deduplicator.processors.OdeBsmJsonProcessor;
import us.dot.its.jpo.ode.model.OdeBsmData;

public class OdeBsmJsonProcessorSupplier implements ProcessorSupplier<String, OdeBsmData, String, OdeBsmData> {
    
    String storeName;
    DateTimeFormatter formatter = DateTimeFormatter.ISO_INSTANT;
    DeduplicatorProperties props;

    public OdeBsmJsonProcessorSupplier(String storeName, DeduplicatorProperties props){
        this.storeName = storeName;
        this.props = props;
    }

    @Override
    public Processor<String, OdeBsmData, String, OdeBsmData> get() {
        return new OdeBsmJsonProcessor(storeName, props);
    }
}
package us.dot.its.jpo.deduplicator.deduplicator.processors.suppliers;

import org.apache.kafka.streams.processor.api.Processor;
import org.apache.kafka.streams.processor.api.ProcessorSupplier;

import us.dot.its.jpo.deduplicator.DeduplicatorProperties;
import us.dot.its.jpo.deduplicator.deduplicator.processors.OdeMapJsonProcessor;
import us.dot.its.jpo.ode.model.OdeMapData;

public class OdeMapJsonProcessorSupplier implements ProcessorSupplier<String, OdeMapData, String, OdeMapData> {
    
    DeduplicatorProperties props;

    public OdeMapJsonProcessorSupplier(DeduplicatorProperties props){
        this.props = props;
    }

    @Override
    public Processor<String, OdeMapData, String, OdeMapData> get() {
        return new OdeMapJsonProcessor(props);
    }
}
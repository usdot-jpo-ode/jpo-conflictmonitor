package us.dot.its.jpo.deduplicator.deduplicator.processors.suppliers;

import org.apache.kafka.streams.processor.api.Processor;
import org.apache.kafka.streams.processor.api.ProcessorSupplier;

import us.dot.its.jpo.deduplicator.DeduplicatorProperties;
import us.dot.its.jpo.deduplicator.deduplicator.processors.ProcessedSpatProcessor;
import us.dot.its.jpo.geojsonconverter.pojos.spat.ProcessedSpat;

public class ProcessedSpatProcessorSupplier implements ProcessorSupplier<String, ProcessedSpat, String, ProcessedSpat> {
    
    DeduplicatorProperties props;

    public ProcessedSpatProcessorSupplier(DeduplicatorProperties props){
        this.props = props;
    }

    @Override
    public Processor<String, ProcessedSpat, String, ProcessedSpat> get() {
        return new ProcessedSpatProcessor(props);
    }
}
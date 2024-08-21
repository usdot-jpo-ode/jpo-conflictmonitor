package us.dot.its.jpo.deduplicator.deduplicator.processors.suppliers;

import org.apache.kafka.streams.processor.api.Processor;
import org.apache.kafka.streams.processor.api.ProcessorSupplier;

import us.dot.its.jpo.deduplicator.DeduplicatorProperties;
import us.dot.its.jpo.deduplicator.deduplicator.processors.ProcessedMapWktProcessor;
import us.dot.its.jpo.geojsonconverter.pojos.geojson.map.ProcessedMap;

public class ProcessedMapWktProcessorSupplier implements ProcessorSupplier<String, ProcessedMap<String>, String, ProcessedMap<String>> {
    
    DeduplicatorProperties props;

    public ProcessedMapWktProcessorSupplier(DeduplicatorProperties props){
        this.props = props;
    }

    @Override
    public Processor<String, ProcessedMap<String>, String, ProcessedMap<String>> get() {
        return new ProcessedMapWktProcessor(props);
    }
}
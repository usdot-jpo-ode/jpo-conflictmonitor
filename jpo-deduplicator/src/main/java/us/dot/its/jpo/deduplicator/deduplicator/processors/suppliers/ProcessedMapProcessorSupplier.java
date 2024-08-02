package us.dot.its.jpo.deduplicator.deduplicator.processors.suppliers;

import org.apache.kafka.streams.processor.api.Processor;
import org.apache.kafka.streams.processor.api.ProcessorSupplier;

import us.dot.its.jpo.deduplicator.DeduplicatorProperties;
import us.dot.its.jpo.deduplicator.deduplicator.processors.ProcessedMapProcessor;
import us.dot.its.jpo.geojsonconverter.pojos.geojson.LineString;
import us.dot.its.jpo.geojsonconverter.pojos.geojson.map.ProcessedMap;

public class ProcessedMapProcessorSupplier implements ProcessorSupplier<String, ProcessedMap<LineString>, String, ProcessedMap<LineString>> {
    
    DeduplicatorProperties props;

    public ProcessedMapProcessorSupplier(DeduplicatorProperties props){
        this.props = props;
    }

    @Override
    public Processor<String, ProcessedMap<LineString>, String, ProcessedMap<LineString>> get() {
        return new ProcessedMapProcessor(props);
    }
}
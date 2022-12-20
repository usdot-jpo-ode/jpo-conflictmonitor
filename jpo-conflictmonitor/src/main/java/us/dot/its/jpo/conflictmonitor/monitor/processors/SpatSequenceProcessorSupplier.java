package us.dot.its.jpo.conflictmonitor.monitor.processors;

import org.apache.kafka.streams.processor.api.Processor;
import org.apache.kafka.streams.processor.api.ProcessorSupplier;

import us.dot.its.jpo.conflictmonitor.monitor.algorithms.time_change_details.spat.SpatTimeChangeDetailsParameters;
import us.dot.its.jpo.conflictmonitor.monitor.models.events.TimeChangeDetailsEvent;
import us.dot.its.jpo.geojsonconverter.pojos.spat.ProcessedSpat;

public class SpatSequenceProcessorSupplier implements ProcessorSupplier<String, ProcessedSpat, String, TimeChangeDetailsEvent>{

    SpatTimeChangeDetailsParameters parameters;
    public SpatSequenceProcessorSupplier(SpatTimeChangeDetailsParameters parameters){
        this.parameters = parameters;
    }

    @Override
    public Processor get() {
        return (Processor) new SpatSequenceProcessor(parameters);
    }
    
}

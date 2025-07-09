package us.dot.its.jpo.conflictmonitor.monitor.processors;

import org.apache.kafka.streams.processor.api.Processor;
import org.apache.kafka.streams.processor.api.ProcessorSupplier;

import us.dot.its.jpo.conflictmonitor.monitor.algorithms.time_change_details.spat.SpatTimeChangeDetailsParameters;
import us.dot.its.jpo.conflictmonitor.monitor.models.event_state_progression.RsuIntersectionSignalGroupKey;
import us.dot.its.jpo.conflictmonitor.monitor.models.events.TimeChangeDetailsEvent;
import us.dot.its.jpo.conflictmonitor.monitor.models.spat.SpatWithDisabledSignalGroups;
import us.dot.its.jpo.geojsonconverter.partitioner.RsuIntersectionKey;
import us.dot.its.jpo.geojsonconverter.pojos.spat.ProcessedSpat;

public class SpatSequenceProcessorSupplier
        implements
            ProcessorSupplier<
                    RsuIntersectionKey,
                    SpatWithDisabledSignalGroups,
                    RsuIntersectionSignalGroupKey,
                    TimeChangeDetailsEvent>{

    SpatTimeChangeDetailsParameters parameters;

    public SpatSequenceProcessorSupplier(SpatTimeChangeDetailsParameters parameters){
        this.parameters = parameters;
    }

    @Override
    public Processor<RsuIntersectionKey, SpatWithDisabledSignalGroups, RsuIntersectionSignalGroupKey, TimeChangeDetailsEvent> get() {
        return new SpatSequenceProcessor(parameters);
    }

}

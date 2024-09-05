package us.dot.its.jpo.conflictmonitor.monitor.processors;

import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.streams.processor.api.ContextualProcessor;
import org.apache.kafka.streams.processor.api.ProcessorContext;
import org.apache.kafka.streams.processor.api.Record;
import org.apache.kafka.streams.state.KeyValueStore;
import us.dot.its.jpo.conflictmonitor.monitor.algorithms.spat_transition.SpatTransitionParameters;
import us.dot.its.jpo.conflictmonitor.monitor.models.spat_transition.RsuIntersectionSignalGroupKey;
import us.dot.its.jpo.conflictmonitor.monitor.models.spat_transition.SpatMovementState;
import us.dot.its.jpo.conflictmonitor.monitor.models.spat_transition.SpatMovementStateTransition;
import us.dot.its.jpo.geojsonconverter.partitioner.RsuIntersectionKey;
import us.dot.its.jpo.geojsonconverter.pojos.spat.ProcessedSpat;

@Slf4j
public class SpatTransitionProcessor
        extends ContextualProcessor<RsuIntersectionSignalGroupKey, SpatMovementState, RsuIntersectionSignalGroupKey, SpatMovementStateTransition> {

    KeyValueStore<RsuIntersectionSignalGroupKey, SpatMovementState> store;

    final SpatTransitionParameters parameters;

    public SpatTransitionProcessor(SpatTransitionParameters parameters) {
        this.parameters = parameters;
    }

    @Override
    public void init(ProcessorContext<RsuIntersectionSignalGroupKey, SpatMovementStateTransition> context) {
        super.init(context);
        store = context.getStateStore(parameters.getStateStoreName());
    }

    @Override
    public void process(Record<RsuIntersectionSignalGroupKey, SpatMovementState> record) {

    }

    @Override
    public void close() {
        super.close();
    }
}

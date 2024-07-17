package us.dot.its.jpo.conflictmonitor.monitor.topologies.timestamp_delta;

import org.apache.kafka.streams.processor.api.FixedKeyProcessor;
import org.apache.kafka.streams.processor.api.FixedKeyProcessorContext;
import org.apache.kafka.streams.processor.api.FixedKeyRecord;
import us.dot.its.jpo.conflictmonitor.monitor.algorithms.timestamp_delta.bsm.BsmTimestampDeltaParameters;
import us.dot.its.jpo.conflictmonitor.monitor.models.bsm.BsmRsuIdKey;
import us.dot.its.jpo.conflictmonitor.monitor.models.events.timestamp_delta.TimestampDelta;
import us.dot.its.jpo.conflictmonitor.monitor.utils.BsmUtils;
import us.dot.its.jpo.ode.model.OdeBsmData;

public class BsmTimestampDeltaProcessor
    implements FixedKeyProcessor<BsmRsuIdKey, OdeBsmData, TimestampDelta> {

    final BsmTimestampDeltaParameters parameters;

    public BsmTimestampDeltaProcessor(BsmTimestampDeltaParameters parameters) {
        this.parameters = parameters;
    }

    FixedKeyProcessorContext<BsmRsuIdKey, TimestampDelta> context;

    @Override
    public void init(FixedKeyProcessorContext<BsmRsuIdKey, TimestampDelta> context) {
        this.context = context;
    }

    @Override
    public void process(FixedKeyRecord<BsmRsuIdKey, OdeBsmData> record) {
        TimestampDelta delta = new TimestampDelta();
        final long timestamp = record.timestamp();
        final long odeReceivedAt = BsmUtils.getOdeReceivedAt(record.value());
        delta.setMessageTimestampMilliseconds(timestamp);
        delta.setOdeIngestTimestampMilliseconds(odeReceivedAt);
        delta.setMaxDeltaMilliseconds(parameters.getMaxDeltaMilliseconds());
        FixedKeyRecord<BsmRsuIdKey, TimestampDelta> result = record.withValue(delta);
        context.forward(result);
    }


}

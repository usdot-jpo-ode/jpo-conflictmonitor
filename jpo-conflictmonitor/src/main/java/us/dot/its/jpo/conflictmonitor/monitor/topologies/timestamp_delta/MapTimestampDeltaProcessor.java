package us.dot.its.jpo.conflictmonitor.monitor.topologies.timestamp_delta;

import org.apache.kafka.streams.processor.api.FixedKeyProcessor;
import org.apache.kafka.streams.processor.api.FixedKeyProcessorContext;
import org.apache.kafka.streams.processor.api.FixedKeyRecord;
import us.dot.its.jpo.conflictmonitor.monitor.algorithms.timestamp_delta.map.MapTimestampDeltaParameters;
import us.dot.its.jpo.conflictmonitor.monitor.models.bsm.BsmRsuIdKey;
import us.dot.its.jpo.conflictmonitor.monitor.models.events.timestamp_delta.TimestampDelta;
import us.dot.its.jpo.conflictmonitor.monitor.utils.BsmUtils;
import us.dot.its.jpo.geojsonconverter.partitioner.RsuIntersectionKey;
import us.dot.its.jpo.geojsonconverter.pojos.geojson.LineString;
import us.dot.its.jpo.geojsonconverter.pojos.geojson.map.ProcessedMap;

public class MapTimestampDeltaProcessor
    implements FixedKeyProcessor<RsuIntersectionKey, ProcessedMap<LineString>, TimestampDelta> {

    final MapTimestampDeltaParameters parameters;

    public MapTimestampDeltaProcessor(MapTimestampDeltaParameters parameters) {
        this.parameters = parameters;
    }

    FixedKeyProcessorContext<RsuIntersectionKey, TimestampDelta> context;

    @Override
    public void process(FixedKeyRecord<RsuIntersectionKey, ProcessedMap<LineString>> record) {
//        TimestampDelta delta = new TimestampDelta();
//        final long timestamp = record.timestamp();
//        final long odeReceivedAt = record.value().getProperties().getTimeStamp
//        delta.setMessageTimestampMilliseconds(timestamp);
//        delta.setOdeIngestTimestampMilliseconds(odeReceivedAt);
//        delta.setMaxDeltaMilliseconds(parameters.getMaxDeltaMilliseconds());
//        FixedKeyRecord<BsmRsuIdKey, TimestampDelta> result = record.withValue(delta);
//        context.forward(result);
    }
}

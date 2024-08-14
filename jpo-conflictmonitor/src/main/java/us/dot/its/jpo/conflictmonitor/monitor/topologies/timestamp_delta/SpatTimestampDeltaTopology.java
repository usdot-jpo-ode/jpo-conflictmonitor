package us.dot.its.jpo.conflictmonitor.monitor.topologies.timestamp_delta;

import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.streams.kstream.KStream;
import org.apache.kafka.streams.kstream.Produced;
import org.slf4j.Logger;
import org.springframework.stereotype.Component;
import us.dot.its.jpo.conflictmonitor.monitor.algorithms.BaseStreamsBuilder;
import us.dot.its.jpo.conflictmonitor.monitor.algorithms.timestamp_delta.spat.SpatTimestampDeltaParameters;
import us.dot.its.jpo.conflictmonitor.monitor.algorithms.timestamp_delta.spat.SpatTimestampDeltaStreamsAlgorithm;
import us.dot.its.jpo.conflictmonitor.monitor.models.events.timestamp_delta.SpatTimestampDeltaEvent;
import us.dot.its.jpo.conflictmonitor.monitor.models.events.timestamp_delta.TimestampDelta;
import us.dot.its.jpo.conflictmonitor.monitor.serialization.JsonSerdes;
import us.dot.its.jpo.conflictmonitor.monitor.utils.SpatUtils;
import us.dot.its.jpo.geojsonconverter.partitioner.IntersectionIdPartitioner;
import us.dot.its.jpo.geojsonconverter.partitioner.RsuIntersectionKey;
import us.dot.its.jpo.geojsonconverter.pojos.spat.ProcessedSpat;

import static us.dot.its.jpo.conflictmonitor.monitor.algorithms.timestamp_delta.TimestampDeltaConstants.DEFAULT_SPAT_TIMESTAMP_DELTA_ALGORITHM;

@Component(DEFAULT_SPAT_TIMESTAMP_DELTA_ALGORITHM)
@Slf4j
public class SpatTimestampDeltaTopology
    extends BaseStreamsBuilder<SpatTimestampDeltaParameters>
    implements SpatTimestampDeltaStreamsAlgorithm {

    @Override
    protected Logger getLogger() {
        return log;
    }

    @Override
    public void buildTopology(KStream<RsuIntersectionKey, ProcessedSpat> inputStream) {
        inputStream
                // Ignore tombstones
                .filter((rsuIntersectionKey, processedSpat) -> processedSpat != null)

                // Calculate timestamp deltas
                .mapValues((rsuIntersectionKey, processedSpat) -> {
                    TimestampDelta delta = new TimestampDelta();
                    delta.setMaxDeltaMillis(parameters.getMaxDeltaMilliseconds());
                    delta.setMessageTimestampMillis(SpatUtils.getTimestamp(processedSpat));
                    delta.setOdeIngestTimestampMillis(SpatUtils.getOdeReceivedAt(processedSpat));
                    if (parameters.isDebug()) {
                        log.debug("RSU: {}, TimestampDelta: {}", rsuIntersectionKey.getRsuId(), delta);
                    }
                    return delta;
                })

                // Filter out small deltas
                .filter((rsuIntersectionKey, timestampDelta) -> timestampDelta.emitEvent())

                // Create even
                .mapValues((rsuIntersectionKey, timestampDelta) -> {
                    SpatTimestampDeltaEvent event = new SpatTimestampDeltaEvent();
                    event.setDelta(timestampDelta);
                    event.setSource(rsuIntersectionKey.getRsuId());
                    event.setIntersectionID(rsuIntersectionKey.getIntersectionId());
                    event.setRoadRegulatorID(rsuIntersectionKey.getRegion());
                    if (parameters.isDebug()) {
                        log.info("Producing TimestampDeltaEvent: {}", event);
                    }
                    return event;
                })

                // Output events to topic
                .to(parameters.getOutputTopicName(),
                        Produced.with(
                                us.dot.its.jpo.geojsonconverter.serialization.JsonSerdes.RsuIntersectionKey(),
                                JsonSerdes.SpatTimestampDeltaEvent(),
                                new IntersectionIdPartitioner<>()   // Don't change partitioning of output
                        ));
    }
}

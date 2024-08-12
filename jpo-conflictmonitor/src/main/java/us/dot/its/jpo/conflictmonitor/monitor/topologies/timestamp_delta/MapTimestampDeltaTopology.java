package us.dot.its.jpo.conflictmonitor.monitor.topologies.timestamp_delta;

import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.streams.kstream.KStream;
import org.apache.kafka.streams.kstream.Produced;
import org.slf4j.Logger;
import org.springframework.stereotype.Component;
import us.dot.its.jpo.conflictmonitor.monitor.algorithms.BaseStreamsBuilder;
import us.dot.its.jpo.conflictmonitor.monitor.algorithms.timestamp_delta.map.MapTimestampDeltaParameters;
import us.dot.its.jpo.conflictmonitor.monitor.algorithms.timestamp_delta.map.MapTimestampDeltaStreamsAlgorithm;
import us.dot.its.jpo.conflictmonitor.monitor.models.events.timestamp_delta.MapTimestampDeltaEvent;
import us.dot.its.jpo.conflictmonitor.monitor.models.events.timestamp_delta.TimestampDelta;
import us.dot.its.jpo.conflictmonitor.monitor.serialization.JsonSerdes;
import us.dot.its.jpo.conflictmonitor.monitor.utils.ProcessedMapUtils;
import us.dot.its.jpo.geojsonconverter.partitioner.IntersectionIdPartitioner;
import us.dot.its.jpo.geojsonconverter.partitioner.RsuIntersectionKey;
import us.dot.its.jpo.geojsonconverter.pojos.geojson.LineString;
import us.dot.its.jpo.geojsonconverter.pojos.geojson.map.ProcessedMap;

import static us.dot.its.jpo.conflictmonitor.monitor.algorithms.timestamp_delta.TimestampDeltaConstants.DEFAULT_MAP_TIMESTAMP_DELTA_ALGORITHM;

@Component(DEFAULT_MAP_TIMESTAMP_DELTA_ALGORITHM)
@Slf4j
public class MapTimestampDeltaTopology
    extends BaseStreamsBuilder<MapTimestampDeltaParameters>
    implements MapTimestampDeltaStreamsAlgorithm {

    @Override
    protected Logger getLogger() {
        return log;
    }

    @Override
    public void buildTopology(KStream<RsuIntersectionKey, ProcessedMap<LineString>> inputStream) {

        KStream<RsuIntersectionKey, MapTimestampDeltaEvent> eventStream =
            inputStream
                // Ignore tombstones
                .filter((rsuIntersectionKey, processedMap) -> processedMap != null)

                // Calculate timestamp delta
                .mapValues((rsuIntersectionKey, processedMap) -> {
                    TimestampDelta delta = new TimestampDelta();
                    delta.setMaxDeltaMilliseconds(parameters.getMaxDeltaMilliseconds());
                    delta.setMessageTimestampMilliseconds(ProcessedMapUtils.getTimestamp(processedMap));
                    delta.setOdeIngestTimestampMilliseconds(ProcessedMapUtils.getOdeReceivedAt(processedMap));
                    if (parameters.isDebug()) {
                        log.debug("RSU: {}, TimestampDelta: {}", rsuIntersectionKey.getRsuId(), delta);
                    }
                    return delta;
                })

                // Filter out small deltas
                .filter((rsuIntersectionKey, timestampDelta) -> timestampDelta.emitEvent())

                // Create Event
                .mapValues((rsuIntersectionKey, timestampDelta) -> {
                    MapTimestampDeltaEvent event = new MapTimestampDeltaEvent();
                    event.setDelta(timestampDelta);
                    event.setSource(rsuIntersectionKey.getRsuId());
                    event.setIntersectionID(rsuIntersectionKey.getIntersectionId());
                    event.setRoadRegulatorID(rsuIntersectionKey.getRegion());
                    if (parameters.isDebug()) {
                        log.info("Producing TimestampDeltaEvent: {}", event);
                    }
                    return event;
                });

        // Output events
        eventStream.to(parameters.getOutputTopicName(), Produced.with(
                us.dot.its.jpo.geojsonconverter.serialization.JsonSerdes.RsuIntersectionKey(),
                JsonSerdes.MapTimestampDeltaEvent(),
                new IntersectionIdPartitioner<>()));    // Don't change partitioning of output

//        // Collect events to issue hourly summary notifications
//        eventStream.toTable();
    }
}

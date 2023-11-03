package us.dot.its.jpo.conflictmonitor.monitor.algorithms.message_ingest;

import org.apache.kafka.streams.KafkaStreams;
import org.apache.kafka.streams.StreamsBuilder;
import org.apache.kafka.streams.state.ReadOnlyKeyValueStore;
import org.apache.kafka.streams.state.ReadOnlyWindowStore;

import us.dot.its.jpo.conflictmonitor.monitor.models.bsm.BsmRsuIdKey;
import us.dot.its.jpo.geojsonconverter.partitioner.RsuIntersectionKey;
import us.dot.its.jpo.geojsonconverter.pojos.geojson.LineString;
import us.dot.its.jpo.geojsonconverter.pojos.geojson.map.ProcessedMap;
import us.dot.its.jpo.geojsonconverter.pojos.spat.ProcessedSpat;
import us.dot.its.jpo.ode.model.OdeBsmData;

public interface MessageIngestStreamsAlgorithm
    extends MessageIngestAlgorithm {

    ReadOnlyWindowStore<BsmRsuIdKey, OdeBsmData> getBsmWindowStore(KafkaStreams streams);
    ReadOnlyWindowStore<RsuIntersectionKey, ProcessedSpat> getSpatWindowStore(KafkaStreams streams);
    ReadOnlyKeyValueStore<RsuIntersectionKey, ProcessedMap<LineString>> getMapStore(KafkaStreams streams);

    StreamsBuilder buildTopology(StreamsBuilder builder);
}

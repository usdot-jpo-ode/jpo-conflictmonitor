package us.dot.its.jpo.conflictmonitor.monitor.algorithms.timestamp_delta.map;

import org.apache.kafka.streams.StreamsBuilder;
import org.apache.kafka.streams.kstream.KStream;
import us.dot.its.jpo.geojsonconverter.partitioner.RsuIntersectionKey;
import us.dot.its.jpo.geojsonconverter.pojos.geojson.LineString;
import us.dot.its.jpo.geojsonconverter.pojos.geojson.map.ProcessedMap;

public interface MapTimestampDeltaStreamsAlgorithm
        extends MapTimestampDeltaAlgorithm {

    void buildTopology(StreamsBuilder builder, KStream<RsuIntersectionKey, ProcessedMap<LineString>> inputStream);

}

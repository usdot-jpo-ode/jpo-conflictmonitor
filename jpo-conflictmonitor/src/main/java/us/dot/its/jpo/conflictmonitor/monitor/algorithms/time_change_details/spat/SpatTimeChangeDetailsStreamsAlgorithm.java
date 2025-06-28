package us.dot.its.jpo.conflictmonitor.monitor.algorithms.time_change_details.spat;

import org.apache.kafka.streams.StreamsBuilder;
import org.apache.kafka.streams.kstream.KStream;
import org.apache.kafka.streams.kstream.KTable;
import us.dot.its.jpo.geojsonconverter.partitioner.RsuIntersectionKey;
import us.dot.its.jpo.geojsonconverter.pojos.geojson.LineString;
import us.dot.its.jpo.geojsonconverter.pojos.geojson.map.ProcessedMap;
import us.dot.its.jpo.geojsonconverter.pojos.spat.ProcessedSpat;

/**
 * SPAT Time Change Details - Streams implementation.
 * Plugs into {@link us.dot.its.jpo.conflictmonitor.monitor.algorithms.map_spat_message_assessment.MapSpatMessageAssessmentStreamsAlgorithm}
 */
public interface SpatTimeChangeDetailsStreamsAlgorithm
    extends SpatTimeChangeDetailsAlgorithm {

    /**
     * Use ProcessedSpat and ProcessedMap table.
     * ProcessedMaps are not used if missing, and checked if the Spat contains enabled lanes.
     * @param builder Stream Builder
     * @param spatStream ProcessedSpat stream
     * @param mapTable MAP KTable keyed by RsuIntersectionKey
     */
    void buildTopology(StreamsBuilder builder,
                       KStream<RsuIntersectionKey, ProcessedSpat> spatStream,
                       KTable<RsuIntersectionKey, ProcessedMap<LineString>> mapTable);
}
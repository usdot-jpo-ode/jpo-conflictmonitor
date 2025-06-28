package us.dot.its.jpo.conflictmonitor.monitor.algorithms.time_change_details;

import org.apache.kafka.streams.StreamsBuilder;
import org.apache.kafka.streams.kstream.KStream;
import org.apache.kafka.streams.kstream.KTable;
import us.dot.its.jpo.conflictmonitor.monitor.algorithms.aggregation.time_change_details.TimeChangeDetailsAggregationAlgorithm;
import us.dot.its.jpo.conflictmonitor.monitor.algorithms.time_change_details.spat.SpatTimeChangeDetailsParameters;
import us.dot.its.jpo.conflictmonitor.monitor.algorithms.time_change_details.spat.SpatTimeChangeDetailsStreamsAlgorithm;
import us.dot.its.jpo.geojsonconverter.partitioner.RsuIntersectionKey;
import us.dot.its.jpo.geojsonconverter.pojos.geojson.LineString;
import us.dot.its.jpo.geojsonconverter.pojos.geojson.map.ProcessedMap;
import us.dot.its.jpo.geojsonconverter.pojos.spat.ProcessedSpat;

/**
 * Mock of the SPAT Time Change Details subtopology that does nothing for testing other parts
 * of the MAP/SPAT alignment topology.
 */
public class MockSpatTimeChangeDetailsStreamsAlgorithm
        implements SpatTimeChangeDetailsStreamsAlgorithm {
    @Override
    public void buildTopology(StreamsBuilder builder, KStream<RsuIntersectionKey, ProcessedSpat> spatStream, KTable<RsuIntersectionKey, ProcessedMap<LineString>> mapTable) {
        // Do Nothing
    }

    @Override
    public void setAggregationAlgorithm(TimeChangeDetailsAggregationAlgorithm aggregationAlgorithm) {
        // Do nothing
    }

    @Override
    public void setParameters(SpatTimeChangeDetailsParameters spatTimeChangeDetailsParameters) {
        // Do nothing
    }

    @Override
    public SpatTimeChangeDetailsParameters getParameters() {
        return new SpatTimeChangeDetailsParameters();
    }
}

package us.dot.its.jpo.conflictmonitor.monitor.topologies;

import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.streams.StreamsBuilder;
import org.apache.kafka.streams.kstream.KStream;
import org.slf4j.Logger;
import org.springframework.stereotype.Component;
import us.dot.its.jpo.conflictmonitor.monitor.algorithms.BaseStreamsBuilder;
import us.dot.its.jpo.conflictmonitor.monitor.algorithms.aggregation.revocable_enabled_lane_alignment.RevocableEnabledLaneAlignmentAggregationAlgorithm;
import us.dot.its.jpo.conflictmonitor.monitor.algorithms.aggregation.revocable_enabled_lane_alignment.RevocableEnabledLaneAlignmentAggregationStreamsAlgorithm;
import us.dot.its.jpo.conflictmonitor.monitor.algorithms.revocable_enabled_lane_alignment.RevocableEnabledLaneAlignmentParameters;
import us.dot.its.jpo.conflictmonitor.monitor.algorithms.revocable_enabled_lane_alignment.RevocableEnabledLaneAlignmentStreamsAlgorithm;
import us.dot.its.jpo.conflictmonitor.monitor.models.SpatMap;
import us.dot.its.jpo.geojsonconverter.partitioner.RsuIntersectionKey;
import us.dot.its.jpo.geojsonconverter.pojos.geojson.LineString;
import us.dot.its.jpo.geojsonconverter.pojos.geojson.map.MapFeature;
import us.dot.its.jpo.geojsonconverter.pojos.geojson.map.MapFeatureCollection;
import us.dot.its.jpo.geojsonconverter.pojos.geojson.map.MapProperties;
import us.dot.its.jpo.geojsonconverter.pojos.geojson.map.ProcessedMap;
import us.dot.its.jpo.ode.plugin.j2735.J2735LaneTypeAttributes;

import static us.dot.its.jpo.conflictmonitor.monitor.algorithms.revocable_enabled_lane_alignment.RevocableEnabledLaneAlignmentConstants.DEFAULT_REVOCABLE_ENABLED_LANE_ALIGNMENT_ALGORITHM;

@Component(DEFAULT_REVOCABLE_ENABLED_LANE_ALIGNMENT_ALGORITHM)
@Slf4j
public class RevocableEnabledLaneAlignmentTopology
    extends BaseStreamsBuilder<RevocableEnabledLaneAlignmentParameters>
    implements RevocableEnabledLaneAlignmentStreamsAlgorithm {

    @Override
    protected Logger getLogger() {
        return log;
    }

    RevocableEnabledLaneAlignmentAggregationStreamsAlgorithm aggregationAlgorithm;

    @Override
    public void buildTopology(StreamsBuilder builder, KStream<RsuIntersectionKey, SpatMap> spatMapStream) {

        // Pass only event that have revocable lanes
        spatMapStream.filter((rsuIntersectionKey, spatMap) -> {
            ProcessedMap<LineString> map = spatMap.getMap();
            MapFeatureCollection<LineString> mfc = map.getMapFeatureCollection();
            for (MapFeature<LineString> feature : mfc.getFeatures()) {
                MapProperties mapProps = feature.getProperties();
                J2735LaneTypeAttributes laneTypeAttributes = mapProps.getLaneType();
                laneTypeAttributes.get
            }
        });

    }

    @Override
    public void setAggregationAlgorithm(RevocableEnabledLaneAlignmentAggregationAlgorithm aggregationAlgorithm) {
        // Enforce the algorithm being a Streams algorithm
        if (aggregationAlgorithm instanceof RevocableEnabledLaneAlignmentAggregationStreamsAlgorithm) {
            this.aggregationAlgorithm = (RevocableEnabledLaneAlignmentAggregationStreamsAlgorithm) aggregationAlgorithm;
        } else {
            throw new IllegalArgumentException("Aggregation algorithm must be a streams algorithm");
        }
    }
}

package us.dot.its.jpo.conflictmonitor.monitor.algorithms.intersection_event;

import org.apache.kafka.streams.state.ReadOnlyKeyValueStore;
import org.apache.kafka.streams.state.ReadOnlyWindowStore;

import us.dot.its.jpo.conflictmonitor.monitor.algorithms.StreamsTopology;
import us.dot.its.jpo.conflictmonitor.monitor.models.bsm.BsmIntersectionIdKey;
import us.dot.its.jpo.geojsonconverter.partitioner.RsuIntersectionKey;
import us.dot.its.jpo.geojsonconverter.pojos.geojson.LineString;
import us.dot.its.jpo.geojsonconverter.pojos.geojson.Point;
import us.dot.its.jpo.geojsonconverter.pojos.geojson.bsm.ProcessedBsm;
import us.dot.its.jpo.geojsonconverter.pojos.geojson.map.ProcessedMap;
import us.dot.its.jpo.geojsonconverter.pojos.spat.ProcessedSpat;


public interface IntersectionEventStreamsAlgorithm 
    extends IntersectionEventAlgorithm, StreamsTopology { 

    ReadOnlyWindowStore<BsmIntersectionIdKey, ProcessedBsm<Point>> getBsmWindowStore();
    ReadOnlyWindowStore<RsuIntersectionKey, ProcessedSpat> getSpatWindowStore();
    ReadOnlyKeyValueStore<RsuIntersectionKey, ProcessedMap<LineString>> getMapStore();


}

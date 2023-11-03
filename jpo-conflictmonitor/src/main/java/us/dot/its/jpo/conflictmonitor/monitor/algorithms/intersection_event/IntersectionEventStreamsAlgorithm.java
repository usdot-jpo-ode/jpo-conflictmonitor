package us.dot.its.jpo.conflictmonitor.monitor.algorithms.intersection_event;

import org.apache.kafka.streams.KafkaStreams;
import org.apache.kafka.streams.state.ReadOnlyKeyValueStore;
import org.apache.kafka.streams.state.ReadOnlyWindowStore;

import us.dot.its.jpo.conflictmonitor.monitor.algorithms.StreamsTopology;
import us.dot.its.jpo.conflictmonitor.monitor.models.bsm.BsmIntersectionKey;
import us.dot.its.jpo.geojsonconverter.partitioner.RsuIntersectionKey;
import us.dot.its.jpo.geojsonconverter.pojos.geojson.LineString;
import us.dot.its.jpo.geojsonconverter.pojos.geojson.map.ProcessedMap;
import us.dot.its.jpo.geojsonconverter.pojos.spat.ProcessedSpat;
import us.dot.its.jpo.ode.model.OdeBsmData;

public interface IntersectionEventStreamsAlgorithm 
    extends IntersectionEventAlgorithm, StreamsTopology { 

    ReadOnlyWindowStore<BsmIntersectionKey, OdeBsmData> getBsmWindowStore();
    ReadOnlyWindowStore<RsuIntersectionKey, ProcessedSpat> getSpatWindowStore();
    ReadOnlyKeyValueStore<RsuIntersectionKey, ProcessedMap<LineString>> getMapStore();


}

package us.dot.its.jpo.conflictmonitor.monitor.algorithms.message_ingest;

import org.apache.kafka.streams.state.ReadOnlyKeyValueStore;
import org.apache.kafka.streams.state.ReadOnlyWindowStore;

import us.dot.its.jpo.conflictmonitor.monitor.algorithms.StreamsTopology;
import us.dot.its.jpo.geojsonconverter.pojos.geojson.map.ProcessedMap;
import us.dot.its.jpo.geojsonconverter.pojos.spat.ProcessedSpat;
import us.dot.its.jpo.ode.model.OdeBsmData;

public interface MessageIngestStreamsAlgorithm
    extends MessageIngestAlgorithm, StreamsTopology {

    ReadOnlyWindowStore<String, OdeBsmData> getBsmWindowStore();
    ReadOnlyWindowStore<String, ProcessedSpat> getSpatWindowStore();
    ReadOnlyKeyValueStore<String, ProcessedMap> getMapStore();
    
}

package us.dot.its.jpo.conflictmonitor.monitor.algorithms.intersection_event;

import org.apache.kafka.streams.state.ReadOnlyKeyValueStore;
import org.apache.kafka.streams.state.ReadOnlyWindowStore;

import us.dot.its.jpo.conflictmonitor.monitor.algorithms.StreamsTopology;
import us.dot.its.jpo.geojsonconverter.pojos.geojson.map.ProcessedMap;
import us.dot.its.jpo.geojsonconverter.pojos.spat.ProcessedSpat;
import us.dot.its.jpo.ode.model.OdeBsmData;

public interface IntersectionEventStreamsAlgorithm 
    extends IntersectionEventAlgorithm, StreamsTopology { 

    ReadOnlyWindowStore<String, OdeBsmData> getBsmWindowStore();
    void setBsmWindowStore(ReadOnlyWindowStore<String, OdeBsmData> bsmStore);
    ReadOnlyWindowStore<String, ProcessedSpat> getSpatWindowStore();
    void setSpatWindowStore(ReadOnlyWindowStore<String, ProcessedSpat> spatStore);
    ReadOnlyKeyValueStore<String, ProcessedMap> getMapStore();
    void setMapStore(ReadOnlyKeyValueStore<String, ProcessedMap> mapStore);

}

package us.dot.its.jpo.conflictmonitor.monitor.algorithms.bsm_event;

import org.apache.kafka.streams.Topology;
import us.dot.its.jpo.conflictmonitor.monitor.algorithms.StreamsTopology;


public interface BsmEventStreamsAlgorithm
    extends BsmEventAlgorithm {

    /**
     * Add BSM Event algorithm to an existing topology
     * @param topology
     * @return
     */
    Topology buildTopology(Topology topology);
}

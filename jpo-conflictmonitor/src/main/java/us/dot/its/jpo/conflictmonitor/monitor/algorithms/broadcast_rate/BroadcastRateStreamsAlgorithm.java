package us.dot.its.jpo.conflictmonitor.monitor.algorithms.broadcast_rate;

import us.dot.its.jpo.conflictmonitor.monitor.algorithms.StreamsTopology;

public interface BroadcastRateStreamsAlgorithm<TParameters extends BroadcastRateParameters> 
    extends BroadcastRateAlgorithm<TParameters>, StreamsTopology {}


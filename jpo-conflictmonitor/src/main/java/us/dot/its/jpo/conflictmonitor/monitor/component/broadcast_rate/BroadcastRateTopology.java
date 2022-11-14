package us.dot.its.jpo.conflictmonitor.monitor.component.broadcast_rate;

import org.apache.kafka.streams.Topology;

public interface BroadcastRateTopology {

    
    Topology build(BroadcastRateParameters params);

 
}
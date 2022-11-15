package us.dot.its.jpo.conflictmonitor.monitor.algorithms.broadcast_rate.map;



public interface MapBroadcastRateAlgorithmFactory  {

    MapBroadcastRateAlgorithm getAlgorithm(String algorithmName);
 
}

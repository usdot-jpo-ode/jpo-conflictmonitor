package us.dot.its.jpo.conflictmonitor.monitor.broadcast_rate;



public interface MapBroadcastRateAlgorithmFactory  {

    MapBroadcastRateAlgorithm getAlgorithm(String algorithmName);
 
}

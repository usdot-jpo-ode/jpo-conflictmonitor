package us.dot.its.jpo.conflictmonitor.monitor.topologies.broadcast_rate;

import us.dot.its.jpo.conflictmonitor.monitor.algorithms.broadcast_rate.spat.SpatBroadcastRateParameters;
import us.dot.its.jpo.conflictmonitor.monitor.algorithms.broadcast_rate.spat.SpatBroadcastRateStreamsAlgorithm;

import static us.dot.its.jpo.conflictmonitor.monitor.algorithms.broadcast_rate.BroadcastRateConstants.*;

import java.util.Properties;

import org.apache.kafka.streams.KafkaStreams;
import org.springframework.stereotype.Component;

@Component(DEFAULT_SPAT_BROADCAST_RATE_ALGORITHM)
public class SpatBroadcastRateTopology implements SpatBroadcastRateStreamsAlgorithm {

    @Override
    public void setParameters(SpatBroadcastRateParameters parameters) {
        // TODO Auto-generated method stub
        
    }

    @Override
    public void start() {
        // TODO Auto-generated method stub
        
    }

    @Override
    public void stop() {
        // TODO Auto-generated method stub
        
    }

    @Override
    public SpatBroadcastRateParameters getParameters() {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public void setStreamsProperties(Properties streamsProperties) {
        // TODO Auto-generated method stub
        
    }

    @Override
    public Properties getStreamsProperties() {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public KafkaStreams getStreams() {
        // TODO Auto-generated method stub
        return null;
    }
    
}

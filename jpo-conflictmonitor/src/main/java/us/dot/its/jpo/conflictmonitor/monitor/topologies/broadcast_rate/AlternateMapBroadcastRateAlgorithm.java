package us.dot.its.jpo.conflictmonitor.monitor.topologies.broadcast_rate;

import java.util.Timer;
import java.util.TimerTask;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import us.dot.its.jpo.conflictmonitor.monitor.algorithms.broadcast_rate.map.MapBroadcastRateAlgorithm;
import us.dot.its.jpo.conflictmonitor.monitor.algorithms.broadcast_rate.map.MapBroadcastRateParameters;
import us.dot.its.jpo.conflictmonitor.monitor.algorithms.broadcast_rate.BroadcastRateConstants;

/**
 * Test algorithm just writes random numbers to the log
 */
@Component(BroadcastRateConstants.ALTERNATE_MAP_BROADCAST_RATE_ALGORITHM)
public class AlternateMapBroadcastRateAlgorithm implements MapBroadcastRateAlgorithm {

    private static final Logger logger = LoggerFactory.getLogger(AlternateMapBroadcastRateAlgorithm.class);

    MapBroadcastRateParameters parameters;
    Timer timer;

    @Override
    public void setParameters(MapBroadcastRateParameters parameters) {
        this.parameters = parameters;
    }

    @Override
    public MapBroadcastRateParameters getParameters() {
        return parameters;
    }

    @Override
    public void start() {
        timer = new Timer();
        timer.scheduleAtFixedRate(new TimerTask() {

            @Override
            public void run() {
                int counts = (int)((Math.random() * 4) + 8); // Random number between 8-12
                logger.info("Fake MAP Count: {}", counts);
                if (counts < parameters.getLowerBound() || counts > parameters.getUpperBound()) {
                    logger.info("Fake MAP Broadcast Rate Event.");
                }
            }

        }, 
        0, 
        1000 * parameters.getOutputIntervalSeconds());
    }

    @Override
    public void stop() {
        if (timer != null) {
            timer.cancel();
            timer = null;
        }
        
    }
    
}

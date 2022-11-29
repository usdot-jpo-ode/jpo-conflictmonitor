package us.dot.its.jpo.conflictmonitor.monitor.topologies.broadcast_rate;

import java.util.Timer;
import java.util.TimerTask;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import static us.dot.its.jpo.conflictmonitor.monitor.algorithms.broadcast_rate.BroadcastRateConstants.*;
import us.dot.its.jpo.conflictmonitor.monitor.algorithms.broadcast_rate.spat.SpatBroadcastRateAlgorithm;
import us.dot.its.jpo.conflictmonitor.monitor.algorithms.broadcast_rate.spat.SpatBroadcastRateParameters;

/**
 * Test SPAT algorithm, writes random numbers to the log in debug mode.
 */
@Component(ALTERNATE_SPAT_BROADCAST_RATE_ALGORITHM)
public class AlternateSpatBroadcastRateAlgorithm
    implements SpatBroadcastRateAlgorithm {
    
        private static final Logger logger = LoggerFactory.getLogger(AlternateSpatBroadcastRateAlgorithm.class);

    SpatBroadcastRateParameters parameters;
    Timer timer;

    @Override
    public void setParameters(SpatBroadcastRateParameters parameters) {
        this.parameters = parameters;
    }

    @Override
    public SpatBroadcastRateParameters getParameters() {
        return parameters;
    }

    @Override
    public void start() {
        // Don't run if not debugging
        if (!parameters.isDebug()) return;

        timer = new Timer();
        timer.scheduleAtFixedRate(new TimerTask() {

            @Override
            public void run() {
                int counts = (int)((Math.random() * 40) + 80); // Random number between 80-120
                logger.info("Fake SPAT Count: {}", counts);
                if (counts < parameters.getLowerBound() || counts > parameters.getUpperBound()) {
                    logger.info("Fake SPAT Broadcast Rate Event.");
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

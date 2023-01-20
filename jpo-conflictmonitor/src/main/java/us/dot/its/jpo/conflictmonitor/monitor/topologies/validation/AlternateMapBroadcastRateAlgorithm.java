package us.dot.its.jpo.conflictmonitor.monitor.topologies.validation;

import static us.dot.its.jpo.conflictmonitor.monitor.algorithms.validation.ValidationConstants.*;

import java.util.Timer;
import java.util.TimerTask;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import us.dot.its.jpo.conflictmonitor.monitor.algorithms.validation.map.MapValidationAlgorithm;
import us.dot.its.jpo.conflictmonitor.monitor.algorithms.validation.map.MapValidationParameters;

/**
 * Test algorithm just writes random numbers to the log
 */
@Component(ALTERNATE_MAP_VALIDATION_ALGORITHM)
public class AlternateMapBroadcastRateAlgorithm implements MapValidationAlgorithm {

    private static final Logger logger = LoggerFactory.getLogger(AlternateMapBroadcastRateAlgorithm.class);

    MapValidationParameters parameters;
    Timer timer;

    @Override
    public void setParameters(MapValidationParameters parameters) {
        this.parameters = parameters;
    }

    @Override
    public MapValidationParameters getParameters() {
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

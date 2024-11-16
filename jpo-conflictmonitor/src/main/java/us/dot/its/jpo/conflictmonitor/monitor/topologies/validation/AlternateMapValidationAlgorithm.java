package us.dot.its.jpo.conflictmonitor.monitor.topologies.validation;

import static us.dot.its.jpo.conflictmonitor.monitor.algorithms.validation.ValidationConstants.*;

import java.util.Timer;
import java.util.TimerTask;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import us.dot.its.jpo.conflictmonitor.monitor.algorithms.aggregation.validation.map.MapMinimumDataAggregationAlgorithm;
import us.dot.its.jpo.conflictmonitor.monitor.algorithms.timestamp_delta.map.MapTimestampDeltaAlgorithm;
import us.dot.its.jpo.conflictmonitor.monitor.algorithms.validation.map.MapValidationAlgorithm;
import us.dot.its.jpo.conflictmonitor.monitor.algorithms.validation.map.MapValidationParameters;
import us.dot.its.jpo.conflictmonitor.monitor.topologies.timestamp_delta.AlternateMapTimestampDeltaAlgorithm;

/**
 * Test algorithm just writes random numbers to the log
 */
@Component(ALTERNATE_MAP_VALIDATION_ALGORITHM)
public class AlternateMapValidationAlgorithm implements MapValidationAlgorithm {

    private static final Logger logger = LoggerFactory.getLogger(AlternateMapValidationAlgorithm.class);

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

        // Plugin algorithm
        timestampDeltaAlgorithm.doNothing();

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


    AlternateMapTimestampDeltaAlgorithm timestampDeltaAlgorithm;


    @Override
    public MapTimestampDeltaAlgorithm getTimestampDeltaAlgorithm() {
        return timestampDeltaAlgorithm;
    }

    @Override
    public void setTimestampDeltaAlgorithm(MapTimestampDeltaAlgorithm timestampDeltaAlgorithm) {
        // Enforce a specific algorithm implementation
        if (timestampDeltaAlgorithm instanceof AlternateMapTimestampDeltaAlgorithm altAlgorithm) {
            this.timestampDeltaAlgorithm = altAlgorithm;
        } else {
            throw new IllegalArgumentException("Algorithm is not an instance of AlternateMapTimestampDeltaAlgorithm");
        }
    }

    @Override
    public void setMinimumDataAggregationAlgorithm(MapMinimumDataAggregationAlgorithm mapMinimumDataAggregationAlgorithm) {
        // Not used
    }
}

package us.dot.its.jpo.conflictmonitor.monitor.topologies.validation;

import static us.dot.its.jpo.conflictmonitor.monitor.algorithms.validation.ValidationConstants.*;

import java.util.Timer;
import java.util.TimerTask;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import us.dot.its.jpo.conflictmonitor.monitor.algorithms.aggregation.validation.spat.SpatMinimumDataAggregationAlgorithm;
import us.dot.its.jpo.conflictmonitor.monitor.algorithms.timestamp_delta.spat.SpatTimestampDeltaAlgorithm;
import us.dot.its.jpo.conflictmonitor.monitor.algorithms.validation.spat.SpatValidationAlgorithm;
import us.dot.its.jpo.conflictmonitor.monitor.algorithms.validation.spat.SpatValidationParameters;
import us.dot.its.jpo.conflictmonitor.monitor.topologies.timestamp_delta.AlternateSpatTimestampDeltaAlgorithm;

/**
 * Test SPAT algorithm, writes random numbers to the log in debug mode.
 */
@Component(ALTERNATE_SPAT_VALIDATION_ALGORITHM)
public class AlternateSpatValidationAlgorithm
    implements SpatValidationAlgorithm {
    
        private static final Logger logger = LoggerFactory.getLogger(AlternateSpatValidationAlgorithm.class);

    SpatValidationParameters parameters;
    Timer timer;

    @Override
    public void setParameters(SpatValidationParameters parameters) {
        this.parameters = parameters;
    }

    @Override
    public SpatValidationParameters getParameters() {
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

    AlternateSpatTimestampDeltaAlgorithm timestampDeltaAlgorithm;


    @Override
    public SpatTimestampDeltaAlgorithm getTimestampDeltaAlgorithm() {
        return timestampDeltaAlgorithm;
    }

    @Override
    public void setTimestampDeltaAlgorithm(SpatTimestampDeltaAlgorithm timestampDeltaAlgorithm) {
        // Enforce a specific algorithm implementation
        if (timestampDeltaAlgorithm instanceof AlternateSpatTimestampDeltaAlgorithm altAlgorithm) {
            this.timestampDeltaAlgorithm = altAlgorithm;
        } else {
            throw new IllegalArgumentException("Algorithm is not an instance of AlternateSpatTimestampDeltaAlgorithm");
        }
    }

    @Override
    public void setMinimumDataAggregationAlgorithm(SpatMinimumDataAggregationAlgorithm spatMinimumDataAggregationAlgorithm) {
        // Not used
    }
}

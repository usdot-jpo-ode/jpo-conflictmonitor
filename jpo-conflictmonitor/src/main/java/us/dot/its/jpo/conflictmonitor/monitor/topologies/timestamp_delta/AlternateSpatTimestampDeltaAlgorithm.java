package us.dot.its.jpo.conflictmonitor.monitor.topologies.timestamp_delta;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import us.dot.its.jpo.conflictmonitor.monitor.algorithms.timestamp_delta.spat.SpatTimestampDeltaAlgorithm;
import us.dot.its.jpo.conflictmonitor.monitor.algorithms.timestamp_delta.spat.SpatTimestampDeltaParameters;

import static us.dot.its.jpo.conflictmonitor.monitor.algorithms.timestamp_delta.TimestampDeltaConstants.ALTERNATE_SPAT_TIMESTAMP_DELTA_ALGORITHM;

// Alternate algorithm that does nothing, for demonstrating the modular architecture
@Component(ALTERNATE_SPAT_TIMESTAMP_DELTA_ALGORITHM)
@Slf4j
public class AlternateSpatTimestampDeltaAlgorithm implements SpatTimestampDeltaAlgorithm {

    SpatTimestampDeltaParameters parameters;

    @Override
    public void setParameters(SpatTimestampDeltaParameters spatTimestampDeltaParameters) {
        this.parameters = spatTimestampDeltaParameters;
    }

    @Override
    public SpatTimestampDeltaParameters getParameters() {
        return parameters;
    }

    public void doNothing() {
        log.info("Doing nothing");
    }
}

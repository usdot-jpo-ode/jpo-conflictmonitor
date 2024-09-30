package us.dot.its.jpo.conflictmonitor.monitor.topologies.timestamp_delta;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import us.dot.its.jpo.conflictmonitor.monitor.algorithms.timestamp_delta.map.MapTimestampDeltaAlgorithm;
import us.dot.its.jpo.conflictmonitor.monitor.algorithms.timestamp_delta.map.MapTimestampDeltaParameters;

import static us.dot.its.jpo.conflictmonitor.monitor.algorithms.timestamp_delta.TimestampDeltaConstants.ALTERNATE_MAP_TIMESTAMP_DELTA_ALGORITHM;

// Alternate algorithm that does nothing, for demonstrating the modular architecture
@Component(ALTERNATE_MAP_TIMESTAMP_DELTA_ALGORITHM)
@Slf4j
public class AlternateMapTimestampDeltaAlgorithm implements MapTimestampDeltaAlgorithm {

    MapTimestampDeltaParameters parameters;

    @Override
    public void setParameters(MapTimestampDeltaParameters mapTimestampDeltaParameters) {
        this.parameters = mapTimestampDeltaParameters;
    }

    @Override
    public MapTimestampDeltaParameters getParameters() {
        return parameters;
    }

    public void doNothing() {
        log.info("Doing nothing");
    }
}

package us.dot.its.jpo.conflictmonitor.monitor.algorithms.signal_state_vehicle_crosses;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import lombok.Data;
import lombok.Generated;

@Data
@Generated
@Component
@ConfigurationProperties(prefix = "signal.state.vehicle.crosses")
public class SignalStateVehicleCrossesParameters {
    // Whether to log diagnostic information for debugging
    boolean debug;

    int spatBsmMatchWindowMillis;

}

package us.dot.its.jpo.conflictmonitor.monitor.algorithms.signal_state_vehicle_stops;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import lombok.Data;
import lombok.Generated;

@Data
@Generated
@Component
@ConfigurationProperties(prefix = "signal.state.vehicle.stops")
public class SignalStateVehicleStopsParameters {
    // Whether to log diagnostic information for debugging
    boolean debug;
    int spatBsmMatchWindowMillis;
    double stopSpeedThreshold;
}




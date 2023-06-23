package us.dot.its.jpo.conflictmonitor.monitor.algorithms.signal_state_vehicle_stops;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import lombok.Data;
import lombok.Generated;
import us.dot.its.jpo.conflictmonitor.monitor.models.config.ConfigData;
import us.dot.its.jpo.conflictmonitor.monitor.models.config.ConfigDataClass;
import us.dot.its.jpo.conflictmonitor.monitor.models.config.ConfigMap;

import static us.dot.its.jpo.conflictmonitor.monitor.models.config.UpdateType.*;
import static us.dot.its.jpo.conflictmonitor.monitor.models.config.UnitsEnum.*;
import static us.dot.its.jpo.conflictmonitor.monitor.algorithms.config.ConfigUtil.*;

@Data
@Generated
@Component
@ConfigurationProperties(prefix = "signal.state.vehicle.stops")
@ConfigDataClass
public class SignalStateVehicleStopsParameters {

    // Whether to log diagnostic information for debugging
    @ConfigData(key = "signal.state.vehicle.stops.debug", 
        description = "Whether to log diagnostic information for debugging",
        updateType = DEFAULT)
    boolean debug;

    @ConfigData(key = "signal.state.vehicle.stops.spatBsmMatchWindowMillis", 
        description = "The number of milliseconds to wait for a SPAT and BSM to match", 
        units = MILLISECONDS,
        updateType = DEFAULT)
    int spatBsmMatchWindowMillis;

    @ConfigData(key = "signal.state.vehicle.stops.stopSpeedThreshold", 
        description = "The speed threshold below which a vehicle is considered stopped", 
        units = MILES_PER_HOUR,
        updateType = INTERSECTION)
    double stopSpeedThreshold;

    // Maps for parameters that can be customized at the intersection level
    final ConfigMap<Double> stopSpeedThresholdMap = new ConfigMap<>();

    // Intersection-specific parameters
    public double getStopSpeedThreshold(String rsuID) {
        return getIntersectionValue(rsuID, stopSpeedThresholdMap, stopSpeedThreshold);
    }
    


}




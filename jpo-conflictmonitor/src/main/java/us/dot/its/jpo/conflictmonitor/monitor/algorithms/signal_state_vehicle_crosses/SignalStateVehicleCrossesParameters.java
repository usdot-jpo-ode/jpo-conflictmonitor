package us.dot.its.jpo.conflictmonitor.monitor.algorithms.signal_state_vehicle_crosses;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import lombok.Data;
import lombok.Generated;
import us.dot.its.jpo.conflictmonitor.monitor.models.config.ConfigData;
import us.dot.its.jpo.conflictmonitor.monitor.models.config.ConfigDataClass;

import static us.dot.its.jpo.conflictmonitor.monitor.models.config.UnitsEnum.*;

import static us.dot.its.jpo.conflictmonitor.monitor.models.config.UpdateType.*;

@Data
@Generated
@Component
@ConfigurationProperties(prefix = "signal.state.vehicle.crosses")
@ConfigDataClass
public class SignalStateVehicleCrossesParameters {

    // Whether to log diagnostic information for debugging
    @ConfigData(key = "signal.state.vehicle.crosses.debug", 
        description = "Whether to log diagnostic information for debugging",
        updateType = DEFAULT)
    boolean debug;

    @ConfigData(key = "signal.state.vehicle.crosses.spatBsmMatchWindowMillis", 
        description = "The number of milliseconds to wait for a SPAT and BSM to match", 
        units = MILLISECONDS,
        updateType = DEFAULT)
    int spatBsmMatchWindowMillis;

}

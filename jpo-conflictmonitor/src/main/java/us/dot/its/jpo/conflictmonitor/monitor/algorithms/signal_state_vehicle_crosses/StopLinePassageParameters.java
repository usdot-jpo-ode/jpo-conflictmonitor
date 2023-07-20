package us.dot.its.jpo.conflictmonitor.monitor.algorithms.signal_state_vehicle_crosses;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import lombok.Data;
import lombok.Generated;
import us.dot.its.jpo.conflictmonitor.monitor.models.config.ConfigData;
import us.dot.its.jpo.conflictmonitor.monitor.models.config.ConfigDataClass;

import static us.dot.its.jpo.conflictmonitor.monitor.models.config.UnitsEnum.*;

import static us.dot.its.jpo.conflictmonitor.monitor.models.config.UpdateType.*;

/**
 * Stop Line Passage Event Configuration
 *
 * <p>See Table 15, CIMMS Software Design Document - FINAL, April 2020/p>
 */
@Data
@Generated
@Component
@ConfigurationProperties(prefix = "signal.state.vehicle.crosses")
@ConfigDataClass
public class StopLinePassageParameters {

    @ConfigData(key = "signal.state.vehicle.crosses.stopLineMinDistance",
        description = "Distance (ft) that the vehicle must pass within of the center of the stop line (end of ingress lane) for an event to be generated.",
        units = FEET,
        updateType = INTERSECTION)
    double stopLineMinDistance;

    @ConfigData(key = "signal.state.vehicle.crosses.headingTolerance",
        description = "Tolerance of the lane heading (decimal degrees) that the vehicle must be traveling within for an event to be generated.",
        units = DEGREES,
        updateType = INTERSECTION)
    double headingTolerance;

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

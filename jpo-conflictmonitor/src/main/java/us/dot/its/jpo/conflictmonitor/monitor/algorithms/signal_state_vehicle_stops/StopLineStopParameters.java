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
public class StopLineStopParameters {

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

    @ConfigData(key = "signal.state.vehicle.stops.upstreamSearchDistance",
            description = "Starting from the stop line and moving upstream, the length (ft) of the " +
                    "ingress lane a vehicle must come to a stop in for an event to be " +
                    "generated",
            units = FEET,
            updateType = INTERSECTION
    )
    double upstreamSearchDistance;

    @ConfigData(key = "signal.state.vehicle.stops.headingTolerance",
        description = "Tolerance of the lane heading (decimal degrees) that the vehicle must " +
                "be traveling within for an event to be generated.",
        units = DEGREES,
        updateType = INTERSECTION)
    double headingTolerance;

    @ConfigData(key = "signal.state.vehicle.stops.stopSpeedThreshold", 
        description = "The speed threshold below which a vehicle is considered stopped", 
        units = MILES_PER_HOUR,
        updateType = INTERSECTION)
    double stopSpeedThreshold;

    @ConfigData(key = "signal.state.vehicle.stops.minTimeStopped",
        description = "The minimum amount of time (seconds) (between the time a vehicle " +
                "initially comes to a stop until the last moment the vehicle is stopped) " +
                "for a Stop Line Stop event to be generated.",
        units = SECONDS,
        updateType = INTERSECTION)
    double minTimeStopped;

    // Maps for parameters that can be customized at the intersection level
    final ConfigMap<Double> upstreamSearchDistanceMap = new ConfigMap<>();
    final ConfigMap<Double> headingToleranceMap = new ConfigMap<>();
    final ConfigMap<Double> stopSpeedThresholdMap = new ConfigMap<>();
    final ConfigMap<Double> minTimeStoppedMap = new ConfigMap<>();

    // Intersection-specific parameters
    public double getUpstreamSearchDistance(String rsuID) {
        return getIntersectionValue(rsuID, upstreamSearchDistanceMap, upstreamSearchDistance);
    }

    public double getHeadingTolerance(String rsuID) {
        return getIntersectionValue(rsuID, headingToleranceMap, headingTolerance);
    }
    public double getStopSpeedThreshold(String rsuID) {
        return getIntersectionValue(rsuID, stopSpeedThresholdMap, stopSpeedThreshold);
    }

    public double getMinTimeStopped(String rsuID) {
        return getIntersectionValue(rsuID, minTimeStoppedMap, minTimeStopped);
    }

}




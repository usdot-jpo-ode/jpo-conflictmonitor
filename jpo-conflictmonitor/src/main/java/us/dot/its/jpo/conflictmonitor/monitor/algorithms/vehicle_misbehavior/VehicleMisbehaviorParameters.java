package us.dot.its.jpo.conflictmonitor.monitor.algorithms.vehicle_misbehavior;

import static us.dot.its.jpo.conflictmonitor.monitor.models.config.UpdateType.DEFAULT;
import static us.dot.its.jpo.conflictmonitor.monitor.models.config.UpdateType.READ_ONLY;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import lombok.Data;
import lombok.Generated;
import us.dot.its.jpo.conflictmonitor.monitor.models.config.ConfigData;
import us.dot.its.jpo.conflictmonitor.monitor.models.config.ConfigDataClass;
import us.dot.its.jpo.conflictmonitor.monitor.models.config.UnitsEnum;

@Data
@Generated
@Component
@ConfigurationProperties(prefix = "vehicle.misbehavior")
@ConfigDataClass
public class VehicleMisbehaviorParameters {

    @ConfigData(key = "vehicle.misbehavior.algorithm",
        description = "Algorithm to use for Vehicle Misbehavior",
        updateType = READ_ONLY)
    String algorithm;

    // Whether to log diagnostic information for debugging
    @ConfigData(key = "vehicle.misbehavior.debug", 
        description = "Whether to log diagnostic information for debugging", 
        updateType = DEFAULT)
    boolean debug;

    @ConfigData(key = "vehicle.misbehavior.acceleration_range.lateral", 
        description = "Threshold for generating an event due to an unrealistic acceleration. Measured in Feet per second squared.", 
        updateType = DEFAULT)
    double accelerationRangeLateral;

    @ConfigData(key = "vehicle.misbehavior.acceleration_range.longitudinal", 
        description = "Threshold for generating an event due to an unrealistic acceleration. Measured in Feet per second squared.", 
        updateType = DEFAULT)
    double accelerationRangeLongitudinal;

    @ConfigData(key = "vehicle.misbehavior.acceleration_range.vertical", 
        description = "Threshold for generating an event due to an unrealistic acceleration. Measured in Feet per second squared.", 
        updateType = DEFAULT)
    double accelerationRangeVertical;

    @ConfigData(key = "vehicle.misbehavior.speed_range", 
        description = "Threshold for generating an event do to unrealistic speed. Measured in Miles Per Hour.", 
        updateType = DEFAULT)
    double speedRange;

    @ConfigData(key = "vehicle.misbehavior.yaw_rate_range", 
        description = "Threshold for generating an event do to unrealistic change in heading. Measured in Degrees per second", 
        updateType = DEFAULT)
    double yawRateRange;

    @ConfigData(key = "vehicle.misbehavior.allowable_max_speed", 
        description = "Maximum allowable Speed. Measured in Miles per Hour", 
        updateType = DEFAULT)
    double allowableMaxSpeed;

    @ConfigData(key = "vehicle.misbehavior.allowable_max_heading_delta", 
        description = "Maximum allowable change in heading. Measured in Degrees per second", 
        updateType = DEFAULT)
    double allowableMaxHeadingDelta;

    @ConfigData(key = "vehicle.misbehavior.processedBsmStateStoreName",
        description = "Name of the versioned state store for the jitter buffer",
        updateType = READ_ONLY)
    volatile String processedBsmStateStoreName;

    @ConfigData(key = "vehicle.misbehavior.bsmInputTopicName", 
        description = "The name of the topic to read BSMs from", 
        updateType = READ_ONLY)
    String bsmInputTopicName;

    @ConfigData(key = "vehicle.misbehavior.vehicleMisbehaviorEventOutputTopicName", 
        description = "The name of the topic to write Vehicle Misbehavior events to", 
        updateType = READ_ONLY)
    String vehicleMisbehaviorEventOutputTopicName;

    
}

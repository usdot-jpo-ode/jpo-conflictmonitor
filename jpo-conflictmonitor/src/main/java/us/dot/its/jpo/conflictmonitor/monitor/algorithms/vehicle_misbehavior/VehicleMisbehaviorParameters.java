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

    @ConfigData(key = "vehicle.misbehavior.acceleration_range", 
        description = "Threshold for generating an event due to an unrealistic change in acceleration. Measured in Feet per second squared.", 
        updateType = DEFAULT)
    int accelerationRange;

    @ConfigData(key = "vehicle.misbehavior.speed_range", 
        description = "Threshold for generating an event do to unrealistic speed. Measured in Miles Per Hour.", 
        updateType = DEFAULT)
    int speedRange;

    @ConfigData(key = "vehicle.misbehavior.yaw_rate_range", 
        description = "Threshold for generating an event do to unrealistic change in heading. Measured in Degrees per second", 
        updateType = DEFAULT)
    int yawRateRange;

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

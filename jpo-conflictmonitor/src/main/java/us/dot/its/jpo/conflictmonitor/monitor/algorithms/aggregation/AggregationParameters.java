package us.dot.its.jpo.conflictmonitor.monitor.algorithms.aggregation;

import lombok.Data;
import lombok.Generated;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;
import us.dot.its.jpo.conflictmonitor.monitor.models.config.ConfigData;
import us.dot.its.jpo.conflictmonitor.monitor.models.config.ConfigDataClass;


import static us.dot.its.jpo.conflictmonitor.monitor.models.config.UpdateType.DEFAULT;

@Data
@Generated
@Component
@ConfigurationProperties(prefix = "aggregation")
@ConfigDataClass
public class AggregationParameters {

    @ConfigData(key = "aggregation.debug",
            description = "Whether to log diagnostic information for debugging",
            updateType = DEFAULT)
    boolean debug;

    @ConfigData(key = "aggregation.interval",
            description = "The time interval over which to aggregate events",
            updateType = DEFAULT)
    int interval;

    @ConfigData(key = "aggregation.interval.units",
            description = "The time units of the aggregation interval",
            updateType = DEFAULT)
    IntervalUnits intervalUnits;

    public enum IntervalUnits {
        SECONDS,
        MINUTES,
        HOURS
    }
}

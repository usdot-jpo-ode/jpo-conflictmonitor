package us.dot.its.jpo.conflictmonitor.monitor.algorithms.aggregation;

import com.google.common.collect.ImmutableSet;
import lombok.Data;
import lombok.Generated;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;
import us.dot.its.jpo.conflictmonitor.monitor.models.config.ConfigData;
import us.dot.its.jpo.conflictmonitor.monitor.models.config.ConfigDataClass;


import java.time.Duration;
import java.time.temporal.ChronoUnit;
import java.util.Set;

import static java.time.temporal.ChronoUnit.*;
import static us.dot.its.jpo.conflictmonitor.monitor.models.config.UpdateType.DEFAULT;
import static us.dot.its.jpo.conflictmonitor.monitor.models.config.UpdateType.READ_ONLY;

@Data
@Generated
@Component
@ConfigurationProperties(prefix = "aggregation")
@ConfigDataClass
@Slf4j
public class AggregationParameters {

    @ConfigData(key = "aggregation.debug",
            description = "Whether to log diagnostic information for debugging",
            updateType = DEFAULT)
    volatile boolean debug;

    @ConfigData(key = "aggregation.interval",
            description = "The time interval over which to aggregate events",
            updateType = DEFAULT)
    volatile int interval;

    @ConfigData(key = "aggregation.interval.units",
            description = "The time units of the aggregation interval",
            updateType = DEFAULT)
    volatile ChronoUnit intervalUnits;

    @ConfigData(key = "aggregation.eventTopicMap",
        description = "Map of aggregated event names to output topic names",
        updateType = READ_ONLY)
    EventTopicMap eventTopicMap;

    public Duration timeInterval() {
        return Duration.of(interval, intervalUnits);
    }

    /**
     * Preliminary Design Details, Data Management, p.9:
     * <pre>
     * The user shall only be able to select intervals that are
     * • a factor of 60 seconds (if less than 60 seconds)
     *     e.g. 1, 2, 3, 4, 5, 6, 10, 12, 15, 20, 30, 60 seconds
     * • OR a factor of 60 minutes (between 1 minute and 60 minutes)
     *     e.g. 1, 2, 3, 4, 5, 6, 10, 12, 15, 20, 30, 60 minutes
     * • OR a factor of 24 hours (between 1 hour and 24 hours)
     *     e.g. 1, 2, 3, 4, 6, 8, 24 hours
     * </pre>
     * @return valid or not
     */
    public boolean validateInterval() {
        if (!allowedIntervalUnits.contains(intervalUnits)) {
            log.error("Invalid interval units: {}, allowed values are: {}", intervalUnits, allowedIntervalUnits);
            return false;
        }

        if (intervalUnits.equals(HOURS)) {
            if (interval < 0 || 24 % interval != 0) {
                log.error("Invalid time interval: {}, allowed values are: 1, 2, 3, 4, 6, 8, 24 hours", interval);
                return false;
            } else {
                return true;
            }
        }

        // SECONDS or MINUTES
        if (interval < 0 || 60 % interval != 0) {
            log.error("Invalid time interval: {}, allowed values are: 1, 2, 3, 4, 5, 6, 10, 12, 15, 20, 30, 60 {}", interval, intervalUnits);
            return false;
        } else {
            return true;
        }
    }


    private static final Set<ChronoUnit> allowedIntervalUnits
            = ImmutableSet.of(SECONDS, MINUTES, HOURS);


}

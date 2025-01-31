package us.dot.its.jpo.conflictmonitor.monitor.algorithms.aggregation;

import com.google.common.collect.ImmutableSet;
import lombok.Data;
import lombok.Generated;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;
import us.dot.its.jpo.conflictmonitor.monitor.models.config.ConfigData;
import us.dot.its.jpo.conflictmonitor.monitor.models.config.ConfigDataClass;
import us.dot.its.jpo.conflictmonitor.monitor.models.events.ProcessingTimePeriod;


import java.time.Duration;
import java.time.Instant;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
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
            updateType = READ_ONLY)
    volatile int interval;

    @ConfigData(key = "aggregation.interval.units",
            description = "The time units of the aggregation interval",
            updateType = READ_ONLY)
    volatile ChronoUnit intervalUnits;

    @ConfigData(key = "aggregation.punctuator.interval.ms",
            description = """
            How often to run the process to check whether to emit an aggregated event, in milliseconds. Must be shorter
            than the aggregation interval. Publishing the aggregated events may be delayed by up to this amount of time
            after the aggregation period elapses.
            """,
            updateType = READ_ONLY)
    long checkIntervalMs;

    @ConfigData(key = "aggregation.grace.period.ms",
            description = "Grace period for receiving out-of-order events",
            updateType = READ_ONLY)
    volatile long gracePeriodMs;

    @ConfigData(key = "aggregation.eventTopicMap",
        description = "Map of aggregated event names to output topic names",
        updateType = READ_ONLY)
    EventTopicMap eventTopicMap;

    @ConfigData(key = "aggregation.eventAlgorithmMap", description = "Map of aggregated event names to algorithm names",
        updateType = READ_ONLY)
    EventAlgorithmMap eventAlgorithmMap;

    public Duration aggInterval() {
        return Duration.of(interval, intervalUnits);
    }

    /**
     * @return Aggregation interval in epoch milliseconds
     */
    public long aggIntervalMs() {
        return aggInterval().toMillis();
    }

    /**
     * @return Aggregated event state store retention time in milliseconds, calculated to include 2 aggregation
     * intervals and grace periods.
     */
    public long retentionTimeMs() {
        return 2 * (aggIntervalMs() + gracePeriodMs);
    }

    /**
     * Utility function to get the beginning and end of the aggregation interval aligned to the beginning of the day,
     * hour, or minute, containing the timestamp.
     *
     * @param timestampMs Epoch millisecond timestamp
     * @return A midnight aligned time period (begin and end time) containing the timestamp.
     */
    public ProcessingTimePeriod aggTimePeriod(final long timestampMs) {
        validateInterval();
        final var timestamp = Instant.ofEpochMilli(timestampMs);
        final var zdt = ZonedDateTime.ofInstant(timestamp, ZoneOffset.UTC);
        return switch (intervalUnits) {
            case HOURS -> {
                final Instant startOfDay = ZonedDateTime.of(zdt.getYear(), zdt.getMonthValue(), zdt.getDayOfMonth(),
                        0, 0, 0, 0, ZoneOffset.UTC).toInstant();
                final int numHours = zdt.getHour();
                yield alignedTimePeriod(startOfDay, numHours);
            }
            case MINUTES -> {
                final Instant startOfHour = ZonedDateTime.of(zdt.getYear(), zdt.getMonthValue(), zdt.getDayOfMonth(),
                        zdt.getHour(), 0, 0, 0, ZoneOffset.UTC).toInstant();
                final int numMinutes = zdt.getMinute();
                yield alignedTimePeriod(startOfHour, numMinutes);
            }
            case SECONDS -> {
                final Instant startOfMinute = ZonedDateTime.of(zdt.getYear(), zdt.getMonthValue(), zdt.getDayOfMonth(),
                        zdt.getHour(), zdt.getMinute(), 0, 0, ZoneOffset.UTC).toInstant();
                final int numSeconds = zdt.getSecond();
                yield alignedTimePeriod(startOfMinute, numSeconds);
            }
            default -> throw new RuntimeException("Invalid interval units");
        };

    }

    private ProcessingTimePeriod alignedTimePeriod(final Instant startOfUnits, final int numUnits) {
        final var numberOfIntervals = numUnits / interval;
        final var startOfInterval = startOfUnits.plus((long) numberOfIntervals * interval, intervalUnits);
        final var endOfInterval = startOfInterval.plus(interval, intervalUnits);
        final var period = new ProcessingTimePeriod();
        period.setBeginTimestamp(startOfInterval.toEpochMilli());
        period.setEndTimestamp(endOfInterval.toEpochMilli());
        return period;
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

package us.dot.its.jpo.conflictmonitor.testutils;

import java.time.Duration;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

/**
 * Utilities for topology tests
 */
public class TopologyTestUtils {
    
    /**
     * Create a list of instants with period in Milliseconds lasting totalSeconds
     */
    public static List<Instant> getInstants(final Instant start, final int periodMillis, final int totalSeconds) {
        var instants = new ArrayList<Instant>();
        var currentTime = start.plusMillis(0L);
        instants.add(currentTime);
        while (Duration.between(start, currentTime).getSeconds() <= totalSeconds) {
            currentTime = currentTime.plusMillis(periodMillis);
            instants.add(currentTime);
        }
        return instants;
    }

    public static List<Instant> getInstantsExclusive(final Instant start, final int periodMillis, final int totalSeconds) {
        var instants = new ArrayList<Instant>();
        var currentTime = start.plusMillis(0L);
        instants.add(currentTime);
        while (Duration.between(start, currentTime).getSeconds() < totalSeconds) {
            currentTime = currentTime.plusMillis(periodMillis);
            instants.add(currentTime);
        }
        return instants;
    }
}

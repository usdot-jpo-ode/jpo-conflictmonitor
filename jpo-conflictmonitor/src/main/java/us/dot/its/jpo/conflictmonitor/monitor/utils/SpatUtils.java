package us.dot.its.jpo.conflictmonitor.monitor.utils;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import lombok.extern.slf4j.Slf4j;
import us.dot.its.jpo.asn.j2735.r2024.SPAT.MovementPhaseState;
import us.dot.its.jpo.conflictmonitor.monitor.models.spat.SpatMovementPhaseState;
import us.dot.its.jpo.conflictmonitor.monitor.models.spat.SpatTimestampExtractor;
import us.dot.its.jpo.geojsonconverter.pojos.spat.ProcessedMovementPhaseState;
import us.dot.its.jpo.geojsonconverter.pojos.spat.ProcessedMovementState;
import us.dot.its.jpo.geojsonconverter.pojos.spat.ProcessedSpat;

import java.time.Instant;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.ListIterator;

@Slf4j
public class SpatUtils {


    /**
     * The number of Milliseconds in a Second.
     */
    public static final double MS_TO_SEC = 1000.0;


    /**
     * Returns the state of a given signal group within the Processed Spat Message.
     * @param spat - The Processed Spat Message
     * @param signalGroup - which signal group to use
     * @return MovementPhaseState corresponding to the given signalGroup in the spat message. Returns null if the signalGroup is not present in the SPaT message.
     */
    public static ProcessedMovementPhaseState getSignalGroupState(ProcessedSpat spat, int signalGroup){
        for(ProcessedMovementState state: spat.getStates()){
            if(state.getSignalGroup() == signalGroup && !state.getStateTimeSpeed().isEmpty()){
                return state.getStateTimeSpeed().getFirst().getEventState();
            }
        }
        return null;
    }

    /**
     * Filter SPATs between two timestamps.
     * @param spats - List of SPATs, assumed to be ordered by timestamp
     * @param startTimestamp - start timestamp, inclusive
     * @param endTimestamp - end timestamp, inclusive
     * @return List of SPATs between startTimestamp and endTimestamp, inclusive
     */
    public static List<ProcessedSpat> filterSpatsByTimestamp(List<ProcessedSpat> spats, long startTimestamp, long endTimestamp) {
        List<ProcessedSpat> filteredSpats = new ArrayList<>();
        for (ProcessedSpat spat : spats) {
            long spatTimestamp = SpatTimestampExtractor.getSpatTimestamp(spat);
            if (spatTimestamp >= startTimestamp && spatTimestamp <= endTimestamp) {
                filteredSpats.add(spat);
            }
        }
        return filteredSpats;
    }

    /**
     * Generate string describing SPaT timing for a given signal group across a list of SPATs.
     * @param spats - List of SPATs, assumed to be ordered by timestamp
     * @param signalGroupId - signal group to describe
     * @return A human readable string representing time timing deltas for a given spat and the provided signal group Id.
     */
    public static String describeSpats(List<ProcessedSpat> spats, int signalGroupId) {
        StringBuilder sb = new StringBuilder();
        sb.append("SPATs for signal group ");
        sb.append(signalGroupId);
        sb.append(":\n");
        List<SpatTiming> spatTimingList = getSpatTiming(spats, signalGroupId);
        for (SpatTiming spatTiming : spatTimingList) {
            sb.append(spatTiming.getTimestamp());
            sb.append(" (delta ");
            sb.append(spatTiming.getDuration());
            sb.append("): ");
            sb.append(spatTiming.getState());
            sb.append("\n");
        }
        return sb.toString();
    }

    /**
     * Calculates the estimated duration of signal states in a SPaT message based upon the time between two SPaT messages.
     * @param spats - List of SPATs used to calculate SpatTimings from
     * @param signalGroupId signalGroupId to perform the calculation on.
     * @return List of SpatTiming objects representing the duration of each state.
     */
    public static List<SpatTiming> getSpatTiming(List<ProcessedSpat> spats, int signalGroupId) {
        var timingList = new ArrayList<SpatTiming>();

        for (ProcessedSpat spat : spats) {
            long timestamp = SpatTimestampExtractor.getSpatTimestamp(spat);
            MovementPhaseState state = getSignalGroupState(spat, signalGroupId);
            var spatTiming = new SpatTiming(timestamp, state);
            timingList.add(spatTiming);
        }

        // Estimate duration of each state based on the time between SPATs
        ListIterator<SpatTiming> iterator = timingList.listIterator();
        while (iterator.hasNext()) {
            SpatTiming spatTiming = iterator.next();
            long compareTiming = 0;
            if (iterator.hasNext()) {
                // There is a next element, compare to that
                compareTiming = timingList.get(iterator.nextIndex()).getTimestamp();
            } else if (iterator.hasPrevious()) {
                // There is no next element, so compare to the previous
                int previousIndex = iterator.previousIndex() - 1;
                if (previousIndex >= 0) {
                    compareTiming = timingList.get(previousIndex).getTimestamp();
                }
            }

            long duration = Math.abs(compareTiming - spatTiming.getTimestamp());
            spatTiming.setDuration(duration);
        }

        return timingList;
    }

    /**
     * Get statistics for the amount of time during each signal phase.
     * @param spats - List of ProcessedSPaT messages to calculate statistics for
     * @param signalGroupId - Signal group ID to use in calculating the Statistics
     * @return {@link SpatStatistics}
     */
    public static SpatStatistics getSpatStatistics(List<ProcessedSpat> spats, int signalGroupId) {
        long redMillis = 0;
        long yellowMillis = 0;
        long greenMillis = 0;
        long darkMillis = 0;

        List<SpatTiming> spatTimingList = getSpatTiming(spats, signalGroupId);
        for (SpatTiming spatTiming : spatTimingList) {
        switch (spatTiming.getState()) {
                case STOP_AND_REMAIN:
                    redMillis += spatTiming.getDuration();
                    break;
                case PERMISSIVE_CLEARANCE:
                case PROTECTED_CLEARANCE:
                    yellowMillis += spatTiming.getDuration();
                    break;
                case PERMISSIVE_MOVEMENT_ALLOWED:
                case PROTECTED_MOVEMENT_ALLOWED:
                    greenMillis += spatTiming.getDuration();
                    break;
                case DARK:
                    darkMillis += spatTiming.getDuration();
                default:
                    break;
            }
        }
        return new SpatStatistics(redMillis/MS_TO_SEC, yellowMillis/MS_TO_SEC, greenMillis/MS_TO_SEC, darkMillis/MS_TO_SEC);
    }

    @Getter
    @Setter
    public static class SpatTiming {

        /**
         * UTC milliseconds timestamp representing when the SPaT first used a given signal state.
         */
        private long timestamp;

        /**
         * The State of the Light.
         */
        private MovementPhaseState state;

        /**
         * The number of milliseconds the light was in this state.
         */
        private long duration;


        /**
         * Creates a SPaT timing object with the given timestamp and state.
         * @param timestamp The timestamp
         * @param state the phase state
         */
        public SpatTiming(long timestamp, MovementPhaseState state) {
            this.timestamp = timestamp;
            this.state = state;
        }
    }



    @Getter
    @Setter
    @AllArgsConstructor
    @ToString
    public static class SpatStatistics {
        /**
         * The amount of time (seconds) the event state was ‘stop-and-remain’
         * while the vehicle was stopped.
         */
        private double timeStoppedDuringRed;

        /**
         * The amount of time (seconds) the event state was ‘protected-clearance’
         * or ‘permissive-clearance’ while the vehicle was stopped.
         */
        private double timeStoppedDuringYellow;

        /**
         * The amount of time (seconds) the event state was ‘protected-movement-allowed’ or ‘permissive-movement-allowed’ while the
         * vehicle was stopped.
         */
        private double timeStoppedDuringGreen;


        /**
         * The amount of time (seconds) the event state was ‘dark’ while the
         * vehicle was stopped.
         */
        private double timeStoppedDuringDark;



    }

    /**
     * Return the epochMillisecond when the SPaT message occured. 
     * @param processedSpat - ProcessedSpat Message
     * @return the Epoch Timestamp of the Processed Spat Message. Returns 0 if no timestamp is available.
     */
    public static long getTimestamp(ProcessedSpat processedSpat) {
        if (processedSpat == null) {
            log.error("ProcessedSpat is null");
            return 0L;
        }
        ZonedDateTime zdt = processedSpat.getUtcTimeStamp();
        if (zdt == null) {
            log.error("ProcessedSpat.UtcTimeStamp is null");
            return 0L;
        }
        return zdt.toInstant().toEpochMilli();
    }

    /**
     * Returns the epochMillisecond of when the ode received the SPaT message.
     * @param processedSpat - The ProcessedSpat to extract the timestamp from
     * @return The epoch timestamp of equivalent to the OdeReceivedAt time in the ProcessedSpat message. Returns 0 if the odeReceivedAt time is missing.
     */
    public static long getOdeReceivedAt(ProcessedSpat processedSpat) {
        if (processedSpat == null) {
            log.error("ProcessedSpat is null");
            return 0L;
        }
        String odeReceivedAtStr = processedSpat.getOdeReceivedAt();
        if (odeReceivedAtStr == null) {
            log.error("ProcessedSpat.OdeReceivedAt is null");
            return 0L;
        }
        try {
            return Instant.parse(odeReceivedAtStr).toEpochMilli();
        } catch (Exception ex) {
            log.error("Exception parsing ProcessedSpat.OdeReceivedAt", ex);
            return 0L;
        }
    }



}

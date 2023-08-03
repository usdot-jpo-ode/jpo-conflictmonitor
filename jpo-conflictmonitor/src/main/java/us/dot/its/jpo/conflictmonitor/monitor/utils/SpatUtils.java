package us.dot.its.jpo.conflictmonitor.monitor.utils;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import us.dot.its.jpo.conflictmonitor.monitor.models.spat.SpatTimestampExtractor;
import us.dot.its.jpo.geojsonconverter.pojos.spat.MovementEvent;
import us.dot.its.jpo.geojsonconverter.pojos.spat.MovementState;
import us.dot.its.jpo.geojsonconverter.pojos.spat.ProcessedSpat;
import us.dot.its.jpo.ode.plugin.j2735.J2735MovementPhaseState;

import java.util.*;

public class SpatUtils {

    public static J2735MovementPhaseState getSignalGroupState(ProcessedSpat spat, int signalGroup){
        for(MovementState state: spat.getStates()){
            if(state.getSignalGroup() == signalGroup && state.getStateTimeSpeed().size() > 0){
                return state.getStateTimeSpeed().get(0).getEventState();
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

    public static List<SpatTiming> getSpatTiming(List<ProcessedSpat> spats, int signalGroupId) {
        var timingList = new ArrayList<SpatTiming>();

        for (ProcessedSpat spat : spats) {
            long timestamp = SpatTimestampExtractor.getSpatTimestamp(spat);
            J2735MovementPhaseState state = getSignalGroupState(spat, signalGroupId);
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
     * Get statistics for the amount of time during each signal phase while
     * @return {@link SpatStatistics}
     */
    public static SpatStatistics getSpatStatistics(List<ProcessedSpat> spats, int signalGroupId) {
        long redMillis = 0;
        long yellowMillis = 0;
        long greenMillis = 0;
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
                default:
                    break;
            }
        }
        return new SpatStatistics(redMillis/1000.0, yellowMillis/1000.0, greenMillis/1000.0);
    }

    @Getter
    @Setter
    public static class SpatTiming {
        public SpatTiming(long timestamp, J2735MovementPhaseState state) {
            this.timestamp = timestamp;
            this.state = state;
        }
        private long timestamp;
        private J2735MovementPhaseState state;
        private long duration;
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
    }
}

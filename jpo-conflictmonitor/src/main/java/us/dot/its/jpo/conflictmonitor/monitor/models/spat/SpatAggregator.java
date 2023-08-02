package us.dot.its.jpo.conflictmonitor.monitor.models.spat;

import java.time.Instant;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Optional;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import us.dot.its.jpo.geojsonconverter.pojos.spat.MovementEvent;
import us.dot.its.jpo.geojsonconverter.pojos.spat.MovementState;
import us.dot.its.jpo.geojsonconverter.pojos.spat.ProcessedSpat;

public class SpatAggregator {

    private ArrayList<ProcessedSpat> spats = new ArrayList<ProcessedSpat>();

    private Comparator<ProcessedSpat> spatComparator = new Comparator<ProcessedSpat>() {
        @Override
        public int compare(ProcessedSpat spat1, ProcessedSpat spat2) {
            long t1 = SpatTimestampExtractor.getSpatTimestamp(spat1);
            long t2 = SpatTimestampExtractor.getSpatTimestamp(spat2);
            if (t2 < t1) {
                return 1;
            } else if (t2 == t1) {
                return 0;
            } else {
                return -1;
            }
        }
    };
    
    public SpatAggregator add(ProcessedSpat newSpat) {
        spats.add(newSpat);
        return this;
    }

    public SpatAggregator subtract(ProcessedSpat newSpat){
        return this;
    }

    public void sort(){
        Collections.sort(this.spats, spatComparator);
    }

    public ProcessedSpat getSpatAtTime(long timestamp){
        ProcessedSpat lookupSpat = new ProcessedSpat();
        lookupSpat.setUtcTimeStamp(ZonedDateTime.ofInstant(Instant.ofEpochMilli(timestamp), ZoneOffset.UTC));
        int index = -1 * (Collections.binarySearch(this.spats, lookupSpat, spatComparator)+1);

        if(index <= 0){
            return this.spats.get(0);
        }else if(index >= this.spats.size()){
            return this.spats.get(this.spats.size()-1);
        }else{
            long firstDelta = getSpatTimeDelta(this.spats.get(index-1), timestamp);
            long secondDelta = getSpatTimeDelta(this.spats.get(index), timestamp);

            if(firstDelta < secondDelta){
                return this.spats.get(index-1);
            }else{
                return this.spats.get(index);
            }
        }
    }

    public long getSpatTimeDelta(ProcessedSpat spat, long timestamp){
        return Math.abs(SpatTimestampExtractor.getSpatTimestamp(spat) - timestamp);
    }


    public ArrayList<ProcessedSpat> getSpats() {
        return spats;
    }

    public void setSpatList(ArrayList<ProcessedSpat> spats) {
        this.spats = spats;
    }

    public String describeSpats(int signalGroupId) {
        StringBuilder sb = new StringBuilder();
        sb.append("SPATs for signal group ");
        sb.append(signalGroupId);
        sb.append(":\n");
        for (ProcessedSpat spat : spats) {
            long timestamp = SpatTimestampExtractor.getSpatTimestamp(spat);
            Optional<MovementState> optMovementState = spat.getStates().stream().filter(state -> state.getSignalGroup() == signalGroupId).findFirst();
            if (optMovementState.isEmpty()) continue;
            MovementState movementState = optMovementState.get();
            Optional<MovementEvent> optMovementEvent = movementState.getStateTimeSpeed().stream().findFirst();
            if (optMovementEvent.isEmpty()) continue;
            MovementEvent movementEvent = optMovementEvent.get();
            sb.append(timestamp);
            sb.append(" ms: ");
            sb.append(movementEvent.getEventState());
            sb.append("\n");
        }
        return sb.toString();
    }

    /**
     * Get statistics for the amount of time during each signal phase while
     * @return {@link SpatStatistics}
     */
    public SpatStatistics getSpatStatistics(int signalGroupId) {
        long redMillis = 0;
        long yellowMillis = 0;
        long greenMillis = 0;

        return new SpatStatistics(redMillis, yellowMillis, greenMillis);
    }

    @Getter
    @Setter
    @AllArgsConstructor
    @ToString
    public class SpatStatistics {
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
         * The amount of time (seconds) the event state was ‘protectedmovement-allowed’ or ‘permissive-movement-allowed’ while the
         * vehicle was stopped.
         */
        private double timeStoppedDuringGreen;
    }
}

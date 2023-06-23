package us.dot.its.jpo.conflictmonitor.monitor.models.spat;

import java.time.Instant;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
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
}

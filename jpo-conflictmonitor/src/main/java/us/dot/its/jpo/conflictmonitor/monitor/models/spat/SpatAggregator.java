package us.dot.its.jpo.conflictmonitor.monitor.models.spat;

import java.time.Instant;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.TreeSet;



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

        for(ProcessedSpat s: this.spats){
            System.out.println(s.getUtcTimeStamp());
        }

        System.out.println("Number of Spats: " + this.spats.size());
        System.out.println("Lookup Spat Time: " + lookupSpat.getUtcTimeStamp());
        System.out.println("First Spat Time:  " + this.spats.get(0).getUtcTimeStamp());
        System.out.println("Last Spat Time:   " + this.spats.get(this.spats.size()-1).getUtcTimeStamp());
        System.out.println("Lookup Index: " + index);
        System.out.println("Found Spat: " + this.spats.get(index-1).getUtcTimeStamp());
        System.out.println("Found Spat2: " + this.spats.get(index).getUtcTimeStamp());


        if(index <= 0){
            return this.spats.get(0);
        }else if(index >= this.spats.size()){
            return this.spats.get(this.spats.size()-1);
        }else{
            long firstDelta = getSpatTimeDelta(this.spats.get(index-1), timestamp);
            long secondDelta = getSpatTimeDelta(this.spats.get(index), timestamp);

            System.out.println("First Delta:" + firstDelta);
            System.out.println("Second Delta: " + secondDelta);

            if(firstDelta < secondDelta){
                System.out.println("First");
                return this.spats.get(index-1);
            }else{
                System.out.println("Second");
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

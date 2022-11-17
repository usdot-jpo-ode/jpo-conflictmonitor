package us.dot.its.jpo.conflictmonitor.monitor.models.spat;

import java.util.Comparator;
import java.util.TreeSet;



import us.dot.its.jpo.geojsonconverter.pojos.spat.ProcessedSpat;

public class SpatAggregator {
    private Comparator<ProcessedSpat> spatComparator = new Comparator<ProcessedSpat>() {
        @Override
        public int compare(ProcessedSpat spat1, ProcessedSpat spat2) {
            long t1 = SpatTimestampExtractor.getSpatTimestamp(spat1);
            long t2 = SpatTimestampExtractor.getSpatTimestamp(spat2);
            if (t2 < t1) {
                return -1;
            } else if (t2 == t1) {
                return 0;
            } else {
                return 1;
            }
        }
    };

    private TreeSet<ProcessedSpat> spats = new TreeSet<ProcessedSpat>(spatComparator);
    public SpatAggregator add(ProcessedSpat newSpat) {
        spats.add(newSpat);
        return this;
    }

    public SpatAggregator subtract(ProcessedSpat newSpat){
        return this;
    }

    public TreeSet<ProcessedSpat> getSpats() {
        return spats;
    }

    public void setSpatList(TreeSet<ProcessedSpat> spats) {
        this.spats = spats;
    }
}

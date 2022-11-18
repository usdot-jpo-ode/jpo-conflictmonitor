package us.dot.its.jpo.conflictmonitor.monitor.models.bsm;

import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Comparator;
import java.util.TreeSet;

import us.dot.its.jpo.ode.model.OdeBsmData;

public class BsmAggregator {
    //private final long retainBsmSeconds = 60;

    private Comparator<OdeBsmData> bsmComparator = new Comparator<OdeBsmData>() {
        @Override
        public int compare(OdeBsmData bsm1, OdeBsmData bsm2) {
            long t1 = BsmTimestampExtractor.getBsmTimestamp(bsm1);
            long t2 = BsmTimestampExtractor.getBsmTimestamp(bsm2);
            if (t2 < t1) {
                return 1;
            } else if (t2 == t1) {
                return 0;
            } else {
                return -1;
            }
        }
    };

    private TreeSet<OdeBsmData> bsms = new TreeSet<OdeBsmData>(bsmComparator);
    public BsmAggregator add(OdeBsmData newBsm) {
        bsms.add(newBsm);
        return this;
    }

    public BsmAggregator addWithoutDeletion(OdeBsmData newBsm){
        bsms.add(newBsm);
        return this;
    }

    public BsmAggregator subtract(OdeBsmData newBsm){
        return this;
    }

    // public ZonedDateTime getBsmDateTime(OdeBsmData bsm) {
    //     return ZonedDateTime.parse(bsm.getMetadata().getOdeReceivedAt(), DateTimeFormatter.ISO_ZONED_DATE_TIME);
    // }

    public TreeSet<OdeBsmData> getBsms() {
        return bsms;
    }

    public void setBsmList(TreeSet<OdeBsmData> bsms) {
        this.bsms = bsms;
    }
}



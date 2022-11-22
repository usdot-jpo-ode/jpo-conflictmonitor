package us.dot.its.jpo.conflictmonitor.monitor.models.bsm;

import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.TreeSet;

import us.dot.its.jpo.ode.model.OdeBsmData;

public class BsmAggregator {
    //private final long retainBsmSeconds = 60;

    private ArrayList<OdeBsmData> bsms = new ArrayList<OdeBsmData>();

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

    
    public BsmAggregator add(OdeBsmData newBsm) {
        bsms.add(newBsm);
        return this;
    }

    public void sort(){
        Collections.sort(this.bsms, bsmComparator);
    }

    public BsmAggregator addWithoutDeletion(OdeBsmData newBsm){
        bsms.add(newBsm);
        return this;
    }

    public BsmAggregator subtract(OdeBsmData newBsm){
        return this;
    }

    public ArrayList<OdeBsmData> getBsms() {
        return bsms;
    }

    public void setBsmList(ArrayList<OdeBsmData> bsms) {
        this.bsms = bsms;
    }
}



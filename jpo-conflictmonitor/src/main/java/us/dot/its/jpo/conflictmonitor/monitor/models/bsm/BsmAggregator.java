package us.dot.its.jpo.conflictmonitor.monitor.models.bsm;

import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Comparator;
import java.util.TreeSet;

import us.dot.its.jpo.ode.model.OdeBsmData;

public class BsmAggregator {
    //private ArrayList<OdeBsmData> bsmList = new ArrayList<>();
    private final long retainBsmSeconds = 60;

    private Comparator<OdeBsmData> bsmComparator = new Comparator<OdeBsmData>() {
        @Override
        public int compare(OdeBsmData bsm1, OdeBsmData bsm2) {
            ZonedDateTime t1 = getBsmDateTime(bsm1);
            ZonedDateTime t2 = getBsmDateTime(bsm2);
            if (t2.isBefore(t1)) {
                return -1;
            } else if (t2.isEqual(t1)) {
                return 0;
            } else {
                return 1;
            }
        }
    };

    private TreeSet<OdeBsmData> bsms = new TreeSet<OdeBsmData>(bsmComparator);
    public BsmAggregator add(OdeBsmData newBsm) {
        bsms.add(newBsm);

        OdeBsmData mostRecentBSM = bsms.first();
        ZonedDateTime mostRecentTime = getBsmDateTime(mostRecentBSM);
        ZonedDateTime compareTime = mostRecentTime.minusSeconds(retainBsmSeconds);
        while (true){
            OdeBsmData checkBsm = bsms.last();
            if(getBsmDateTime(checkBsm).isBefore(compareTime)){
                bsms.remove(checkBsm);
            }else{
                break;
            }
        }


        // Adds a new BSM to the list of stored BSMs. Removes any BSM that is older than
        // the new BSM by the specified number of seconds.

        // ZonedDateTime newBsmDateTime = getBsmDateTime(newBsm);

        // newBsmDateTime.minusSeconds(retainBsmSeconds);
        // for (OdeBsmData bsm : bsmList) {
        //     if (getBsmDateTime(bsm).isBefore(newBsmDateTime)) {
        //         bsmList.remove(bsm);
        //     } else {
        //         break; // remove bsms until they are more recent then the incoming bsm
        //     }
        // }
        // while (bsmList.size() > 50){
        //     bsmList.remove(0);
        // }
        

        // bsmList.add(newBsm);
        return this;
    }

    public BsmAggregator addWithoutDeletion(OdeBsmData newBsm){
        bsms.add(newBsm);
        return this;
    }

    public BsmAggregator subtract(OdeBsmData newBsm){
        return this;
    }

    public ZonedDateTime getBsmDateTime(OdeBsmData bsm) {
        return ZonedDateTime.parse(bsm.getMetadata().getOdeReceivedAt(), DateTimeFormatter.ISO_ZONED_DATE_TIME);
    }

    public TreeSet<OdeBsmData> getBsms() {
        return bsms;
    }

    public void setBsmList(TreeSet<OdeBsmData> bsms) {
        this.bsms = bsms;
    }
}



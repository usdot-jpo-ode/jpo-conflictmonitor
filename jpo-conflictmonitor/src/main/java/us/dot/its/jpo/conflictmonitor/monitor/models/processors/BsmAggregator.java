package us.dot.its.jpo.conflictmonitor.monitor.models.processors;

import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;

import us.dot.its.jpo.ode.model.OdeBsmData;

public class BsmAggregator {
    private ArrayList<OdeBsmData> bsmList = new ArrayList<>();
    private final long retainBsmSeconds = 60;

    public BsmAggregator add(OdeBsmData newBsm) {

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
        while (bsmList.size() > 50){
            bsmList.remove(0);
        }
        

        bsmList.add(newBsm);
        return this;
    }

    public ZonedDateTime getBsmDateTime(OdeBsmData bsm) {
        return ZonedDateTime.parse(bsm.getMetadata().getOdeReceivedAt(), DateTimeFormatter.ISO_ZONED_DATE_TIME);
    }

    public ArrayList<OdeBsmData> getBsmList() {
        return bsmList;
    }

    public void setBsmList(ArrayList<OdeBsmData> bsmList) {
        this.bsmList = bsmList;
    }
}

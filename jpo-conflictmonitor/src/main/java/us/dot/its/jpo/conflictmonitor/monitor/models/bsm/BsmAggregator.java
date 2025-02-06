package us.dot.its.jpo.conflictmonitor.monitor.models.bsm;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import lombok.Getter;
import lombok.Setter;
import us.dot.its.jpo.geojsonconverter.DateJsonMapper;
import us.dot.its.jpo.ode.model.OdeBsmData;

public class BsmAggregator {


    @Getter
    @Setter
    private ArrayList<OdeBsmData> bsms = new ArrayList<OdeBsmData>();

    /** 
     * In-line class definition of a comparator used for sorting OdeBsmData objects by time.
     */
    private Comparator<OdeBsmData> bsmComparator = new Comparator<OdeBsmData>() {

        /** 
         * This is a comparison function which can be used to sort BSM's based upon the timestamp in the BSMs.
         * 
         * @param bsm1 the first BSM to compare
         * @param bsm2 the second BSM to compare
         * @return -1, 1, 0 - Returns 1 if the timestamp of bsm 2 is less than the timestamp of bsm 1. Returns 0 if the timestamps are the same. Returns -1 if the timestamp of BSM 2 is greater than the timestamp of bsm 1
         */
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

    
    
    /** 
     * @param newBsm the BSM to add to the aggregation
     * @return BsmAggregator returns this instance of the BSMAggregator
     */
    public BsmAggregator add(OdeBsmData newBsm) {
        bsms.add(newBsm);
        return this;
    }

    /** 
     * Sorts the BSMs in this BSM aggregator by time.
     */
    public void sort(){
        Collections.sort(this.bsms, bsmComparator);
    }

    /** 
     * @param newBsm the BSM to add to the aggregation
     * @return BsmAggregator returns this instance of the BSMAggregator
     */
    public BsmAggregator addWithoutDeletion(OdeBsmData newBsm){
        bsms.add(newBsm);
        return this;
    }

    /** 
     * @return a JSON string representing the contents of this BSM aggregation object.
     */
    @Override
    public String toString() {
        ObjectMapper mapper = DateJsonMapper.getInstance();
        String testReturn = "";
        try {
            testReturn = (mapper.writeValueAsString(this));
        } catch (JsonProcessingException e) {
            System.out.println(e);
        }
        return testReturn;
    }
}



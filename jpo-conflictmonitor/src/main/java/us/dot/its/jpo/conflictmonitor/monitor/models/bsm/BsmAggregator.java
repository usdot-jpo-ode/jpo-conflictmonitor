package us.dot.its.jpo.conflictmonitor.monitor.models.bsm;

import java.util.ArrayList;

import java.util.Comparator;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import lombok.Getter;
import lombok.Setter;
import us.dot.its.jpo.geojsonconverter.DateJsonMapper;
import us.dot.its.jpo.geojsonconverter.pojos.geojson.Point;
import us.dot.its.jpo.geojsonconverter.pojos.geojson.bsm.ProcessedBsm;

public class BsmAggregator {

    @Getter
    @Setter
    private ArrayList<ProcessedBsm<Point>> bsms = new ArrayList<>();

    private Comparator<ProcessedBsm<Point>> bsmComparator = new Comparator<>() {
        @Override
        public int compare(ProcessedBsm<Point> bsm1, ProcessedBsm<Point> bsm2) {
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

    
    public BsmAggregator add(ProcessedBsm<Point> newBsm) {
        bsms.add(newBsm);
        return this;
    }

    public void sort(){
        this.bsms.sort(bsmComparator);
    }

    public BsmAggregator addWithoutDeletion(ProcessedBsm<Point> newBsm){
        bsms.add(newBsm);
        return this;
    }

    public BsmAggregator subtract(ProcessedBsm<Point> newBsm){
        return this;
    }



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



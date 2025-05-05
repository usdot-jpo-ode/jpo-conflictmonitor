package us.dot.its.jpo.conflictmonitor.monitor.models.bsm;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import lombok.Getter;
import lombok.Setter;
import us.dot.its.jpo.geojsonconverter.DateJsonMapper;
import us.dot.its.jpo.geojsonconverter.pojos.geojson.Point;
import us.dot.its.jpo.geojsonconverter.pojos.geojson.bsm.ProcessedBsm;





public class MisbehaviorAggregator {


    @Getter
    @Setter
    private ArrayList<TimeAcceleration> accelerations = new ArrayList<TimeAcceleration>();

    // private Comparator<OdeBsmData> bsmComparator = new Comparator<OdeBsmData>() {
    //     @Override
    //     public int compare(OdeBsmData bsm1, OdeBsmData bsm2) {
    //         long t1 = BsmTimestampExtractor.getBsmTimestamp(bsm1);
    //         long t2 = BsmTimestampExtractor.getBsmTimestamp(bsm2);
    //         if (t2 < t1) {
    //             return 1;
    //         } else if (t2 == t1) {
    //             return 0;
    //         } else {
    //             return -1;
    //         }
    //     }
    // };

    
    public MisbehaviorAggregator add(ProcessedBsm<Point> newBsm) {
        TimeAcceleration acceleration = new TimeAcceleration();
        acceleration.setTime(newBsm.getProperties().getTimeStamp().toInstant().toEpochMilli());
        acceleration.setLateralAcceleration(getVehicleAcceleration(newBsm.getProperties().getAccelSet().getAccelLat()));
        acceleration.setLongitudinalAcceleration(getVehicleAcceleration(newBsm.getProperties().getAccelSet().getAccelLong()));
        acceleration.setVerticalAcceleration(getVehicleAcceleration(newBsm.getProperties().getAccelSet().getAccelVert()));
        accelerations.add(acceleration);
        return this;
    }

    public TimeAcceleration getAverageTimeAcceleration(){
        double totalLateral = 0;
        double totalLongitudinal = 0;
        double totalVertical = 0;

        for(TimeAcceleration accel : accelerations){
            totalLateral += accel.getLateralAcceleration();
            totalLongitudinal += accel.getLongitudinalAcceleration();
            totalVertical += accel.getVerticalAcceleration();    
        }

        TimeAcceleration retVal = new TimeAcceleration();
        retVal.setLateralAcceleration(totalLateral / accelerations.size());
        retVal.setLongitudinalAcceleration(totalLongitudinal / accelerations.size());
        retVal.setVerticalAcceleration(totalVertical / accelerations.size());

        retVal.setTime(accelerations.getFirst().getTime());

        return retVal;
    }


    // public BsmAggregator subtract(ProcessedBsm<Point> newBsm){
    //     ArrayList<TimeAcceleration> markedForDeletion = new ArrayList<>();
    //     for(TimeAcceleration accel : accelerations){
    //         if(accel.getTime())
    //     }

    //     return this;
    // }



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

    /**
     * 
     * @param acceleration
     * @return The Vehicle Acceleration converted to Ft / S^2
     */
    public double getVehicleAcceleration(BigDecimal acceleration){
        return acceleration.doubleValue() * 0.01 * 3.2808399;
    }
}



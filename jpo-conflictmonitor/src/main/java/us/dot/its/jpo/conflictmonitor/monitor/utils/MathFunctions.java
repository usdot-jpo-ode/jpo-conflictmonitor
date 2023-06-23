package us.dot.its.jpo.conflictmonitor.monitor.utils;

import java.util.ArrayList;
import java.util.Collections;

public class MathFunctions {
    

    public static double getMedian(ArrayList<Double> list){
        if(list.size() > 0){
            
            Collections.sort(list);

            int middle = list.size()/2;
            if(list.size() % 2 ==0){
                return (list.get(middle) + list.get(middle-1)) / 2.0;
            }else{
                return list.get(list.size()/2);
            }
        } else{
            return 0;
        }
        
    }

    public static long getMedianTimestamp(ArrayList<Long> list){
        Collections.sort(list);

        int middle = list.size()/2;
        if(list.size() % 2 ==0){
            return (list.get(middle) + list.get(middle-1)) / 2;
        }else{
            return list.get(list.size()/2);
        }
    }

    // returns the input value bounded by the positive and negative threshold;
    public static double clamp(double value, double threshold) {
        return Math.max(Math.min(value, threshold), -threshold);
    }



}

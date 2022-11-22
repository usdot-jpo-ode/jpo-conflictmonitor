package us.dot.its.jpo.conflictmonitor.monitor.utils;

import java.util.ArrayList;
import java.util.Collections;

public class MathFunctions {
    

    public static double getMedian(ArrayList<Double> list){
        Collections.sort(list);

        int middle = list.size()/2;
        if(list.size() % 2 ==0){
            return (list.get(middle) + list.get(middle+1)) / 2.0;
        }else{
            return list.get(list.size()/2);
        }
    }



}

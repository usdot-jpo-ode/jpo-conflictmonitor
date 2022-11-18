package us.dot.its.jpo.conflictmonitor.monitor.utils;

public class CircleMath {
    

    // Returns the Minimum distance between two angles computed in degrees. 
    public static double getAngularDistanceDegrees(double angle1, double angle2){
        double boundedAngle1 = boundAngleDegrees(angle1);
        double boundedAngle2 = boundAngleDegrees(angle2);

        double distance = Math.abs(boundedAngle2 - boundedAngle1);
        if (distance > 180){
            return 360 - distance;
        }else{
            return distance;
        }
    }

    // Returns an equivalent angle bounded between 0 and 360 degrees
    public static double boundAngleDegrees(double angle){
        return angle % 360;
    }
}

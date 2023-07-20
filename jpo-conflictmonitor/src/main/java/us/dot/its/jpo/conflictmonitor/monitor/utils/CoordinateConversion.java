package us.dot.its.jpo.conflictmonitor.monitor.utils;

import java.awt.geom.Point2D;

import org.geotools.geometry.DirectPosition2D;
import org.geotools.referencing.GeodeticCalculator;
import org.geotools.referencing.datum.DefaultEllipsoid;
import org.opengis.referencing.operation.TransformException;

public class CoordinateConversion {

    public static double[] offsetMToLongLat(double refLongitude, double refLatitude, double offsetX, double offsetY){

        double direction = Math.atan2(offsetX, offsetY);
        double distance = Math.sqrt(Math.pow(offsetX,2) + Math.pow(offsetY, 2));

        GeodeticCalculator geoCalc = new GeodeticCalculator(DefaultEllipsoid.WGS84);
        geoCalc.setStartingGeographicPoint(refLongitude, refLatitude);
        geoCalc.setDirection(Math.toDegrees(direction), distance);



        Point2D convertedPoint = geoCalc.getDestinationGeographicPoint();
        double output[] = {convertedPoint.getX(), convertedPoint.getY()}; 

        //DirectPosition result = geoCalc.getDestinationPosition();
        return output;


    }

    public static double[] offsetCmToLongLat(double refLongitude, double refLatitude, double offsetX, double offsetY){
        return offsetMToLongLat(refLongitude, refLatitude, offsetX / 100.0, offsetY / 100.0);
    }

    public static double[] longLatToOffsetM(double lng, double lat, double refLng, double refLat){

        GeodeticCalculator geoCalc = new GeodeticCalculator(DefaultEllipsoid.WGS84);
        geoCalc.setStartingGeographicPoint(refLng, refLat);
        geoCalc.setDestinationGeographicPoint(lng, lat);
        double azimuth = geoCalc.getAzimuth();
        double distance = geoCalc.getOrthodromicDistance();

        double offsetX = distance * Math.sin(Math.toRadians(azimuth));
        double offsetY = distance * Math.cos(Math.toRadians(azimuth));

        double[] ret = {offsetX, offsetY};
        return ret;
    }

    public static double[] longLatToOffsetMM(double lng, double lat, double refLng, double refLat){
        double[] offsetM = longLatToOffsetM(lng, lat, refLng, refLat);
        double[] ret = {offsetM[0]*1000.0, offsetM[1]*1000.0};
        return ret;
    }

    public static double[] longLatToOffsetCM(double lng, double lat, double refLng, double refLat){
        double[] offsetM = longLatToOffsetM(lng, lat, refLng, refLat);
        double[] ret = {offsetM[0]*100.0, offsetM[1]*100.0};
        return ret;
    }

    public static double[] offsetMToLongLat2(double refLongitude, double refLatitude, double offsetX, double offsetY){

        double direction = Math.atan2(offsetX, offsetY);
        double distance = Math.sqrt(Math.pow(offsetX,2) + Math.pow(offsetY, 2));

        GeodeticCalculator geoCalc = new GeodeticCalculator();
        try {
            geoCalc.setStartingPosition(new DirectPosition2D(0,0));
            geoCalc.setDestinationPosition(new DirectPosition2D(offsetX, offsetY));
        } catch (TransformException e) {
            e.printStackTrace();
        }
        
        
        double azimuth = geoCalc.getAzimuth();
        double orthDistance = geoCalc.getOrthodromicDistance();

        Point2D convertedPoint = geoCalc.getDestinationGeographicPoint();
        double output[] = {convertedPoint.getX(), convertedPoint.getY()}; 

        //DirectPosition result = geoCalc.getDestinationPosition();
        return output;


    }

    public static final double CM_PER_FOOT = 30.48;

    /**
     * Convert feet to centimeters
     * @param feet length in feet
     * @return length in centimeters
     */
    public static double feetToCM(double feet) {
        return CM_PER_FOOT * feet;
    }

    
}

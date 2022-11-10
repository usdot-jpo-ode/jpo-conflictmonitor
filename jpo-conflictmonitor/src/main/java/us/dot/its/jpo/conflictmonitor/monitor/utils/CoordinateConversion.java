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

    public static double[] offsetMToLongLat2(double refLongitude, double refLatitude, double offsetX, double offsetY){

        double direction = Math.atan2(offsetX, offsetY);
        double distance = Math.sqrt(Math.pow(offsetX,2) + Math.pow(offsetY, 2));

        GeodeticCalculator geoCalc = new GeodeticCalculator();
        try {
            geoCalc.setStartingPosition(new DirectPosition2D(0,0));
            geoCalc.setDestinationPosition(new DirectPosition2D(offsetX, offsetY));
        } catch (TransformException e) {
            System.out.println("Failed to offset in Geodeteic Calculator");
            e.printStackTrace();
        }
        
        
        double azimuth = geoCalc.getAzimuth();
        double orthDistance = geoCalc.getOrthodromicDistance();

        System.out.println(Math.toDegrees(direction) + ", " + azimuth+", " +  distance + ", "+  orthDistance);
        



        Point2D convertedPoint = geoCalc.getDestinationGeographicPoint();
        double output[] = {convertedPoint.getX(), convertedPoint.getY()}; 

        //DirectPosition result = geoCalc.getDestinationPosition();
        return output;


    }
}

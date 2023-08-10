package us.dot.its.jpo.conflictmonitor.monitor.utils;

import java.awt.geom.Point2D;

import lombok.SneakyThrows;
import org.geotools.geometry.DirectPosition2D;
import org.geotools.geometry.jts.JTS;
import org.geotools.referencing.CRS;
import org.geotools.referencing.GeodeticCalculator;
import org.geotools.referencing.crs.DefaultGeographicCRS;
import org.geotools.referencing.datum.DefaultEllipsoid;
import org.locationtech.jts.geom.Coordinate;
import org.locationtech.jts.geom.Geometry;
import org.locationtech.jts.geom.LineString;
import org.opengis.referencing.FactoryException;
import org.opengis.referencing.crs.CoordinateReferenceSystem;
import org.opengis.referencing.operation.MathTransform;
import org.opengis.referencing.operation.TransformException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class CoordinateConversion {

    private static final Logger logger = LoggerFactory.getLogger(CoordinateConversion.class);

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

    /**
     * Finds math transforms to convert a geometry between the default Geographic Coordinate System and UTM.
     *
     * The conversion to UTM is more approximate than the Offset Centimeter coordinates used for MAPs, but is
     * useful for generating simplified geometries for display and testing.
     *
     * @param gcsGeom - Geometry using geographic (long/lat) coordinates.
     * @return {@link MathTransformPair}
     */
    public static MathTransformPair findGcsToUtmTransforms(Geometry gcsGeom) {
        try {
            Coordinate[] coordinates = gcsGeom.getCoordinates();
            Coordinate refCoord = coordinates[0];
            String epsg = String.format("AUTO:42001,%.2f,%.2f", refCoord.getX(), refCoord.getY());
            CoordinateReferenceSystem crs = CRS.decode(epsg);
            MathTransform transform = CRS.findMathTransform(DefaultGeographicCRS.WGS84, crs);
            MathTransform inverseTransform = CRS.findMathTransform(crs, DefaultGeographicCRS.WGS84);
            return new MathTransformPair(transform, inverseTransform);
        } catch (Exception ex) {
            logger.error("Exception finding coordinate transform", ex);
            return null;
        }
    }

    public static LineString transformLineString(LineString line, MathTransform transform) {
        try {
            return (LineString) JTS.transform(line, transform);
        } catch (Exception ex) {
            logger.error("Coordinate conversion failed, returning the LineString as is.", ex);
            return line;
        }
    }
    
}

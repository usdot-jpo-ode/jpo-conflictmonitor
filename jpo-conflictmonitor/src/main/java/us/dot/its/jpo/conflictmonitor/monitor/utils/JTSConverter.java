package us.dot.its.jpo.conflictmonitor.monitor.utils;

import org.locationtech.jts.geom.Coordinate;
import org.locationtech.jts.geom.Geometry;
import org.locationtech.jts.geom.GeometryFactory;
import org.locationtech.jts.geom.PrecisionModel;
import us.dot.its.jpo.geojsonconverter.pojos.geojson.LineString;
import us.dot.its.jpo.geojsonconverter.pojos.geojson.Point;

/**
 * Methods to convert between GeoJSON and JTS.
 */
public class JTSConverter {

    private final static GeometryFactory FACTORY = new GeometryFactory(
            new PrecisionModel(PrecisionModel.FLOATING));

    public static org.locationtech.jts.geom.LineString convertToJTS(LineString lineString) {
        return (org.locationtech.jts.geom.LineString)convert(lineString);
    }

    public static org.locationtech.jts.geom.Point convertToJTS(Point point) {
        return (org.locationtech.jts.geom.Point)convert(point);
    }

    public static LineString convertFromJTS(org.locationtech.jts.geom.LineString lineString) {
        return (LineString)convert(lineString);
    }

    public static Point convertFromJTS(org.locationtech.jts.geom.Point point) {
        return (Point)convert(point);
    }

    private static Geometry convert(Point point) {
        return FACTORY.createPoint(convert(point.getCoordinates()));
    }

    private static Geometry convert(LineString lineString) {
        return FACTORY.createLineString(convert(lineString.getCoordinates()));
    }

    private static Coordinate convert(double[] c) {
        return (c.length == 2)
                ? new Coordinate(c[0], c[1])
                : new Coordinate(c[0], c[1], c[2]);
    }

    private static Coordinate[] convert(double[][] ca) {
        Coordinate[] coordinates = new Coordinate[ca.length];
        for (int i = 0; i < ca.length; i++) {
            coordinates[i] = convert(ca[i]);
        }
        return coordinates;
    }

    private static Point convert(org.locationtech.jts.geom.Point point) {
       return new Point(convert(point.getCoordinate()));
    }

    private static LineString convert(org.locationtech.jts.geom.LineString lineString) {
        return new LineString(convert(lineString.getCoordinates()));
    }

    private static double[] convert(Coordinate coordinate) {
        return (Double.isNaN( coordinate.getZ() ))
                ? new double[] { coordinate.x, coordinate.y }
                : new double[] { coordinate.x, coordinate.y, coordinate.getZ() };
    }

    private static double[][] convert(Coordinate[] coordinates) {
        double[][] array = new double[coordinates.length][];
        for (int i = 0; i < coordinates.length; i++) {
            array[i] = convert(coordinates[i]);
        }
        return array;
    }
}

package us.dot.its.jpo.conflictmonitor.monitor.utils;

import org.locationtech.jts.geom.Coordinate;
import org.locationtech.jts.geom.GeometryFactory;
import org.locationtech.jts.geom.PrecisionModel;
import us.dot.its.jpo.geojsonconverter.pojos.geojson.LineString;
import us.dot.its.jpo.geojsonconverter.pojos.geojson.Point;

/**
 * Methods to convert between GeoJSON and JTS.
 */
public class JTSConverter {

    public final static GeometryFactory FACTORY = new GeometryFactory(
            new PrecisionModel(PrecisionModel.FLOATING));

    public static org.locationtech.jts.geom.LineString convertToJTS(LineString lineString) {
        return FACTORY.createLineString(convert(lineString.getCoordinates()));
    }

    public static org.locationtech.jts.geom.Point convertToJTS(Point point) {
        return FACTORY.createPoint(convert(point.getCoordinates()));
    }

    public static LineString convertFromJTS(org.locationtech.jts.geom.LineString lineString) {
        return new LineString(convertPrimitive(lineString.getCoordinates()));
    }

    public static Point convertFromJTS(org.locationtech.jts.geom.Point point) {
        return new Point(convert(point.getCoordinate()));
    }


    private static Coordinate convert(Double[] c) {
        return (c.length == 2)
                ? new Coordinate(c[0], c[1])
                : new Coordinate(c[0], c[1], c[2]);
    }

    private static Coordinate[] convert(double[][] ca) {
        Coordinate[] coordinates = new Coordinate[ca.length];
        for (int i = 0; i < ca.length; i++) {
            coordinates[i] = convertPrimitive(ca[i]);
        }
        return coordinates;
    }

    private static Double[] convert(Coordinate coordinate) {
        return Double.isNaN( coordinate.getZ() )
                ? new Double[] { coordinate.x, coordinate.y }
                : new Double[] { coordinate.x, coordinate.y, coordinate.getZ() };
    }



    private static Coordinate convertPrimitive(double[] c) {
        return (c.length == 2)
                ? new Coordinate(c[0], c[1])
                : new Coordinate(c[0], c[1], c[2]);
    }

    private static double[] convertPrimitive(Coordinate coordinate) {
        return Double.isNaN( coordinate.getZ() )
                ? new double[] { coordinate.x, coordinate.y }
                : new double[] { coordinate.x, coordinate.y, coordinate.getZ() };
    }

    private static double[][] convertPrimitive(Coordinate[] coordinates) {
        double[][] array = new double[coordinates.length][];
        for (int i = 0; i < coordinates.length; i++) {
            array[i] = convertPrimitive(coordinates[i]);
        }
        return array;
    }
}

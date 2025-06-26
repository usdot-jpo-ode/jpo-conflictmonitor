package us.dot.its.jpo.conflictmonitor.monitor.models.map;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.locationtech.jts.geom.CoordinateXY;
import org.locationtech.jts.geom.Envelope;
import us.dot.its.jpo.conflictmonitor.monitor.models.IntersectionRegion;
import us.dot.its.jpo.conflictmonitor.monitor.utils.JTSConverter;
import us.dot.its.jpo.geojsonconverter.pojos.geojson.LineString;
import us.dot.its.jpo.geojsonconverter.pojos.geojson.map.MapFeatureCollection;
import us.dot.its.jpo.geojsonconverter.pojos.geojson.map.ProcessedMap;

/**
 * Light-weight object to store MAP bounding box in the Global Store.
 * Contains bounding box coordinates, intersection ID, region, timestamp, and origin IP.
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class MapBoundingBox {

    /** Timestamp of the MAP message (milliseconds since epoch). */
    long timestamp;
    /** Intersection ID associated with the MAP. */
    int intersectionId;
    /** Region code for the intersection. */
    int region;
    /** Origin IP address of the MAP message. */
    String originIp;
    /** Minimum longitude of the bounding box. */
    double minLongitude;
    /** Minimum latitude of the bounding box. */
    double minLatitude;
    /** Maximum longitude of the bounding box. */
    double maxLongitude;
    /** Maximum latitude of the bounding box. */
    double maxLatitude;

    /**
     * Constructs a MapBoundingBox from a ProcessedMap object.
     * Extracts intersection ID, region, origin IP, timestamp, and bounding box coordinates.
     *
     * @param map the processed MAP object containing geometry and properties
     */
    public MapBoundingBox(ProcessedMap<LineString> map) {
        if (map.getProperties() != null) {
            if (map.getProperties().getIntersectionId() != null) {
                intersectionId = map.getProperties().getIntersectionId().intValue();
            }
            if (map.getProperties().getRegion() != null) {
                region = map.getProperties().getRegion().intValue();
            }
            originIp = map.getProperties().getOriginIp();
            if (map.getProperties().getTimeStamp() != null) {
                timestamp = map.getProperties().getTimeStamp().toInstant().toEpochMilli();
            }
        }
        var mapEnvelope = envelopeForMap(map);
        minLongitude = mapEnvelope.getMinX();
        maxLongitude = mapEnvelope.getMaxX();
        minLatitude = mapEnvelope.getMinY();
        maxLatitude = mapEnvelope.getMaxY();
    }

    /**
     * Returns the IntersectionRegion object for this bounding box.
     *
     * @return IntersectionRegion containing intersectionId and region
     */
    public IntersectionRegion intersectionRegion() {
        return  new IntersectionRegion(intersectionId, region);
    }

    /**
     * Returns the bounding box as a JTS Envelope.
     *
     * @return Envelope representing the bounding box
     */
    public Envelope envelope() {
        return new Envelope(minLongitude, maxLongitude, minLatitude, maxLatitude);
    }

    /**
     * Computes the bounding box envelope for a given ProcessedMap.
     *
     * @param map the processed MAP object
     * @return Envelope representing the bounding box of all features in the map
     */
    public static Envelope envelopeForMap(ProcessedMap<LineString> map) {
        MapFeatureCollection<LineString> features = map.getMapFeatureCollection();

        // Get the overall bounding box of all the line features
        Envelope envelope = new Envelope();
        if (features != null && features.getFeatures() != null) {
            for (var feature : features.getFeatures()) {
                LineString geom = feature.getGeometry();
                org.locationtech.jts.geom.LineString jtsGeom = JTSConverter.convertToJTS(geom);
                Envelope featureEnvelope = jtsGeom.getEnvelopeInternal();
                envelope.expandToInclude(featureEnvelope);
            }
        }
        return envelope;
    }

    /**
     * Returns the bounding box as a JTS Polygon.
     *
     * @return Polygon representing the bounding box
     */
    @JsonIgnore
    public org.locationtech.jts.geom.Polygon boundingPolygon() {
        Envelope envelope = envelope();
        var minX = envelope.getMinX();
        var minY = envelope.getMinY();
        var maxX = envelope.getMaxX();
        var maxY = envelope.getMaxY();

        var coordinates = new CoordinateXY[]{
                new CoordinateXY(minX, minY),
                new CoordinateXY(minX, maxY),
                new CoordinateXY(maxX, maxY),
                new CoordinateXY(maxX, minY),
                new CoordinateXY(minX, minY)
        };

        return JTSConverter.FACTORY.createPolygon(coordinates);
    }

    /**
     * Returns the bounding polygon as a WKT (Well-Known Text) string.
     *
     * @return String representation of the bounding polygon in WKT format
     */
    public String getBoundingPolygonWkt() {
        var polygon = boundingPolygon();
        return polygon.toString();
    }

}

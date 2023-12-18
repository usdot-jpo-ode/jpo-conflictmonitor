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
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class MapBoundingBox {

    long timestamp;
    int intersectionId;
    int region;
    String originIp;
    double minLongitude;
    double minLatitude;
    double maxLongitude;
    double maxLatitude;

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

    public IntersectionRegion intersectionRegion() {
        return  new IntersectionRegion(intersectionId, region);
    }

    public Envelope envelope() {
        return new Envelope(minLongitude, maxLongitude, minLatitude, maxLatitude);
    }

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

    public String getBoundingPolygonWkt() {
        var polygon = boundingPolygon();
        return polygon.toString();
    }



}

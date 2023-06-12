package us.dot.its.jpo.conflictmonitor.monitor.models.map;

import org.locationtech.jts.geom.CoordinateXY;
import org.locationtech.jts.geom.Envelope;
import org.locationtech.jts.index.quadtree.Quadtree;
import org.springframework.stereotype.Component;
import us.dot.its.jpo.conflictmonitor.monitor.utils.JTSConverter;
import us.dot.its.jpo.geojsonconverter.pojos.geojson.LineString;
import us.dot.its.jpo.geojsonconverter.pojos.geojson.map.MapFeatureCollection;
import us.dot.its.jpo.geojsonconverter.pojos.geojson.map.ProcessedMap;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;
import static java.util.Objects.equals;

/**
 * Spatial Index for MAP messages.
 *
 * Use a Quadtree data structure so that items can be dynamically inserted into the index.
 */
@Component
public class MapIndex {

    private final Quadtree quadtree;

    public MapIndex() {
        quadtree = new Quadtree();
    }

    public Quadtree getQuadtree() {
        return quadtree;
    }

    public void insert(ProcessedMap map) {
        Envelope envelope = getEnvelope(map);
        Integer intersectionId = null;
        Integer regionId = null;
        if (map.getProperties() != null) {
            intersectionId = map.getProperties().getIntersectionId();
            regionId = map.getProperties().getRegion();
        }

        // If there is already a map for the intersection, replace it
        List items = quadtree.query(envelope);
        if (items != null && items.size() > 0) {
            for (var item : items) {

                if (!(item instanceof ProcessedMap)) continue;
                var mapItem = (ProcessedMap)item;
                if (mapItem.getProperties() == null) continue;

                Integer itemIntersectionId = mapItem.getProperties().getIntersectionId();
                Integer itemRegionId = mapItem.getProperties().getRegion();
                if (Objects.equals(intersectionId, itemIntersectionId) && Objects.equals(regionId, itemRegionId)) {
                    quadtree.remove(envelope, item);
                }

            }
        }

        quadtree.insert(envelope, map);
    }

    public static Envelope getEnvelope(ProcessedMap<LineString> map) {
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

    public static org.locationtech.jts.geom.Polygon getBoundingPolygon(ProcessedMap map) {
        Envelope envelope = getEnvelope(map);
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

//    /**
//     * Check if a point is inside a MAP bounding box.
//     *
//     * @param coords - Lon/Lat coordinates of the point
//     *
//     * @return If the point is inside a Map bounding box, return the {@link ProcessedMap}, otherwise
//     * return null if no MAP's bounding box contains the point.
//     */
//    public ProcessedMap mapContainingPoint(CoordinateXY coords) {
//        final org.locationtech.jts.geom.Point point = JTSConverter.FACTORY.createPoint(coords);
//        final Envelope pointEnvelope = point.getEnvelopeInternal();
//        pointEnvelope.expandBy(1e-6);
//
//        // Query the spatial index to get candidate MAPs that might contain the point
//        var items = quadtree.query(pointEnvelope);
//
//        // Not all candidates necessarily contain the point, so check to be sure
//        for (var item : items) {
//            ProcessedMap map = (ProcessedMap)item;
//            var boundingPolygon = getBoundingPolygon(map);
//            if (boundingPolygon.contains(point)) {
//                return map;
//            }
//        }
//        return null;
//    }

    /**
     * List MAPs whose bounding boxes contain the point.
     *
     * @param coords - Lon/Lat coordinates of the point
     *
     * @return A list of {@link ProcessedMap}s that contain the point.
     */
    public List<ProcessedMap<LineString>> mapsContainingPoint(CoordinateXY coords) {
        List<ProcessedMap<LineString>> mapList = new ArrayList<>();
        final org.locationtech.jts.geom.Point point = JTSConverter.FACTORY.createPoint(coords);
        final Envelope pointEnvelope = point.getEnvelopeInternal();
        pointEnvelope.expandBy(1e-6);

        // Query the spatial index to get candidate MAPs that might contain the point
        var items = quadtree.query(pointEnvelope);

        // Not all candidates necessarily contain the point, so check to be sure
        for (var item : items) {
            var map = (ProcessedMap<LineString>)item;
            var boundingPolygon = getBoundingPolygon(map);
            if (boundingPolygon.contains(point)) {
                mapList.add(map);
            }
        }
        return mapList;
    }

}

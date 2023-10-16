package us.dot.its.jpo.conflictmonitor.monitor.models.map;

import org.locationtech.jts.geom.CoordinateXY;
import org.locationtech.jts.geom.Envelope;
import org.locationtech.jts.index.quadtree.Quadtree;
import org.springframework.stereotype.Component;
import us.dot.its.jpo.conflictmonitor.monitor.utils.JTSConverter;
import us.dot.its.jpo.geojsonconverter.pojos.geojson.map.ProcessedMap;

import java.util.ArrayList;
import java.util.List;

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

    public void insert(MapBoundingBox map) {
        Envelope envelope = removeIfPresent(map);
        quadtree.insert(envelope, map);
    }

    public void remove(MapBoundingBox map) {
        removeIfPresent(map);
    }

    /**
     * Remove the item if present and return the Envelope
     * @param map
     * @return {@link Envelope} of the {@link ProcessedMap}
     */
    private Envelope removeIfPresent(MapBoundingBox map) {
        Envelope envelope = map.envelope();

       int intersectionId = map.getIntersectionId();
       int regionId = map.getRegion();


        // If there is already a map for the intersection, remove it
        List items = quadtree.query(envelope);
        if (items != null && items.size() > 0) {
            for (var item : items) {
                if (!(item instanceof MapBoundingBox)) continue;
                var mapItem = (MapBoundingBox)item;
                int itemIntersectionId = mapItem.getIntersectionId();
                int itemRegionId = mapItem.getRegion();
                if (intersectionId == itemIntersectionId && regionId == itemRegionId) {
                    quadtree.remove(envelope, item);
                }
            }
        }
        return envelope;
    }





    /**
     * List MAPs whose bounding boxes contain the point.
     *
     * @param coords - Lon/Lat coordinates of the point
     *
     * @return A list of {@link ProcessedMap}s that contain the point.
     */
    public List<MapBoundingBox> mapsContainingPoint(CoordinateXY coords) {
        List<MapBoundingBox> mapList = new ArrayList<>();
        final org.locationtech.jts.geom.Point point = JTSConverter.FACTORY.createPoint(coords);
        final Envelope pointEnvelope = point.getEnvelopeInternal();
        pointEnvelope.expandBy(1e-6);

        // Query the spatial index to get candidate MAPs that might contain the point
        var items = quadtree.query(pointEnvelope);

        // Not all candidates necessarily contain the point, so check to be sure
        for (var item : items) {
            var map = (MapBoundingBox)item;
            var boundingPolygon = map.boundingPolygon();
            if (boundingPolygon.contains(point)) {
                mapList.add(map);
            }
        }
        return mapList;
    }

}

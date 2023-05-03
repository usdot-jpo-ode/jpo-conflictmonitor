package us.dot.its.jpo.conflictmonitor.monitor.models.map;

import org.locationtech.jts.index.quadtree.Quadtree;
import org.springframework.stereotype.Component;
import us.dot.its.jpo.geojsonconverter.pojos.geojson.map.MapFeatureCollection;
import us.dot.its.jpo.geojsonconverter.pojos.geojson.map.ProcessedMap;

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

    public void insert(ProcessedMap map) {
        MapFeatureCollection features = map.getMapFeatureCollection();
        if (features != null) {
            for (var feature : features.getFeatures()) {
                var geom = feature.getGeometry();

            }
        }
    }

}

package us.dot.its.jpo.conflictmonitor.monitor.models;

import us.dot.its.jpo.geojsonconverter.pojos.geojson.map.MapFeatureCollection;
import us.dot.its.jpo.geojsonconverter.pojos.spat.ProcessedSpat;

public class SpatMap {
    private ProcessedSpat spat;
    private MapFeatureCollection map;

    public SpatMap(ProcessedSpat spat, MapFeatureCollection map) {
        this.spat = spat;
        this.map = map;
    }

    

    public ProcessedSpat getSpat() {
        return spat;
    }

    public void setSpat(ProcessedSpat spat) {
        this.spat = spat;
    }
    
    public MapFeatureCollection getMap() {
        return map;
    }

    public void setMap(MapFeatureCollection map) {
        this.map = map;
    }

}

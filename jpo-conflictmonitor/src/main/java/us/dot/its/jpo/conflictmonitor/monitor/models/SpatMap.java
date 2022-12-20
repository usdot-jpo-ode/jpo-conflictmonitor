package us.dot.its.jpo.conflictmonitor.monitor.models;

import us.dot.its.jpo.geojsonconverter.pojos.geojson.map.ProcessedMap;
import us.dot.its.jpo.geojsonconverter.pojos.spat.ProcessedSpat;

public class SpatMap {
    private ProcessedSpat spat;
    private ProcessedMap map;

    public SpatMap(ProcessedSpat spat, ProcessedMap map) {
        this.spat = spat;
        this.map = map;
    }

    public ProcessedSpat getSpat() {
        return spat;
    }

    public void setSpat(ProcessedSpat spat) {
        this.spat = spat;
    }
    
    public ProcessedMap getMap() {
        return map;
    }

    public void setMap(ProcessedMap map) {
        this.map = map;
    }

}

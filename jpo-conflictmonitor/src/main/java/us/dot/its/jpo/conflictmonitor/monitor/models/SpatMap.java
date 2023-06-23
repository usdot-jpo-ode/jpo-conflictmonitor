package us.dot.its.jpo.conflictmonitor.monitor.models;

import lombok.AllArgsConstructor;
import lombok.Generated;
import lombok.Getter;
import lombok.Setter;
import us.dot.its.jpo.geojsonconverter.pojos.geojson.map.ProcessedMap;
import us.dot.its.jpo.geojsonconverter.pojos.spat.ProcessedSpat;

@Getter
@Setter
@AllArgsConstructor
@Generated
public class SpatMap {
    private ProcessedSpat spat;
    private ProcessedMap map;
}

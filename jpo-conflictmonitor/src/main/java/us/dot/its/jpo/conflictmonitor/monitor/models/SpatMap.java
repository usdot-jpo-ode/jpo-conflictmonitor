package us.dot.its.jpo.conflictmonitor.monitor.models;

import lombok.*;
import us.dot.its.jpo.geojsonconverter.pojos.geojson.map.ProcessedMap;
import us.dot.its.jpo.geojsonconverter.pojos.spat.ProcessedSpat;

@Getter
@Setter
@AllArgsConstructor
@Generated
@ToString
public class SpatMap {
    private ProcessedSpat spat;
    private ProcessedMap<?> map;
}

package us.dot.its.jpo.conflictmonitor.monitor.models;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import us.dot.its.jpo.geojsonconverter.partitioner.RsuIntersectionKey;

@Getter
@Setter
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
public class VehicleEventKey extends RsuIntersectionKey {
    String vehicleId;
}

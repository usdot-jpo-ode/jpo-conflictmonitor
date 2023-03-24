package us.dot.its.jpo.conflictmonitor.monitor.models;



import lombok.AllArgsConstructor;
import lombok.Generated;
import lombok.Getter;
import lombok.Setter;
import us.dot.its.jpo.conflictmonitor.monitor.models.Intersection.Intersection;
import us.dot.its.jpo.conflictmonitor.monitor.models.bsm.BsmAggregator;
import us.dot.its.jpo.conflictmonitor.monitor.models.spat.SpatAggregator;

@Getter
@Setter
@AllArgsConstructor
@Generated
public class VehicleEvent {
    private BsmAggregator bsms;
    private SpatAggregator spats;
    private Intersection intersection;
}

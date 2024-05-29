package us.dot.its.jpo.conflictmonitor.monitor.models.concurrent_permissive;


import lombok.*;
import us.dot.its.jpo.conflictmonitor.monitor.models.IntersectionRegion;

@Data
@Generated
public class AllowedConcurrentPermissive {


    ConnectedLanesPair connectedLanesPair;
    boolean allowConcurrent = true;


}
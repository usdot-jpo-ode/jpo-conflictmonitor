package us.dot.its.jpo.conflictmonitor.monitor.models.concurrent_permissive;

import lombok.AllArgsConstructor;
import lombok.Data;

/**
 * A pair of connected lane IDs
 */
@Data
@AllArgsConstructor
public class ConnectedLanes {
    int ingressLaneID;
    int egressLaneID;
}

package us.dot.its.jpo.conflictmonitor.monitor.models.concurrent_permissive;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * A pair of connected lane IDs
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class ConnectedLanes {
    int ingressLaneID;
    int egressLaneID;
}

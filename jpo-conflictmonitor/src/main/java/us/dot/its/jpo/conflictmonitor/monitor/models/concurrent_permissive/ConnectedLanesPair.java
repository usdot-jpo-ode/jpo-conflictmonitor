package us.dot.its.jpo.conflictmonitor.monitor.models.concurrent_permissive;

import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Sets;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import us.dot.its.jpo.conflictmonitor.monitor.models.IntersectionRegion;

import java.util.Objects;
import java.util.Set;

/**
 * An unordered pair of connected lanes in the same intersection.
 * Equals and HashCode are defined so the order of the 'first' and 'second' set of connected lanes doesn't matter.
 */
@Getter
@Setter
@ToString
public class ConnectedLanesPair {

    /** 
     * An integer representing the intersectionID of the lane pair. This value should correspond the intersectionID of the MAP message
     */
    Integer intersectionID;

    /** 
     * An integer representing the roadRegulatorID of the lane pair. This value corresponds to the region of the MAP message. Since MAP message regions are going away this field is deprecated. Set to -1 if unknown.
     */
    @Deprecated
    Integer roadRegulatorID;

    /** 
     * A Connecting Lane object representing the first pair of connecting lanes in the message
     */
    ConnectedLanes first;

    /** 
     * A Connecting Lane object representing the second pair of connecting lanes in the message
     */
    ConnectedLanes second;

    public ConnectedLanesPair() {}

    /** 
     * @param intersectionID integer representing the ID of the intersection from the MAP message
     * @param roadRegulatorID integer representing the region of the intersection from the MAP message. Use -1 if unknown.
     * @param firstIngressLaneId int representing the lane id of the first ingress lane
     * @param firstEgressLaneId int representing the lane id of the first egress lane
     * @param secondIngressLaneId int representing the lane id of the second ingress lane
     * @param secondEgressLaneId int representing the lane id of the second egress lane
     */
    public ConnectedLanesPair(Integer intersectionID, Integer roadRegulatorID, int firstIngressLaneId, int firstEgressLaneId,
        int secondIngressLaneId, int secondEgressLaneId) {
        this.intersectionID = intersectionID;
        this.roadRegulatorID = roadRegulatorID;
        this.first = new ConnectedLanes(firstIngressLaneId, firstEgressLaneId);
        this.second = new ConnectedLanes(secondIngressLaneId, secondEgressLaneId);
    }

    /** 
     * @return a Set of Connecting Lanes containing all of the unique connecting lane objects generated from the first and second variables.
     */
    public Set<ConnectedLanes> connectedLanesSet() {
        var setBuilder = ImmutableSet.<ConnectedLanes>builder();
        if (first != null) setBuilder.add(first);
        if (second != null) setBuilder.add(second);
        return setBuilder.build();
    }

    /** 
     * @return IntersectionRegion object containing the intersectionID and region from this connected lane pair.
     */
    public IntersectionRegion intersectionRegion() {
        return new IntersectionRegion(intersectionID, roadRegulatorID);
    }

    @Override
    public int hashCode() {
        return Objects.hash(intersectionRegion(), connectedLanesSet());
    }

    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof ConnectedLanesPair other)) return false;

        // Compare intersection/region
        IntersectionRegion thisIntersection = intersectionRegion();
        IntersectionRegion otherIntersection = other.intersectionRegion();
        if (!Objects.equals(thisIntersection, otherIntersection)) {
            return false;
        }

        // If intersection/region are the same, compare ConnectedLanes
        Set<ConnectedLanes> thisSet = connectedLanesSet();
        Set<ConnectedLanes> otherSet = other.connectedLanesSet();
        Set<ConnectedLanes> diff = Sets.symmetricDifference(thisSet, otherSet);
        return diff.isEmpty();
    }


}

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
 * Equals and HashCode are defined so the order of 'first' and 'second' set of connected lanes doesn't matter.
 */
@Getter
@Setter
@ToString
public class ConnectedLanesPair {

    Integer intersectionID;
    Integer roadRegulatorID;

    ConnectedLanes first;
    ConnectedLanes second;

    public ConnectedLanesPair() {}

    public ConnectedLanesPair(Integer intersectionID, Integer roadRegulatorID, int firstIngressLaneId, int firstEgressLaneId,
        int secondIngressLaneId, int secondEgressLaneId) {
        this.intersectionID = intersectionID;
        this.roadRegulatorID = roadRegulatorID;
        this.first = new ConnectedLanes(firstIngressLaneId, firstEgressLaneId);
        this.second = new ConnectedLanes(secondIngressLaneId, secondEgressLaneId);
    }

    public Set<ConnectedLanes> connectedLanesSet() {
        var setBuilder = ImmutableSet.<ConnectedLanes>builder();
        if (first != null) setBuilder.add(first);
        if (second != null) setBuilder.add(second);
        return setBuilder.build();
    }

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

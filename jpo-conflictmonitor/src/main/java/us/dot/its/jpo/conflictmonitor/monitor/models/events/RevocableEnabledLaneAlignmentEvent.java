package us.dot.its.jpo.conflictmonitor.monitor.models.events;

import us.dot.its.jpo.ode.plugin.j2735.J2735LaneTypeAttributes;
import us.dot.its.jpo.ode.plugin.j2735.J2735MovementPhaseState;

import java.util.Map;
import java.util.Set;

public class RevocableEnabledLaneAlignmentEvent extends Event {

    public RevocableEnabledLaneAlignmentEvent() {
        super("RevocableEnabledLaneAlignment");
    }

    private String source;
    private long timestamp;

    /**
     * Map of LaneID to DE_LaneTypeAttributes, including only lanes with
     * the 'revocable' bit set.
     */
    Map<Integer, J2735LaneTypeAttributes> laneTypeAttributes;

    /**
     * Set of LanIDs with the 'revocable' bit set in the MAP message.
     */
    Set<Integer> revocableLaneList() {
        return (laneTypeAttributes == null) ? Set.of() : laneTypeAttributes.keySet();
    }

    /**
     * Set of enabled Lane IDs from the SPAT message.
     */
    Set<Integer> enabledLaneList;

    /**
     * Movement Phase State from the SPAT message
     */
    J2735MovementPhaseState eventState;

}

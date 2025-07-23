package us.dot.its.jpo.conflictmonitor.monitor.models.events.revocable_enabled_lane_alignment;

import lombok.EqualsAndHashCode;
import lombok.Generated;
import lombok.Getter;
import lombok.Setter;
import us.dot.its.jpo.conflictmonitor.monitor.models.events.Event;
import us.dot.its.jpo.ode.plugin.j2735.J2735MovementPhaseState;

import java.util.Set;

@Getter
@Setter
@EqualsAndHashCode(callSuper = true)
@Generated
public class RevocableEnabledLaneAlignmentEvent extends Event {

    public RevocableEnabledLaneAlignmentEvent() {
        super("RevocableEnabledLaneAlignment");
    }

    /**
     * The SPAT source (usually IP address)
     */
    private String source;

    /**
     * The timestamp of the SPAT
     */
    private long timestamp;

    /**
     * Map of LaneID to DE_LaneTypeAttributes, including all lanes, revocable or not
     */
    private LaneTypeAttributesMap laneTypeAttributes;

    /**
     * Set of LanIDs with the 'revocable' bit set in the MAP message.
     */
    private Set<Integer> revocableLaneList;

    /**
     * Set of enabled Lane IDs from the SPAT message.
     */
    private Set<Integer> enabledLaneList;

}

package us.dot.its.jpo.conflictmonitor.monitor.models.bsm;

import lombok.*;
import us.dot.its.jpo.ode.model.OdeBsmData;

@Getter
@Setter
@EqualsAndHashCode
@ToString
@Generated
public class BsmEvent {
    private OdeBsmData startingBsm;
    private OdeBsmData endingBsm;
    private Long startingBsmTimestamp;
    private Long endingBsmTimestamp;
    private String wktPath;
    private String wktMapBoundingBox;
    private boolean inMapBoundingBox;
    private Integer intersectionID;

    /**
     * Timestamp to use with wall clock punctuator
     */
    private long wallClockTimestamp;


    /** 
     * Creates a new BsmEvent Object;
     */
    public BsmEvent() {}

    /** 
     * Creates a new BsmEvent object with the supplied starting BSM. This is typically used when a BSM event is in progress, and the final BSM has not yet been received.
     * @param startingBsm The first BSM included in this event
     */
    public BsmEvent(OdeBsmData startingBsm){
        this.startingBsm = startingBsm;
    }

    /** 
     * Creates a new BsmEvent object with the supplied starting and ending BSMs. 
     * @param startingBsm The first BSM included in this event
     * @param endingBSM The last BSM included in this event
     */
    public BsmEvent(OdeBsmData startingBsm, OdeBsmData endingBsm){
        this.startingBsm = startingBsm;
        this.endingBsm = endingBsm;
    }

}

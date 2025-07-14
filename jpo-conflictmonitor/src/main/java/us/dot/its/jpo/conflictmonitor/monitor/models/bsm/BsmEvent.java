package us.dot.its.jpo.conflictmonitor.monitor.models.bsm;

import lombok.EqualsAndHashCode;
import lombok.Generated;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import us.dot.its.jpo.geojsonconverter.pojos.geojson.Point;
import us.dot.its.jpo.geojsonconverter.pojos.geojson.bsm.ProcessedBsm;

@Getter
@Setter
@EqualsAndHashCode
@ToString
@Generated
public class BsmEvent {
    private ProcessedBsm<Point> startingBsm;
    private ProcessedBsm<Point> endingBsm;
    private Long startingBsmTimestamp;
    private Long endingBsmTimestamp;
    private String wktPath;
    private String wktMapBoundingBox;
    private boolean inMapBoundingBox;
    private Integer intersectionID;

    /**
     * Timestamp to use with wall clock punctuator
     * TODO Remove/use TopologyTestDriver support
     */
    private long wallClockTimestamp;

    /**
     * Creates a new BsmEvent Object;
     */
    public BsmEvent() {
    }

    /**
     * Creates a new BsmEvent object with the supplied starting BSM. This is
     * typically used when a BSM event is in progress, and the final BSM has not yet
     * been received.
     * 
     * @param startingBsm The first BSM included in this event
     */
    public BsmEvent(ProcessedBsm<Point> startingBsm) {
        this.startingBsm = startingBsm;
    }

    /**
     * Creates a new BsmEvent object with the supplied starting and ending BSMs.
     * 
     * @param startingBsm The first BSM included in this event
     * @param endingBsm   The last BSM included in this event
     */
    public BsmEvent(ProcessedBsm<Point> startingBsm, ProcessedBsm<Point> endingBsm) {
        this.startingBsm = startingBsm;
        this.endingBsm = endingBsm;
    }

}

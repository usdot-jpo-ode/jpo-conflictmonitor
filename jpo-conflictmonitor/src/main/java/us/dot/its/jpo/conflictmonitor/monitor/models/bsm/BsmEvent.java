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

    public BsmEvent() {}

    public BsmEvent(ProcessedBsm<Point> startingBsm){
        this.startingBsm = startingBsm;
    }

    public BsmEvent(ProcessedBsm<Point> startingBsm, ProcessedBsm<Point> endingBsm){
        this.startingBsm = startingBsm;
        this.endingBsm = endingBsm;
    }

}

package us.dot.its.jpo.conflictmonitor.monitor.models.events.minimum_data;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@EqualsAndHashCode(callSuper = true)
public class RtcmMinimumDataEventAggregation
        extends BaseMinimumDataEventAggregation<RtcmMinimumDataEvent> {
    public RtcmMinimumDataEventAggregation() {
        super("RtcmMinimumDataAggregation");
    }
}

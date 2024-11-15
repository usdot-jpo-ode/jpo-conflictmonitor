package us.dot.its.jpo.conflictmonitor.monitor.models.events.minimum_data;

import lombok.EqualsAndHashCode;
import lombok.Generated;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@EqualsAndHashCode(callSuper = true)
@Generated
public class SpatMinimumDataEventAggregation extends BaseMinimumDataEventAggregation<SpatMinimumDataEvent> {

    public SpatMinimumDataEventAggregation() {
        super("SpatMinimumDataAggregation");
    }

}

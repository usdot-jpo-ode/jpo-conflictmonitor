package us.dot.its.jpo.conflictmonitor.monitor.models.bsm;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class TimeAcceleration {
    private long time;
    private double lateralAcceleration;
    private double longitudinalAcceleration;
    private double verticalAcceleration;
}
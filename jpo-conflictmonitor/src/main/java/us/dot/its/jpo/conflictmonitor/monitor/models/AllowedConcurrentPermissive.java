package us.dot.its.jpo.conflictmonitor.monitor.models;


import lombok.AllArgsConstructor;
import lombok.Generated;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@Generated
public class AllowedConcurrentPermissive {
    private Integer intersectionID;
    private Integer roadRegulatorID;

    private int firstIngressLane;
    private int secondIngressLane;

    private int firstEgressLane;
    private int secondEgressLane;

    private boolean allowConcurrent = true;
}
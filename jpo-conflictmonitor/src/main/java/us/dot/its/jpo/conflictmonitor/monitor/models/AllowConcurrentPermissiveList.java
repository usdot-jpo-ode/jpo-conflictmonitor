package us.dot.its.jpo.conflictmonitor.monitor.models;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Generated;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@Generated
public class AllowConcurrentPermissiveList {
    private List<AllowedConcurrentPermissive> allowedConcurrentList;
}

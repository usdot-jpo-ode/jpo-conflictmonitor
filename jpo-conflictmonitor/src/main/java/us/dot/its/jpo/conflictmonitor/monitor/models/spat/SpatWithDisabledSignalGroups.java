package us.dot.its.jpo.conflictmonitor.monitor.models.spat;

import us.dot.its.jpo.geojsonconverter.pojos.spat.ProcessedSpat;

import java.util.Set;

public record SpatWithDisabledSignalGroups (
        ProcessedSpat processedSpat,
        Set<Integer> disabledSignalGroups) {
}

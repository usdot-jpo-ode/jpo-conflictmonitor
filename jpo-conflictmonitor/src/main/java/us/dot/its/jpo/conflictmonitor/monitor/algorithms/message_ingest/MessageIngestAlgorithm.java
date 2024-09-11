package us.dot.its.jpo.conflictmonitor.monitor.algorithms.message_ingest;

import us.dot.its.jpo.conflictmonitor.monitor.algorithms.ConfigurableAlgorithm;
import us.dot.its.jpo.conflictmonitor.monitor.algorithms.spat_transition.SpatTransitionAlgorithm;
import us.dot.its.jpo.conflictmonitor.monitor.models.map.MapIndex;

public interface MessageIngestAlgorithm
    extends ConfigurableAlgorithm<MessageIngestParameters> {
    MapIndex getMapIndex();
    void setMapIndex(MapIndex mapIndex);
    void validate();

    // Plug-in Spat Transition algorithm
    void setSpatTransitionAlgorithm(SpatTransitionAlgorithm spatTransitionAlgorithm);
    SpatTransitionAlgorithm getSpatTransitionAlgorithm();
}

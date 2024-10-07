package us.dot.its.jpo.conflictmonitor.monitor.algorithms.message_ingest;

import us.dot.its.jpo.conflictmonitor.monitor.algorithms.ConfigurableAlgorithm;
import us.dot.its.jpo.conflictmonitor.monitor.algorithms.event_state_progression.EventStateProgressionAlgorithm;
import us.dot.its.jpo.conflictmonitor.monitor.models.map.MapIndex;

public interface MessageIngestAlgorithm
    extends ConfigurableAlgorithm<MessageIngestParameters> {
    MapIndex getMapIndex();
    void setMapIndex(MapIndex mapIndex);
    void validate();

    // Plug-in Spat Transition algorithm
    void setSpatTransitionAlgorithm(EventStateProgressionAlgorithm spatTransitionAlgorithm);
    EventStateProgressionAlgorithm getSpatTransitionAlgorithm();
}

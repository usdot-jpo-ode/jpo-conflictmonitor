package us.dot.its.jpo.conflictmonitor.monitor.algorithms.message_ingest;

import us.dot.its.jpo.conflictmonitor.monitor.algorithms.Algorithm;
import us.dot.its.jpo.conflictmonitor.monitor.algorithms.ConfigurableAlgorithm;
import us.dot.its.jpo.conflictmonitor.monitor.algorithms.event_state_progression.EventStateProgressionAlgorithm;
import us.dot.its.jpo.conflictmonitor.monitor.models.map.MapIndex;

/**
 * Interface for message ingest algorithms.
 * <p>
 * Extends the base {@link Algorithm} interface for SPaT validation parameters.
 */
public interface MessageIngestAlgorithm
    extends ConfigurableAlgorithm<MessageIngestParameters> {

    /**
     * Gets the map index used for message ingest.
     *
     * @return the MapIndex instance for the message ingest algorithm
     */
    MapIndex getMapIndex();

    /**
     * Sets the map index used for message ingest.
     */
    void setMapIndex(MapIndex mapIndex);

    /**
     * Helper function to validate the MAP and SPaT ingest messages.
     */
    void validate();

    /**
     * Sets the SPaT transition algorithm used for validation.
     *
     * @param spatTransitionAlgorithm the SPaT transition algorithm to set
     */
    void setEventStateProgressionAlgorithm(EventStateProgressionAlgorithm spatTransitionAlgorithm);
    
    /**
     * Gets the Event State Progression Algorithm used for message ingest.
     *
     * @return the EventStateProgressionAlgorithm instance for the message ingest algorithm
     */
    EventStateProgressionAlgorithm getEventStateProgressionAlgorithm();
}

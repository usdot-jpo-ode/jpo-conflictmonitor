package us.dot.its.jpo.conflictmonitor.monitor.algorithms.bsm_event;

import us.dot.its.jpo.conflictmonitor.monitor.algorithms.Algorithm;
import us.dot.its.jpo.conflictmonitor.monitor.models.map.MapIndex;

/**
 * Interface for Bsm Event algorithms.
 * <p>
 * Extends the base {@link Algorithm} interface for BSM Event parameters.
 */
public interface BsmEventAlgorithm extends Algorithm<BsmEventParameters> {

    /**
     * Sets the MapIndex to be used by the BsmEventAlgorithm.
     *
     * @param mapIndex the mapIndex to set
     */
    void setMapIndex(MapIndex mapIndex);

}

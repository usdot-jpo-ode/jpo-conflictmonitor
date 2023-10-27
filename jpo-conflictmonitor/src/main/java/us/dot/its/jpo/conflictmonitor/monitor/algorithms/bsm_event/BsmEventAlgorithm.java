package us.dot.its.jpo.conflictmonitor.monitor.algorithms.bsm_event;

import us.dot.its.jpo.conflictmonitor.monitor.algorithms.ConfigurableAlgorithm;
import us.dot.its.jpo.conflictmonitor.monitor.models.map.MapIndex;


public interface BsmEventAlgorithm extends ConfigurableAlgorithm<BsmEventParameters> {

    void setMapIndex(MapIndex mapIndex);

}

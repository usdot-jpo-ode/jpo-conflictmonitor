package us.dot.its.jpo.conflictmonitor.monitor.models.config;

public interface IntersectionKey {

    /**
     * @return The Region / Road Regulator ID, or 0 if undefined.
     */
    int getRoadRegulatorID();

    /**
     * @return The Intersection ID
     */
    int getIntersectionID();
}

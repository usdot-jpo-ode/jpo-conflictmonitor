package us.dot.its.jpo.conflictmonitor.monitor.models.config;

/**
 * Type of runtime updates allowed for the configuration setting
 */
public enum UpdateType {
    
    /**
     * No updates allowed, the value is read-only.
     */
    READ_ONLY,
         
    /**
     * Updates are allowed to the default value.  Not customizable per intersection.
     */
    DEFAULT,
    
    /*
     * Both default value updates and intersection-specific customizations are allowed.
     */
    INTERSECTION

}

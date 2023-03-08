package us.dot.its.jpo.conflictmonitor.monitor.models.config;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.lang.annotation.ElementType;

/**
 * Conflict Monitor Configuration Data field-level annotation
 * 
 * <p>Marks default value fields in a config parameters class be
 * stored in the database with metadata (category, units and description) and
 * whether to receive updates at runtime.
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
public @interface ConfigData {
    public String key();
    public String category() default "";
    public UnitsEnum units() default UnitsEnum.NONE;
    public String description() default "";
    public UpdateType updateType() default UpdateType.READ_ONLY;
}

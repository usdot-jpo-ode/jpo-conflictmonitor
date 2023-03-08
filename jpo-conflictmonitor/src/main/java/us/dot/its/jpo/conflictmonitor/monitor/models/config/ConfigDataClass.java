package us.dot.its.jpo.conflictmonitor.monitor.models.config;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import org.springframework.beans.factory.annotation.Qualifier;

import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.ElementType;

/**
 * Conflict Monitor Configuration Data class-level annotation
 * 
 * <p>Marks a Parameters class that is stored in the database 
 * and causes the class to be indesxed by the 
 * {@link us.dot.its.jpo.conflictmonitor.monitor.algorithms.AlgorithmParameters} 
 * component, and can have fields annotated with {@link ConfigData} to be
 * updated at runtime.
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
@Qualifier  // Qualifier to enable the annotation to be found by Spring
public @interface ConfigDataClass {
    
}

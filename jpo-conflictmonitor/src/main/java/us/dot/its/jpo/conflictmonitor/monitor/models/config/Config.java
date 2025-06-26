package us.dot.its.jpo.conflictmonitor.monitor.models.config;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import lombok.EqualsAndHashCode;
import lombok.Generated;
import lombok.Getter;
import lombok.Setter;
import us.dot.its.jpo.geojsonconverter.DateJsonMapper;

/**
 * A class representing a config parameter for the Conflict Monitor System.
 */
@Getter
@Setter
@EqualsAndHashCode
@Generated
@JsonIgnoreProperties(value = { "_id", "_class" }, ignoreUnknown = true)
public abstract class Config<T> {

    private static final Logger logger = LoggerFactory.getLogger(Config.class);

    /** 
     * A String representing the unique Key to indentify this configuration parameter with
     */
    private String key;

    /** 
     * String describing what category of operations this parameter is a part of
     */
    private String category;

    /** 
     * Mutable Type for the value of this config parameter
     */
    private T value;

    /** 
     * String java name of the type of the value parameter.
     */
    private String type;

    /** 
     * UnitsEnum representing what unit the value is measured in. Possible units include both time units such as days, or seconds as well as distance units such as meters, or speed units such as feet per second.
     */
    private UnitsEnum units;

    /** 
     * String containing a user friendly description of what this parameter does.
     */
    private String description;

    /** 
     * UpdateType enum showing when and to what extent this parameter can be updated. Possible values are READ_ONLY, DEFAULT, and INTERSECTION. 
     * READ_ONLY: Value cannot be changed by user. 
     * DEFAULT: Value can be changed by users. Values may only be changed with Global Scope across all intersections.
     * INTERSECTION: Value can be changed by users. Value can be set on a per intersection basis.
     */
    private UpdateType updateType;
    

    /**
     * An Empty constructor used for Deserialization.
     */
    public Config(){
        
    }

    /**
     * All Arguments constructor used for creating new Config Parameters
     */
    public Config(String key, String category, T value, String type, UnitsEnum units, String description){
        this.key = key;
        this.category = category;
        this.value = value;
        this.type = type;
        this.units = units;
        this.description = description;
    }



    @Override
    public String toString() {
        ObjectMapper mapper = DateJsonMapper.getInstance();
        try {
            return mapper.writeValueAsString(this);
        } catch (JsonProcessingException e) {
            logger.error("Exception serializing JSON", e);
        }
        return "";
    }
}

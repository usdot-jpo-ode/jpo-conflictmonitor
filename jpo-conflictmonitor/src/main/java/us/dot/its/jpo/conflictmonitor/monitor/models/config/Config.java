package us.dot.its.jpo.conflictmonitor.monitor.models.config;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;

import us.dot.its.jpo.geojsonconverter.DateJsonMapper;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.Transient;
import org.springframework.data.mongodb.core.mapping.Field;

import lombok.EqualsAndHashCode;
import lombok.Generated;
import lombok.Getter;
import lombok.Setter;


@Getter
@Setter
@EqualsAndHashCode
@Generated
@JsonIgnoreProperties(value = { "_id", "_class" })
public abstract class Config<T> {

    private static final Logger logger = LoggerFactory.getLogger(Config.class);

    private String key;
    private String category;
    private T value;
    private String type;
    private UnitsEnum units;
    private String description;
    private UpdateType updateType;
    
    public Config(){
        
    }

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

package us.dot.its.jpo.conflictmonitor.monitor.models.config;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import org.springframework.data.annotation.Id;

import lombok.EqualsAndHashCode;

/** 
 * A default Config is a subtype of Config used for configuration variables that can only be changed at a global level.
 */
@EqualsAndHashCode(callSuper=true)
@JsonIgnoreProperties(ignoreUnknown = true)
public class DefaultConfig<T> extends Config<T>{

    @Id
    @Override
    public String getKey() {
        return super.getKey();
    }

    public DefaultConfig(){
        
    }

    public DefaultConfig(String key, String category, T value, String type, UnitsEnum units, String description){
        super(key, category, value, type, units, description);
    }

}



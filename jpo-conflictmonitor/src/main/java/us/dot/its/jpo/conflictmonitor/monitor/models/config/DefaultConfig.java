package us.dot.its.jpo.conflictmonitor.monitor.models.config;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper=true)
@Document("CmDefaultConfig")
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



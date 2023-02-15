package us.dot.its.jpo.conflictmonitor.monitor.models.config;

import org.springframework.data.mongodb.core.mapping.Document;



@Document("CmDefaultConfig")
public class DefaultConfig<T> extends Config<T>{

    public DefaultConfig(){
        
    }

    public DefaultConfig(String key, String category, T value){
        super(key, category, value);
    }

}

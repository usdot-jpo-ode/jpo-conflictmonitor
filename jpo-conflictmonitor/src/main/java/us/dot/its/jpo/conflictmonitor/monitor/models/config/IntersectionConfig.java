package us.dot.its.jpo.conflictmonitor.monitor.models.config;

import org.springframework.data.mongodb.core.mapping.Document;

import lombok.Getter;
import lombok.Setter;

@Document("CmIntersectionConfig")
public class IntersectionConfig<T> extends Config<T>{

    @Getter @Setter private int intersectionID;

    public IntersectionConfig(String key, String category, int intersectionID, T value){
        super(key, category, value);
        this.intersectionID = intersectionID;
    }

}

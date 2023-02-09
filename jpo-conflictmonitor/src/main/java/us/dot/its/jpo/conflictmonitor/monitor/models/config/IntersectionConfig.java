package us.dot.its.jpo.conflictmonitor.monitor.models.config;

import org.springframework.data.mongodb.core.mapping.Document;

import lombok.Getter;
import lombok.Setter;

@Document("CmIntersectionConfig")
public class IntersectionConfig<T> extends Config<T>{

    @Getter @Setter private int intersectionID;
    @Getter @Setter private String roadRegulatorID;
    @Getter @Setter private String rsuID;
    

    public IntersectionConfig(String key, String category, String roadRegulatorID, int intersectionID, String rsuID, T value){
        super(key, category, value);
        this.intersectionID = intersectionID;
        this.roadRegulatorID = roadRegulatorID;
        this.rsuID = rsuID;
    }

}

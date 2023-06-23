package us.dot.its.jpo.conflictmonitor.monitor.models.config;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

@EqualsAndHashCode(callSuper = true)
@Document("CmIntersectionConfig")
public class IntersectionConfig<T> extends Config<T>{

    @Getter @Setter private int intersectionID;
    @Getter @Setter private int roadRegulatorID;
    @Getter @Setter private String rsuID;



    public IntersectionConfig(){
        super();
    }
    

    public IntersectionConfig(String key, String category, int roadRegulatorID, int intersectionID, String rsuID, T value, String type, UnitsEnum units, String description){
        super(key, category, value, type, units, description);
        this.intersectionID = intersectionID;
        this.roadRegulatorID = roadRegulatorID;
        this.rsuID = rsuID;
    }

    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    @Id
    public RsuConfigKey getRsuKey() {
        return new RsuConfigKey(rsuID, getKey());
    }


}

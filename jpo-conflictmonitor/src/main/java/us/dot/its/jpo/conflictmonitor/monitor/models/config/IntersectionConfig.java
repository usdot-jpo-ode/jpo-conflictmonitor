package us.dot.its.jpo.conflictmonitor.monitor.models.config;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.Id;


@Setter
@Getter
@EqualsAndHashCode(callSuper = true)
@JsonIgnoreProperties(ignoreUnknown = true)
public class IntersectionConfig<T> extends Config<T>{

    private int roadRegulatorID;
    private int intersectionID;

    public IntersectionConfig(){
        super();
    }

    public IntersectionConfig(String key, String category, int roadRegulatorID, int intersectionID, T value, String type, UnitsEnum units, String description){
        super(key, category, value, type, units, description);
        this.intersectionID = intersectionID;
        this.roadRegulatorID = roadRegulatorID;
    }

    public IntersectionConfigKey intersectionKey() {
        return new IntersectionConfigKey(roadRegulatorID, intersectionID, getKey());
    }

    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    @Id
    public String getIntersectionKey() {
        return String.format("%s-%s-%s", roadRegulatorID, intersectionID, getKey());
    }

}

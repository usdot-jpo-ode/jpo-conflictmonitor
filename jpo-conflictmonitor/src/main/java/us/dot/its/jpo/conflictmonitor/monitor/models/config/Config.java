package us.dot.its.jpo.conflictmonitor.monitor.models.config;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import us.dot.its.jpo.geojsonconverter.DateJsonMapper;

import org.springframework.data.annotation.Id;

import lombok.EqualsAndHashCode;
import lombok.Generated;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@EqualsAndHashCode
@Generated
public abstract class Config<T> {

    @Id
    private String key;
    private String category;
    private T value;

    public Config(){
        
    }

    public Config(String key, String category, T value){
        this.key = key;
        this.category = category;
        this.value = value;
    }

    @Override
    public String toString() {
        ObjectMapper mapper = DateJsonMapper.getInstance();
        String testReturn = "";
        try {
            testReturn = (mapper.writeValueAsString(this));
        } catch (JsonProcessingException e) {
            System.out.println(e);
        }
        return testReturn;
    }
}

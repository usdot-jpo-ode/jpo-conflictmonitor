package us.dot.its.jpo.conflictmonitor.monitor.models.bsm;

import java.util.Objects;

import com.fasterxml.jackson.core.JsonProcessingException;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import us.dot.its.jpo.geojsonconverter.DateJsonMapper;
import us.dot.its.jpo.geojsonconverter.partitioner.RsuIdKey;

@EqualsAndHashCode
@Getter
@Setter
public class BsmIntersectionKey implements RsuIdKey {

    private static final Logger logger = LoggerFactory.getLogger(BsmIntersectionKey.class);

    private String rsuId;
    private String bsmId;

    public BsmIntersectionKey() {}

    public BsmIntersectionKey(String rsuId, String bsmId){
        this.rsuId = rsuId;
        this.bsmId = bsmId;
    }

    @Override
    public String getRsuId() {
        return this.rsuId;
    }

    @Override
    public String toString() {
        var mapper = DateJsonMapper.getInstance();
        String json = "";
        try {
            json = mapper.writeValueAsString(this);
        } catch (JsonProcessingException e) {
            logger.error("Error writing BsmIntersectionKey as JSON string", e);
        }
        return json;
    }
    


    
}

package us.dot.its.jpo.conflictmonitor.monitor.models.bsm;

import com.fasterxml.jackson.core.JsonProcessingException;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import us.dot.its.jpo.geojsonconverter.DateJsonMapper;
import us.dot.its.jpo.geojsonconverter.partitioner.RsuIdKey;

// TODO Remove, use geojsonconverter RsuLogKey instead
@Deprecated(forRemoval = true)
@EqualsAndHashCode
@Getter
@Setter
public class BsmRsuIdKey implements RsuIdKey {

    private static final Logger logger = LoggerFactory.getLogger(BsmRsuIdKey.class);

    private String rsuId;
    private String bsmId;

    public BsmRsuIdKey() {}

    public BsmRsuIdKey(String rsuId, String bsmId){
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

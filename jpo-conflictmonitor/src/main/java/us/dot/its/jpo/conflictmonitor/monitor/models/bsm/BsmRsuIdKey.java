package us.dot.its.jpo.conflictmonitor.monitor.models.bsm;

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
public class BsmRsuIdKey implements RsuIdKey {

    private static final Logger logger = LoggerFactory.getLogger(BsmRsuIdKey.class);

    /**
     * RSU ID to use for the key
     */
    private String rsuId;

    /**
     * BSM ID to use for the key
     */
    private String bsmId;

    /** 
     * Empty Constructor for Building BsmRsuIdKey objects
     */
    public BsmRsuIdKey() {}

    /** 
     * @param rsuId The Id of the RSU that forwarded the message back to the system. Typically the IP address of the RSU unit.
     * @param bsmId The Vehicle Id included in the BSM.
     */
    public BsmRsuIdKey(String rsuId, String bsmId){
        this.rsuId = rsuId;
        this.bsmId = bsmId;
    }

    /** 
     * @return A String containing the RSU ID from this key. Typically the IP address of the RSU unit.
     */
    @Override
    public String getRsuId() {
        return this.rsuId;
    }

    /** 
     * @return a JSON string representing the contents of this BSM aggregation object.
     */
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

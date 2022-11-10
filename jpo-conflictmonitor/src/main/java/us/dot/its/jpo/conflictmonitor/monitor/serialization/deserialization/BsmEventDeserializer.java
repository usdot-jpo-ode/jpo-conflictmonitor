package us.dot.its.jpo.conflictmonitor.monitor.serialization.deserialization;
import java.io.IOException;

import us.dot.its.jpo.conflictmonitor.monitor.models.bsm.BsmEvent;
import us.dot.its.jpo.ode.model.OdeBsmData;
import us.dot.its.jpo.ode.model.OdeBsmMetadata;
import us.dot.its.jpo.ode.model.OdeBsmPayload;
import us.dot.its.jpo.ode.util.JsonUtils;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import org.apache.kafka.common.serialization.Deserializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class BsmEventDeserializer implements Deserializer<BsmEvent> {
    private static Logger logger = LoggerFactory.getLogger(BsmEventDeserializer.class);

    protected final ObjectMapper mapper = new ObjectMapper();

    @Override
    public BsmEvent deserialize(String topic, byte[] data) {
        
        if (data == null) {
            return null;
        }
        try {
            
            JsonNode actualObj = mapper.readTree(data);
            

            JsonNode startingBsmNode = actualObj.get("startingBsm");
            JsonNode endingBsmNode = actualObj.get("endingBsm");

            OdeBsmData startingBsm = null;
            OdeBsmData endingBsm = null;



            // // Deserialize the metadata
            JsonNode metadataNode = startingBsmNode.get("metadata");
            if(metadataNode != null){
                String metadataString = metadataNode.toString();
                OdeBsmMetadata metadataObject = (OdeBsmMetadata) JsonUtils.fromJson(metadataString, OdeBsmMetadata.class);
    
                // // Deserialize the payload
                JsonNode payloadNode = startingBsmNode.get("payload");
                String payloadString = payloadNode.toString();
                OdeBsmPayload mapPayload = (OdeBsmPayload) JsonUtils.fromJson(payloadString, OdeBsmPayload.class);
    
                startingBsm = new OdeBsmData(metadataObject, mapPayload);
            }
            


            // // Deserialize the metadata
            metadataNode = endingBsmNode.get("metadata");
            if(metadataNode != null){
                String metadataString = metadataNode.toString();
                OdeBsmMetadata metadataObject = (OdeBsmMetadata) JsonUtils.fromJson(metadataString, OdeBsmMetadata.class);
    
                // // Deserialize the payload
                JsonNode payloadNode = endingBsmNode.get("payload");
                String payloadString = payloadNode.toString();
                OdeBsmPayload mapPayload = (OdeBsmPayload) JsonUtils.fromJson(payloadString, OdeBsmPayload.class);
    
                endingBsm = new OdeBsmData(metadataObject, mapPayload);
            }

            Long startingTimestamp = actualObj.get("startingBsmTimestamp").asLong();
            Long endingTimestamp = actualObj.get("endingBsmTimestamp").asLong();

            
            BsmEvent returnData = new BsmEvent(startingBsm, endingBsm);
            if(startingTimestamp != null){
                returnData.setStartingBsmTimestamp(startingTimestamp);
            }
            if(endingTimestamp != null){
                returnData.setEndingBsmTimestamp(endingTimestamp);
            }
            
            return returnData;
        } catch (IOException e) {
            String errMsg = String.format("Exception deserializing for topic %s: %s", topic, e.getMessage());
            logger.error(errMsg, e);
            throw new RuntimeException(errMsg, e);
        }
    }
}

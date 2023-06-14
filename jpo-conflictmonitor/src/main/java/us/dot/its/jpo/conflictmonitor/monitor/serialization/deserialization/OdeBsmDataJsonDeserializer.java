package us.dot.its.jpo.conflictmonitor.monitor.serialization.deserialization;

import java.io.IOException;

import us.dot.its.jpo.ode.model.OdeBsmData;
import us.dot.its.jpo.ode.model.OdeBsmMetadata;
import us.dot.its.jpo.ode.model.OdeBsmPayload;
import us.dot.its.jpo.ode.util.JsonUtils;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import org.apache.kafka.common.serialization.Deserializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * OdeSpatData JSON serializer for Kafka.  Converts a OdeSpatData POJO
 * to a JSON string encoded as a UTF-8 byte array.
 */

public class OdeBsmDataJsonDeserializer implements Deserializer<OdeBsmData> {
    private static Logger logger = LoggerFactory.getLogger(OdeBsmDataJsonDeserializer.class);

    protected final ObjectMapper mapper = new ObjectMapper();

    @Override
    public OdeBsmData deserialize(String topic, byte[] data) {
        if (data == null || data.length < 100) {
            return null;
        }
        try {
            JsonNode actualObj = mapper.readTree(data);

            // // // Deserialize the metadata
            JsonNode metadataNode = actualObj.get("metadata");
            String metadataString = metadataNode.toString();
            OdeBsmMetadata metadataObject = (OdeBsmMetadata) JsonUtils.fromJson(metadataString, OdeBsmMetadata.class);


            // // Deserialize the payload
            JsonNode payloadNode = actualObj.get("payload");
            String payloadString = payloadNode.toString();
            OdeBsmPayload mapPayload = (OdeBsmPayload) JsonUtils.fromJson(payloadString, OdeBsmPayload.class);
            OdeBsmData returnData = new OdeBsmData(metadataObject, mapPayload);
            return returnData;
        } catch (IOException e) {
            String errMsg = String.format("Exception deserializing for topic %s: %s", topic, e.getMessage());
            logger.error(errMsg, e);
            throw new RuntimeException(errMsg, e);
        }
    }
}

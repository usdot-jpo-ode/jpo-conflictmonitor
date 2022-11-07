package us.dot.its.jpo.conflictmonitor.monitor.serialization.deserialization;


import java.io.IOException;
import java.util.ArrayList;

import us.dot.its.jpo.conflictmonitor.monitor.models.bsm.BsmAggregator;
import us.dot.its.jpo.ode.model.OdeBsmData;
import us.dot.its.jpo.ode.model.OdeBsmMetadata;
import us.dot.its.jpo.ode.model.OdeBsmPayload;
import us.dot.its.jpo.ode.util.JsonUtils;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import org.apache.kafka.common.serialization.Deserializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class BsmAggregatorDeserializer implements Deserializer<BsmAggregator> {
    private static Logger logger = LoggerFactory.getLogger(OdeBsmDataJsonDeserializer.class);

    protected final ObjectMapper mapper = new ObjectMapper();

    @Override
    public BsmAggregator deserialize(String topic, byte[] data) {
        if (data == null) {
            return null;
        }
        try {
            JsonNode actualObj = mapper.readTree(data);
            

            BsmAggregator aggregator =  new BsmAggregator();
            //TreeSet<OdeBsmData> bsmList = new TreeSet();
            JsonNode bsmListNode = actualObj.get("bsms");

            for(JsonNode bsm: bsmListNode){
                JsonNode metadataNode = bsm.get("metadata");
                String metadataString = metadataNode.toString();
                OdeBsmMetadata metadataObject = (OdeBsmMetadata) JsonUtils.fromJson(metadataString, OdeBsmMetadata.class);


                JsonNode payloadNode = bsm.get("payload");
                String payloadString = payloadNode.toString();
                OdeBsmPayload mapPayload = (OdeBsmPayload) JsonUtils.fromJson(payloadString, OdeBsmPayload.class);

                OdeBsmData newBsm = new OdeBsmData(metadataObject, mapPayload);

                aggregator.addWithoutDeletion(newBsm);
            }

            //aggregator.setBsmList(bsmList);

            return aggregator;
        } catch (IOException e) {
            String errMsg = String.format("Exception deserializing for topic %s: %s", topic, e.getMessage());
            logger.error(errMsg, e);
            throw new RuntimeException(errMsg, e);
        }
    }
}

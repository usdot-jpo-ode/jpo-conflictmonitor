package us.dot.its.jpo.conflictmonitor.monitor.serialization.deserialization;

import us.dot.its.jpo.conflictmonitor.monitor.models.VehicleEvent;


import us.dot.its.jpo.ode.util.JsonUtils;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;

import org.apache.kafka.common.serialization.Deserializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class VehicleEventDeserializer implements Deserializer<VehicleEvent> {
    private static Logger logger = LoggerFactory.getLogger(VehicleEventDeserializer.class);

    protected final ObjectMapper mapper = new ObjectMapper();

    @Override
    public VehicleEvent deserialize(String topic, byte[] data) {
        
        if (data == null) {
            return null;
        }
        try {
            
            JsonNode actualObj = mapper.readTree(data);
            
            return null;
        } catch (IOException e) {
            String errMsg = String.format("Exception deserializing for topic %s: %s", topic, e.getMessage());
            logger.error(errMsg, e);
            throw new RuntimeException(errMsg, e);
        }
    }
}

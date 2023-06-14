package us.dot.its.jpo.conflictmonitor.monitor.serialization.deserialization;

import java.io.IOException;
import java.util.ArrayList;

import us.dot.its.jpo.conflictmonitor.monitor.models.spat.SpatTimeChangeDetail;
import us.dot.its.jpo.conflictmonitor.monitor.models.spat.SpatTimeChangeDetailAggregator;
import us.dot.its.jpo.ode.util.JsonUtils;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import org.apache.kafka.common.serialization.Deserializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;



public class SpatTimeChangeDetailAggregatorDeserializer implements Deserializer<SpatTimeChangeDetailAggregator> {
    private static Logger logger = LoggerFactory.getLogger(SpatTimeChangeDetailAggregatorDeserializer.class);

    protected final ObjectMapper mapper = new ObjectMapper();

    @Override
    public SpatTimeChangeDetailAggregator deserialize(String topic, byte[] data) {
        if (data == null) {
            return null;
        }
        try {
            JsonNode actualObj = mapper.readTree(data);
            JsonNode messageBufferSize = actualObj.get("messageBufferSize");

            SpatTimeChangeDetailAggregator aggregator =  new SpatTimeChangeDetailAggregator(messageBufferSize.asInt());



            ArrayList<SpatTimeChangeDetail> details = new ArrayList<>();
            for(JsonNode detail: actualObj.get("spatTimeChangeDetails")){
                details.add((SpatTimeChangeDetail)JsonUtils.fromJson(detail.toString(), SpatTimeChangeDetail.class));
            }
            aggregator.setSpatTimeChangeDetails(details);
            return aggregator;
        } catch (IOException e) {
            String errMsg = String.format("Exception deserializing for topic %s: %s", topic, e.getMessage());
            logger.error(errMsg, e);
            throw new RuntimeException(errMsg, e);
        }
    }
}
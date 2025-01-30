package us.dot.its.jpo.ode.messagesender.scriptrunner;


import org.apache.kafka.common.serialization.StringSerializer;
import org.apache.kafka.common.utils.Utils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class SendMessageJob implements Runnable {

    private final static Logger logger = LoggerFactory.getLogger(SendMessageJob.class);

   
    KafkaTemplate<String, String> kafkaTemplate;
    String messageType;
    long sendTime;
    String message;
    String rsuId;
    Integer intersectionId;
    String logId;
    String bsmId;

    final String ProcessedMapTopic = "topic.ProcessedMap";
    final String ProcessedSpatTopic = "topic.ProcessedSpat";
    final String ProcessedBsmTopic = "topic.ProcessedBsm";


    @Override
    public void run() {
        try {
            logger.info("{}: Sending {}", sendTime, messageType);
            switch (messageType) {
                case "MAP":
                    kafkaTemplate.send("topic.OdeMapJson", message);
                    break;
                case "SPAT":
                    kafkaTemplate.send("topic.OdeSpatJson", message);
                    break;
                case "BSM":
                    kafkaTemplate.send("topic.OdeBsmJson", message);
                    break;
                case "ProcessedMap":
                    kafkaTemplate.send(ProcessedMapTopic, partitionForIntersection(intersectionId, ProcessedMapTopic), getKey(rsuId, intersectionId), message);
                    break;
                case "ProcessedSpat":
                    kafkaTemplate.send(ProcessedSpatTopic, partitionForIntersection(intersectionId, ProcessedSpatTopic), getKey(rsuId, intersectionId), message);
                    break;
                case "ProcessedBsm":
                    kafkaTemplate.send(ProcessedBsmTopic, partitionForRsuId(rsuId, ProcessedBsmTopic), getBsmKey(rsuId, logId, bsmId), message);
                    break;
            }

        } catch (Exception e) {
            logger.error("Exception sending to topic", e);
        }
        
    }



    private int partitionForIntersection(Integer intersectionId, String topic) {
        int numPartitions = kafkaTemplate.partitionsFor(topic).size();
        if (intersectionId != null) {
            return intersectionId.intValue() % numPartitions;
        } else {
            return 0;
        }
    }

    private int partitionForRsuId(String rsuId, String topic) {
        try (var stringSer = new StringSerializer()) {
            byte[] partitionBytes = stringSer.serialize(rsuId, topic);
            int numPartitions = kafkaTemplate.partitionsFor(topic).size();
            return Utils.toPositive(Utils.murmur2(partitionBytes)) % numPartitions;
        }
    }

    private String getKey(String rsuId, Integer intersectionId) {
        return String.format("{\"rsuId\": \"%s\", \"intersectionId\": %s, \"region\": -1}", rsuId, intersectionId);
    }

    private String getBsmKey(String rsuId, String logId, String bsmId) {
        return String.format("""
                {"rsuId": "%s", "logId": "%s", "bsmId": "%s"}""", rsuId, logId != null ? logId : "", bsmId);
    }

    
    
}

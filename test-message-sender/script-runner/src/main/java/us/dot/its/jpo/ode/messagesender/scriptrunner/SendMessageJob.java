package us.dot.its.jpo.ode.messagesender.scriptrunner;


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

    final String ProcessedMapTopic = "topic.ProcessedMap";
    final String ProcessedSpatTopic = "topic.ProcessedSpat";


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

    private String getKey(String rsuId, Integer intersectionId) {
        return String.format("{\"rsuId\": \"%s\", \"intersectionId\": %s, \"region\": -1}", rsuId, intersectionId);
    }

    

    
    
}

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
            }

        } catch (Exception e) {
            logger.error("Exception sending to topic", e);
        }
        
    }

    

    
    
}

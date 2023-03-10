package us.dot.its.jpo.ode.messagesender.scriptrunner.hex;

import java.io.File;
import java.nio.charset.StandardCharsets;
import java.io.IOException;

import org.apache.commons.io.FileUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

/**
 * Listens to Kafka ODE JSON topics and constructs a script.
 */
@Component
public class KafkaListeners {

    private static final Logger logger = LoggerFactory.getLogger(KafkaListeners.class);

    final KafkaTemplate<String, String> kafkaTemplate;
    File outputFile;
    long startTime;

    @Autowired
    public KafkaListeners(KafkaTemplate<String, String> kafkaTemplate) {
        this.kafkaTemplate = kafkaTemplate;
    }


    public void startSavingToFile(File outputFile) {
        this.outputFile = outputFile;
        startTime = System.currentTimeMillis();
    }


    @KafkaListener(topics = "topic.OdeBsmJson", groupId = "hexLogConverter-bsm")
    synchronized void listenBsm(String message) {
        listen(DSRCmsgID.BSM, message);
    }

    @KafkaListener(topics = "topic.OdeSpatJson", groupId = "hexLogConverter-spat")
    synchronized void listenSpat(String message) {
        listen(DSRCmsgID.SPAT, message);
    }

    @KafkaListener(topics = "topic.OdeMapJson", groupId = "hexLogConverter-map")
    synchronized void listenMap(String message) {
        listen(DSRCmsgID.MAP, message);
    }

    void listen(DSRCmsgID msgId, String message)  {
        final long now = System.currentTimeMillis();
        final long offsetTime = now - startTime;
        var formattedMessage = String.format("%s,%s,%s%n", msgId, offsetTime, message);
        
        logger.info("Received {} message", msgId);
        
        if (outputFile != null) {
            if (!outputFile.exists()) {
                try {
                    outputFile.createNewFile();
                } catch (IOException e) {
                    throw new RuntimeException("Error creating file", e);
                }
            }
            logger.info("Writing {} message to file", msgId);
            try {
                FileUtils.writeStringToFile(outputFile, formattedMessage, StandardCharsets.UTF_8, true);
            } catch (IOException e) {
                logger.error("Error writing to file", e);
            }
        }
    }

}

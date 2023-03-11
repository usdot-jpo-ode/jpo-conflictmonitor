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

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ObjectNode;

import us.dot.its.jpo.ode.messagesender.scriptrunner.DateJsonMapper;

/**
 * Listens to Kafka ODE JSON topics and constructs a script.
 */
@Component
public class KafkaListeners {

    private static final Logger logger = LoggerFactory.getLogger(KafkaListeners.class);

    final KafkaTemplate<String, String> kafkaTemplate;
    File outputFile;
    long startTime;
    boolean placeholders;
    File mapFile;
    File spatFile;
    File bsmFile;

    @Autowired
    public KafkaListeners(KafkaTemplate<String, String> kafkaTemplate) {
        this.kafkaTemplate = kafkaTemplate;
    }


    public void startSavingToFile(File outputFile, long startTime, boolean placeholders,
            File mapFile, File spatFile, File bsmFile) {
        this.outputFile = outputFile;
        this.startTime = startTime;
        this.placeholders = placeholders;
        this.mapFile = mapFile;
        this.spatFile = spatFile;
        this.bsmFile = bsmFile;
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
        logger.info("{}: Received {} message", offsetTime, msgId);   
         
        if (outputFile != null) {
            if (offsetTime < 0) {
                logger.info("Not saving old message with negative offset time");
                return;
            }
            var templatedMessage = placeholders ? substitutePlaceholders(msgId, message) : message;
            var formattedMessage = String.format("%s,%s,%s%n", msgId, offsetTime, templatedMessage);  
            if (!outputFile.exists()) {
                try {
                    outputFile.createNewFile();
                } catch (IOException e) {
                    throw new RuntimeException("Error creating file", e);
                }
            }
            logger.info("{}: Writing {} message to file {}", offsetTime, msgId, outputFile.getName());
            try {
                FileUtils.writeStringToFile(outputFile, formattedMessage, StandardCharsets.UTF_8, true);
            } catch (IOException e) {
                logger.error("Error writing to file", e);
            }
        }
        writeToMessageFile(msgId, message, offsetTime);
    }

    void writeToMessageFile(DSRCmsgID msgId, String message, long offsetTime) {
        File msgFile = null;
        if (msgId == DSRCmsgID.BSM) {
            msgFile = bsmFile;
        } else if (msgId == DSRCmsgID.SPAT) {
            msgFile = spatFile;
        } else if (msgId == DSRCmsgID.MAP) {
            msgFile = mapFile;
        }
        if (msgFile == null) return;

        if (msgFile != null) {
            if (!msgFile.exists()) {
                try {
                    msgFile.createNewFile();
                } catch (IOException e) {
                    throw new RuntimeException("Error creating file", e);
                }
            }
            logger.info("{}: Writing {} message to file {}", offsetTime, msgId, msgFile.getName());
            try {
                FileUtils.writeStringToFile(msgFile, message, StandardCharsets.UTF_8, true);
            } catch (IOException e) {
                logger.error("Error writing to file", e);
            }
        }
    }

    final String ISO_DATE_TIME = "@ISO_DATE_TIME@";
    final String MINUTE_OF_YEAR = "@MINUTE_OF_YEAR@";
    final String MILLI_OF_MINUTE = "@MILLI_OF_MINUTE@";
    final String TEMP_ID = "@TEMP_ID@";

    String substitutePlaceholders(DSRCmsgID msgId, String message) {
        var mapper = DateJsonMapper.getInstance();
        try {
            JsonNode node = mapper.readTree(message);
            JsonNode metadata = node.at("/metadata");
            ((ObjectNode)metadata).put("odeReceivedAt", ISO_DATE_TIME);
            if (DSRCmsgID.SPAT.equals(msgId)) {
                JsonNode data = node.at("/payload/data");
                ((ObjectNode)data).put("timeStamp", MINUTE_OF_YEAR);
                JsonNode intersectionList = node.at("/payload/data/intersectionStateList/intersectionStatelist");
                if (intersectionList.isArray()) {
                    for (JsonNode intersection : intersectionList) {
                        ObjectNode intersectionObj = (ObjectNode)intersection;
                        intersectionObj.put("moy", MINUTE_OF_YEAR);
                        intersectionObj.put("timeStamp", MILLI_OF_MINUTE);
                    }
                }
            } else if (DSRCmsgID.BSM.equals(msgId)) {
                JsonNode coreData = node.at("/payload/data/coreData");
                ObjectNode coreDataObj = (ObjectNode)coreData;
                coreDataObj.put("secMark", MILLI_OF_MINUTE);
                coreDataObj.put("id", TEMP_ID);
            }
            return mapper.writeValueAsString(node);
        } catch (JsonProcessingException e) {
            return message;
        }
    }

}

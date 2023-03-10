package us.dot.its.jpo.ode.messagesender;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.time.Duration;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Formatter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.BiConsumer;
import java.util.function.Consumer;

import org.apache.commons.codec.DecoderException;
import org.apache.commons.codec.binary.Hex;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.messaging.handler.annotation.Header;
import static org.springframework.kafka.support.KafkaHeaders.*;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Component;


@Component
public class HexLogConverter {

    private static final Logger logger = LoggerFactory.getLogger(HexLogConverter.class);

    final KafkaTemplate<String, String> kafkaTemplate;

    @Autowired
    public HexLogConverter(KafkaTemplate<String, String> kafkaTemplate) {
        this.kafkaTemplate = kafkaTemplate;
        resetMessageCount();
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

    void listen(DSRCmsgID msgId, String message) {
        logger.info("Received {} message", msgId);
        latestMessage.put(msgId, message);
        long count = messageCount.get(msgId);
        messageCount.put(msgId, count + 1);
    }

    final Map<DSRCmsgID, String> latestMessage = new ConcurrentHashMap<>();
    final Map<DSRCmsgID, Long> messageCount = new ConcurrentHashMap<>();

    void resetMessageCount() {
        for (DSRCmsgID msgId : DSRCmsgID.values()) {
            messageCount.put(msgId, 0L);
        }
    }
    
    public String convertHexLogToScript(HexLog hexLog) {
        resetMessageCount();
        String output = null;
        final long startTime = hexLog.get(0).getTimeStamp();
        try (Formatter sb = new Formatter()) {
            for (HexLogItem item : hexLog) {
                String result = sendToODE(startTime, item);
                sb.format("%s%n", result);
            }
            output = sb.toString();
        }
        return output;
    }
   

    public String sendToODE(final long startTime, HexLogItem item) {
        final String hex = item.getHexMessage();
        final String hexMsgId = hex.substring(0, 4);
        final DSRCmsgID msgId = DSRCmsgID.fromHex(hexMsgId);
        if (msgId == null) {
            logger.error("Unknown hex message id: {}", hexMsgId);
        }
        final int udpPort = msgId.getUdpPort();
        if (udpPort == 0) {
            logger.error("ODE does not accept {} messages", msgId);
        }
        
        final long startCount = messageCount.get(msgId).longValue();

        // Send hex message to UDP port
        try (DatagramSocket socket = new DatagramSocket()) {
            String dockerHostIp = System.getenv("DOCKER_HOST_IP");
            InetAddress address = InetAddress.getByName(dockerHostIp);
            byte[] buf = Hex.decodeHex(hex);
            DatagramPacket packet = new DatagramPacket(buf, buf.length, address, udpPort);
            socket.send(packet);
            logger.info("Sent {} to udp {}:{}", msgId, dockerHostIp, udpPort);
        } catch (IOException | DecoderException e) {
            logger.error("Error sending hex message to UDP port", e);
        }

        // Wait until a message is received
        while (messageCount.get(msgId).longValue() == startCount) {
            try {
                Thread.sleep(1);
            } catch (InterruptedException e) {
                logger.error("Error sleeping", e);
            }
        }

        return String.format("%s,%s,%s", msgId, item.getTimeStamp() - startTime + 100L, latestMessage.get(msgId));

    }
    
}

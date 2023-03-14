package us.dot.its.jpo.ode.messagesender.scriptrunner.hex;

import java.io.File;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.time.Instant;

import org.apache.commons.codec.DecoderException;
import org.apache.commons.codec.binary.Hex;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.databind.MappingIterator;

import us.dot.its.jpo.ode.messagesender.scriptrunner.DateJsonMapper;


/**
 * Sends a hex log to the ODE and recieves and saves the converted ODE JSON messages as script.
 * 
 * <p>Hex log format is line-delimited JSON with format: 
 * <pre>{ "timeStamp": milliseconds, "dir": "S" or "R", "hexMessage": "00142846F..."}</pre>
 * 
 */
@Component
public class HexLogRunner {

    private static final Logger logger = LoggerFactory.getLogger(HexLogRunner.class);

    final KafkaListeners listeners;
    final ThreadPoolTaskScheduler scheduler;

    @Autowired
    public HexLogRunner(KafkaListeners listeners, ThreadPoolTaskScheduler scheduler) {
        this.listeners = listeners;
        this.scheduler = scheduler;
    }


    

    

    /**
     * @param inputFile - File containing a json hex log
     * @throws IOException
     */
    public void convertHexLogToScript(String dockerHostIp, File inputFile, File outputFile, int delay,
        boolean placeholders, File mapFile, File spatFile, File bsmFile) throws IOException {
        logger.info("Running hex log, inputFile {}, outputFile: {}", inputFile, outputFile);
        
        var mapper = DateJsonMapper.getInstance();

        // Get the earliest timestamp in the hexScript
        long earliestTimestamp = Long.MAX_VALUE;
        try (MappingIterator<HexLogItem> iterator = mapper.readerFor(HexLogItem.class).readValues(inputFile)) {
            while (iterator.hasNext()) {
                HexLogItem hexItem = iterator.next();
                long timeStamp = hexItem.getTimeStamp();
                if (timeStamp < earliestTimestamp) {
                    earliestTimestamp = timeStamp;
                }
            }
        }
        logger.info("Earliest timestamp: {}", earliestTimestamp);
        final long startTime = System.currentTimeMillis() + delay;
        logger.info("Start time: {}", startTime);

        listeners.startSavingToFile(outputFile, startTime, placeholders, mapFile, spatFile, bsmFile);

        // Schedule sending hex messages to ODE
        try (MappingIterator<HexLogItem> iterator = mapper.readerFor(HexLogItem.class).readValues(inputFile)) {
            while (iterator.hasNext()) {
                HexLogItem hexItem = iterator.next();

                // Classify the Message Frame type
                final String hex = hexItem.getHexMessage();
                final String hexMsgId = hex.substring(0, 4);
                final DSRCmsgID msgId = DSRCmsgID.fromHex(hexMsgId);
                if (msgId == null) {
                    logger.error("Unknown hex message id: {}", hexMsgId);
                    continue;
                }
                final int udpPort = msgId.getUdpPort();
                if (udpPort == 0) {
                    logger.error("ODE does not accept {} messages", msgId);
                    continue;
                }

                long timeOffset = hexItem.getTimeStamp() - earliestTimestamp;
                scheduleSendHexItem(hex, msgId, startTime, timeOffset, dockerHostIp);
            }
        } 
    }


    public void scheduleSendHexItem(String hexMessage, DSRCmsgID msgId, final long startTime, final long timeOffset, String dockerHostIp) {
        final long sendTime = startTime + timeOffset;
        final Instant sendInstant = Instant.ofEpochMilli(sendTime);
        var job = new SendHexJob();
        job.setSendTime(sendTime);
        job.setStartTime(startTime);
        job.setMsgId(msgId);
        job.setHexMessage(hexMessage);
        job.setDockerHostIp(dockerHostIp);
        scheduler.schedule(job, sendInstant);
        logger.info("Scheduled {} job at {}", msgId, sendTime - startTime);
    }




   
}

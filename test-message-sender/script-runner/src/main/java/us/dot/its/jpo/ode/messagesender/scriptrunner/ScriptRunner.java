package us.dot.its.jpo.ode.messagesender.scriptrunner;

import java.io.File;
import java.io.FileNotFoundException;
import java.time.Duration;
import java.time.Instant;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Random;
import java.util.Scanner;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;
import org.springframework.stereotype.Component;

import static us.dot.its.jpo.ode.messagesender.scriptrunner.ScriptTemplate.fillTemplate;

@Component
public class ScriptRunner {

    private final static Logger logger = LoggerFactory.getLogger(ScriptRunner.class);

    private final static Pattern linePattern = Pattern.compile("^(?<messageType>BSM|SPAT|MAP|ProcessedMap|ProcessedSpat)(;(?<rsuId>[A-Za-z0-9.]+);(?<intersectionId>\\d+))?,(?<time>\\d+),(?<message>.+)$");
    
    @Autowired
    ThreadPoolTaskScheduler scheduler;

    @Autowired
    KafkaTemplate<String, String> kafkaTemplate;

    private static final Random random = new Random();

    /**
     * Schedule each item in a script to be run
     * @param scriptFile - File containing the script
     */
    public void scheduleScript(File scriptFile) throws FileNotFoundException {
        try (var scanner = new Scanner(scriptFile)) {
            scanScript(scanner);
        }
    }

    /**
     * Schedule each item in a script to be run
     * @param script - String containing the entire script
     */
    public void scheduleScript(String script) {
        try (var scanner = new Scanner(script)) {
            scanScript(scanner);
        } 
    }

    private void scanScript(Scanner scanner) {
        // Random Temporary ID, 32-bits, 8 hex digit
        int randomInt = random.nextInt();
        final String tempId = String.format("%08X", randomInt);
        final long startTime = Instant.now().toEpochMilli();
        while (scanner.hasNextLine()) {
            String line = scanner.nextLine();

            // Skip blank lines or comments
            if (StringUtils.isBlank(line) || line.startsWith("#")) continue;
            
            Matcher m = linePattern.matcher(line);
            if (!m.find()) {
                logger.warn("Skipping invalid line: \n{}", line);
                continue;
            } 
            try {
                String messageType = m.group("messageType");
                long timeOffset = Long.parseLong(m.group("time"));
                String message = m.group("message");
                String rsuId = m.group("rsuId");
                String intersectionIdStr = m.group("intersectionId");
                Integer intersectionId = StringUtils.isNotEmpty(intersectionIdStr) ? Integer.parseInt(intersectionIdStr) : null;
                scheduleMessage(startTime, messageType, timeOffset, message, tempId, rsuId, intersectionId);
            } catch (Exception e) {
                logger.error(String.format("Exception in line '%s'", line), e);
            }
        }
    }

    private void scheduleMessage(final long startTime, final String messageType, 
        final long timeOffset, final String message, final String tempId, final String rsuId, final Integer intersectionId) {
        final long sendTime = startTime + timeOffset;
        final Instant sendInstant = Instant.ofEpochMilli(sendTime);
        var job = new SendMessageJob();
        job.setKafkaTemplate(kafkaTemplate);
        job.setMessageType(messageType);
        job.setSendTime(sendTime);
        job.setMessage(fillTemplate(sendInstant, message, tempId));
        job.setRsuId(rsuId);
        job.setIntersectionId(intersectionId);
        scheduler.schedule(job, sendInstant);
        logger.info("Scheduled {} job at {}", messageType, sendTime);
    }



}



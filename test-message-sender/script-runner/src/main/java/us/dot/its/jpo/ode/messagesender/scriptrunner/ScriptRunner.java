package us.dot.its.jpo.ode.messagesender.scriptrunner;

import java.io.File;
import java.io.FileNotFoundException;
import java.time.Duration;
import java.time.Instant;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
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

@Component
public class ScriptRunner {

    private final static Logger logger = LoggerFactory.getLogger(ScriptRunner.class);

    private final static Pattern linePattern = Pattern.compile("^(?<messageType>BSM|SPAT|MAP),(?<time>\\d+),(?<message>.+)$");
    
    @Autowired
    ThreadPoolTaskScheduler scheduler;

    @Autowired
    KafkaTemplate<String, String> kafkaTemplate;

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
        long startTime = Instant.now().toEpochMilli();
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
                scheduleMessage(startTime, messageType, timeOffset, message);
            } catch (Exception e) {
                logger.error(String.format("Exception in line '%s'", line), e);
            }
        }
    }

    private void scheduleMessage(long startTime, String messageType, long timeOffset, String message) 
    {
        final long sendTime = startTime + timeOffset;
        final Instant sendInstant = Instant.ofEpochMilli(sendTime);
        var job = new SendMessageJob();
        job.setKafkaTemplate(kafkaTemplate);
        job.setMessageType(messageType);
        job.setSendTime(sendTime);
        job.setMessage(fillTimestamps(sendInstant, message));
        scheduler.schedule(job, sendInstant);
        logger.info("Scheduled {} job at {}", messageType, sendTime);
    }

    public static final String ISO_DATE_TIME = "@ISO_DATE_TIME@";
    public static final String MINUTE_OF_YEAR = "\"@MINUTE_OF_YEAR@\"";
    public static final String MILLI_OF_MINUTE = "\"@MILLI_OF_MINUTE@\"";

    private static String fillTimestamps(Instant sendInstant, String message) {
        ZonedDateTime zdt = sendInstant.atZone(ZoneOffset.UTC);
        String isoDateTime = zdt.format(DateTimeFormatter.ISO_DATE_TIME);

        ZonedDateTime zdtYear = ZonedDateTime.of(zdt.getYear(), 1, 1, 0, 0, 0, 0, ZoneOffset.UTC);
        Duration moyDuration = Duration.between(zdtYear, zdt);
        long minuteOfYear = moyDuration.toMinutes();

        ZonedDateTime zdtMinute = ZonedDateTime.of(zdt.getYear(), zdt.getMonthValue(), 
            zdt.getDayOfMonth(), zdt.getHour(), zdt.getMinute(), 0, 0, ZoneOffset.UTC);
        Duration minDuration = Duration.between(zdtMinute, zdt);
        long milliOfMinute = minDuration.toMillis();
        
        return message
            .replace(ISO_DATE_TIME, isoDateTime)
            .replace(MINUTE_OF_YEAR, Long.toString(minuteOfYear))
            .replace(MILLI_OF_MINUTE, Long.toString(milliOfMinute));

    }

    
}

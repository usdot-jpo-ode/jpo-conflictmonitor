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

    public static final String ISO_DATE_TIME = "@ISO_DATE_TIME@";
    public static final String MINUTE_OF_YEAR = "\"@MINUTE_OF_YEAR@\"";
    public static final String MILLI_OF_MINUTE = "\"@MILLI_OF_MINUTE@\"";
    public static final String EPOCH_SECONDS = "\"@EPOCH_SECONDS@\"";
    public static final Pattern OFFSET_SECONDS = Pattern.compile("\"@OFFSET_SECONDS_(?<offset>-?[0-9.]+)@\"");
    public static final String TEMP_ID = "@TEMP_ID@";
    


    private static String fillTemplate(Instant sendInstant, String message, String tempId) {
        ZonedDateTime zdt = sendInstant.atZone(ZoneOffset.UTC);
        String isoDateTime = zdt.format(DateTimeFormatter.ISO_DATE_TIME);

        ZonedDateTime zdtYear = ZonedDateTime.of(zdt.getYear(), 1, 1, 0, 0, 0, 0, ZoneOffset.UTC);
        Duration moyDuration = Duration.between(zdtYear, zdt);
        long minuteOfYear = moyDuration.toMinutes();

        ZonedDateTime zdtMinute = ZonedDateTime.of(zdt.getYear(), zdt.getMonthValue(), 
            zdt.getDayOfMonth(), zdt.getHour(), zdt.getMinute(), 0, 0, ZoneOffset.UTC);
        Duration minDuration = Duration.between(zdtMinute, zdt);
        long milliOfMinute = minDuration.toMillis();
       
        double epochSecond = sendInstant.toEpochMilli() / 1000.0d;

        // Fill in offset decimal seconds in timing
        var matcher = OFFSET_SECONDS.matcher(message);
        String replaced = matcher.replaceAll((matchResult) -> {
            String offsetStr = matcher.group("offset");
            double offset = Double.parseDouble(offsetStr);

            // Offset seconds and round to 3 decimals
            double seconds = epochSecond + offset;
            return String.format("%.3f", seconds);
        });



        return replaced
            .replace(ISO_DATE_TIME, isoDateTime)
            .replace(MINUTE_OF_YEAR, Long.toString(minuteOfYear))
            .replace(MILLI_OF_MINUTE, Long.toString(milliOfMinute))
            .replace(EPOCH_SECONDS, Double.toString(epochSecond))
            .replace(TEMP_ID, tempId);



    }

    
}

package us.dot.its.jpo.ode.messagesender.scriptrunner;

import java.text.DecimalFormat;
import java.time.Duration;
import java.time.Instant;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Methods for filling in templated scripts
 */
public class ScriptTemplate {

    public static final String ISO_DATE_TIME = "@ISO_DATE_TIME@";
    public static final Pattern ISO_DATE_TIME_OFFSET_MILLIS = Pattern.compile("@ISO_DATE_TIME_OFFSET_MILLIS_(?<offset>-?[0-9]+)@");
    public static final String MINUTE_OF_YEAR = "\"@MINUTE_OF_YEAR@\"";
    public static final String MILLI_OF_MINUTE = "\"@MILLI_OF_MINUTE@\"";
    public static final String EPOCH_SECONDS = "\"@EPOCH_SECONDS@\"";
    public static final Pattern OFFSET_SECONDS = Pattern.compile("@OFFSET_SECONDS_(?<offset>-?[0-9.]+)@");
    public static final String TEMP_ID = "@TEMP_ID@";



    public static String fillTemplate(final Instant sendInstant, final String message, final String tempId) {
        final ZonedDateTime zdt = sendInstant.atZone(ZoneOffset.UTC);
        final String isoDateTime = zdt.format(DateTimeFormatter.ISO_DATE_TIME);

        final ZonedDateTime zdtYear = ZonedDateTime.of(zdt.getYear(), 1, 1, 0, 0, 0, 0, ZoneOffset.UTC);
        final Duration moyDuration = Duration.between(zdtYear, zdt);
        final long minuteOfYear = moyDuration.toMinutes();

        final ZonedDateTime zdtMinute = ZonedDateTime.of(zdt.getYear(), zdt.getMonthValue(),
                zdt.getDayOfMonth(), zdt.getHour(), zdt.getMinute(), 0, 0, ZoneOffset.UTC);
        final Duration minDuration = Duration.between(zdtMinute, zdt);
        final long milliOfMinute = minDuration.toMillis();

        final double epochSecond = sendInstant.toEpochMilli() / 1000.0d;

        // Fill in offset seconds in timing
        // Fill in ISO Date/time offset by milliseconds
        String offsetsReplaced =
                replaceIsoOffsetMillis(sendInstant,
                        replaceOffsetSeconds(epochSecond, message));

        return offsetsReplaced
                .replace(ISO_DATE_TIME, isoDateTime)
                .replace(MINUTE_OF_YEAR, Long.toString(minuteOfYear))
                .replace(MILLI_OF_MINUTE, Long.toString(milliOfMinute))
                .replace(EPOCH_SECONDS, String.format("%.3f", epochSecond))
                .replace(TEMP_ID, tempId);


    }

    public static String replaceOffsetSeconds(final double epochSecond, final String message) {
        final Matcher matcher = OFFSET_SECONDS.matcher(message);
        return matcher.replaceAll((matchResult) -> {
            String offsetStr = matcher.group("offset");
            double offsetSeconds = Double.parseDouble(offsetStr);
            double timingSeconds = epochSecond + offsetSeconds;
            long timingMillis = Math.round(1000 * timingSeconds);
            Instant timingInstant = Instant.ofEpochMilli(timingMillis);
            ZonedDateTime zdtTiming = timingInstant.atZone(ZoneOffset.UTC);
            return zdtTiming.format(DateTimeFormatter.ISO_DATE_TIME);
        });
    }

    public static String replaceIsoOffsetMillis(final Instant sendInstant, final String message) {
        final Matcher matcher = ISO_DATE_TIME_OFFSET_MILLIS.matcher(message);
        return matcher.replaceAll((matchResult) -> {
            final String offsetStr = matcher.group("offset");
            final long offsetMillis = Long.parseLong(offsetStr);
            final long sendInstantMillis = sendInstant.toEpochMilli();
            final long isoOffsetMillis = sendInstantMillis + offsetMillis;
            final Instant offsetInstant = Instant.ofEpochMilli(isoOffsetMillis);
            final ZonedDateTime zdtOffset = offsetInstant.atZone(ZoneOffset.UTC);
            return zdtOffset.format(DateTimeFormatter.ISO_DATE_TIME);
        });
    }
}

package us.dot.its.jpo.ode.messagesender.scriptrunner;

import org.junit.Test;

import java.time.Instant;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;


public class ScriptTemplateTest {

    @Test
    public void testFillTemplate() {
        final Instant sendInstant = Instant.ofEpochMilli(1721876849123L);
        final String tempId = "abababab";
        final String message = """
                "isoDateTime": "@ISO_DATE_TIME@"
                "minuteOfYear": "@MINUTE_OF_YEAR@"
                "milliOfMinute": "@MILLI_OF_MINUTE@"
                "epochSeconds": "@EPOCH_SECONDS@"
                "tempId": "@TEMP_ID@"
                """;
        final String expectStr = """
                "isoDateTime": "2024-07-25T03:07:29.123Z"
                "minuteOfYear": 296827
                "milliOfMinute": 29123
                "epochSeconds": 1721876849.123
                "tempId": "abababab"
                """;
        String actualStr = ScriptTemplate.fillTemplate(sendInstant, message, tempId);
        assertThat(actualStr, equalTo(expectStr));
    }

    @Test
    public void testReplaceIsoOffsetMillis() {
        final Instant sendInstant = Instant.ofEpochMilli(1721876849000L);
        final String message = """
             "timeStamp": "@ISO_DATE_TIME_OFFSET_MILLIS_1575@"
             """;
        final String expectStr = """
             "timeStamp": "2024-07-25T03:07:30.575Z"
             """;

        String actualStr = ScriptTemplate.replaceIsoOffsetMillis(sendInstant, message);
        assertThat(actualStr, equalTo(expectStr));
    }

    @Test
    public void testReplaceIsoOffsetMillis_Negative() {
        final Instant sendInstant = Instant.ofEpochMilli(1721876849000L);
        final String message = """
             "timeStamp": "@ISO_DATE_TIME_OFFSET_MILLIS_-2575@"
             """;
        final String expectStr = """
             "timeStamp": "2024-07-25T03:07:26.425Z"
             """;

        String actualStr = ScriptTemplate.replaceIsoOffsetMillis(sendInstant, message);
        assertThat(actualStr, equalTo(expectStr));
    }

    @Test
    public void testReplaceOffsetSeconds() {
        final double epochSecond = 1721876849;
        final String message = """
                "maxEndTime": "@OFFSET_SECONDS_21.555@"
                """;
        final String expectStr = """
                "maxEndTime": "2024-07-25T03:07:50.555Z"
                """;
        String actualStr = ScriptTemplate.replaceOffsetSeconds(epochSecond, message);
        assertThat(actualStr, equalTo(expectStr));
    }

    @Test
    public void testReplaceOffsetSeconds_Negative() {
        final double epochSecond = 1721876849;
        final String message = """
                "maxEndTime": "@OFFSET_SECONDS_-21.555@"
                """;
        final String expectStr = """
                "maxEndTime": "2024-07-25T03:07:07.445Z"
                """;
        String actualStr = ScriptTemplate.replaceOffsetSeconds(epochSecond, message);
        assertThat(actualStr, equalTo(expectStr));
    }

}

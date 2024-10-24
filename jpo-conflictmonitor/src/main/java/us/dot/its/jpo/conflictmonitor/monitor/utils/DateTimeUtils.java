package us.dot.its.jpo.conflictmonitor.monitor.utils;

import java.time.ZonedDateTime;

public class DateTimeUtils {

    /**
     * Null safe ZonedDateTime -> millis
     * @param zdt
     * @return
     */
    public static long toMillis(ZonedDateTime zdt) {
        if (zdt == null) return -1;
        return zdt.toInstant().toEpochMilli();
    }
}

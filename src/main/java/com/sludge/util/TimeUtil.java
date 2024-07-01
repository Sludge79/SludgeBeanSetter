package com.sludge.util;

import java.time.Instant;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Locale;

/**
 * @author Sludge
 * @since 2024/7/1 17:09
 */
public class TimeUtil {
    private static final ZoneId ZONE_ID = ZoneId.of("Asia/Shanghai");
    private static final String PATTEN_FULL = "yyyy-MM-dd HH:mm:ss";
    private static final DateTimeFormatter fullFormatter = DateTimeFormatter.ofPattern(PATTEN_FULL).withLocale(Locale.CHINA).withZone(ZONE_ID);

    public static String formatDateFull(Instant date) {
        try {
            return fullFormatter.format(date);
        }catch (Exception ignored){
            return null;
        }
    }

}

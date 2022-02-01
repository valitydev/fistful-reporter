package com.rbkmoney.fistful.reporter.util;

import dev.vality.geck.common.util.TypeUtil;

import java.time.Instant;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;

public class TimeUtils {

    private static final DateTimeFormatter DEFAULT_DATE_TIME_FORMAT =
            DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm:ss");
    private static final DateTimeFormatter DEFAULT_DATE_FORMAT = DateTimeFormatter.ofPattern("dd.MM.yyyy");

    public static String toLocalizedDate(String dateTimeUtc, ZoneId zoneId) {
        return toFormattedDateTime(TypeUtil.stringToInstant(dateTimeUtc), zoneId, DEFAULT_DATE_FORMAT);
    }

    public static String toLocalizedDate(Instant instant, ZoneId zoneId) {
        return toFormattedDateTime(instant, zoneId, DEFAULT_DATE_FORMAT);
    }

    public static String toLocalizedDateTime(String dateTimeUtc, ZoneId zoneId) {
        return toLocalizedDateTime(TypeUtil.stringToInstant(dateTimeUtc), zoneId);
    }

    public static String toLocalizedDateTime(Instant instant, ZoneId zoneId) {
        return toFormattedDateTime(instant, zoneId, DEFAULT_DATE_TIME_FORMAT);
    }

    public static String toFormattedDateTime(Instant instant, ZoneId zoneId, DateTimeFormatter dateTimeFormatter) {
        return instant.atZone(zoneId).format(dateTimeFormatter);
    }
}

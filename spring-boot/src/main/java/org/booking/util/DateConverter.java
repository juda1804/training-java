package org.booking.util;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.util.Date;

public class DateConverter {
    private static final ZoneId zoneId = ZoneId.of("UTC");

    public static LocalDate convertToLocalDate(Date date) {
        return date.toInstant()
                .atZone(zoneId)
                .toLocalDate();
    }

    public static Date convertToDate(LocalDate localDate) {
        var instant = localDate.atStartOfDay(zoneId).toInstant();
        return Date.from(instant);
    }

    public static Date convertToDate(LocalDateTime localDateTime) {
        var instant = localDateTime.atZone(zoneId).toInstant();
        return Date.from(instant);
    }
}

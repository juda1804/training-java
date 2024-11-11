package org.booking.util;


import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;

public class DateConverterTest {

    @Test
    public void testConvertToLocalDate() {
        var date = new Date();
        var localDate = DateConverter.convertToLocalDate(date);

        Assertions.assertEquals(localDate, LocalDate.now());
    }

    @Test
    public void testConvertToDateFromLocalDate() {
        var localDate = LocalDate.now();
        var date = DateConverter.convertToDate(localDate);

        Assertions.assertEquals(
                date.toInstant().getEpochSecond(),
                localDate.atStartOfDay(ZoneId.of("UTC")).toInstant().getEpochSecond()
        );
    }

    @Test
    public void testConvertToDateFromLocalDateTime() {
        var localDateTime = LocalDateTime.now();
        var date = DateConverter.convertToDate(localDateTime);

        Assertions.assertEquals(
                date.toInstant().getEpochSecond(),
                localDateTime.atZone(ZoneId.of("UTC")).toInstant().getEpochSecond()
        );
    }
}

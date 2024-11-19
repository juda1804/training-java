package org.booking;

import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Date;

public class CommonUtilTest {

    public static Date convertToDate(LocalDate localDate) {
        var instant = localDate.atStartOfDay(ZoneId.systemDefault()).toInstant();
        return Date.from(instant);
    }
}

package org.booking.util;

import java.util.Random;

public class IdentifierGenerator {
    private static final Random random = new Random();

    public static long generateId() {
        final long origin = 10000L;
        final long bound = 1000000L;

        return random.nextLong(origin, bound);
    }
}

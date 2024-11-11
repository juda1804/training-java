package org.booking.util;


import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class IdentifierGeneratorTest {

    @Test
    public void testGenerateId() {
        var id = IdentifierGenerator.generateId();

        Assertions.assertTrue(10000L <= id && id <= 1000000L);
    }
}

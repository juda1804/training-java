package org.booking;

import org.booking.facade.BookingFacadeImplIntegrationTest;
import org.booking.service.EventServiceIntegrationTest;
import org.booking.service.TicketServiceIntegrationTest;
import org.booking.service.UserAccountServiceIntegrationTest;
import org.junit.runner.RunWith;
import org.junit.runners.Suite;

@RunWith(Suite.class)
@Suite.SuiteClasses ({
        BookingFacadeImplIntegrationTest.class,
        EventServiceIntegrationTest.class,
        TicketServiceIntegrationTest.class,
        UserAccountServiceIntegrationTest.class
})
public class BookingFeatureTestSuite { }

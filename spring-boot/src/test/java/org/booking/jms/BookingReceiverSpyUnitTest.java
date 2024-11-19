package org.booking.jms;

import org.booking.TestWebApplication;
import org.booking.facade.BookingFacade;
import org.booking.model.Ticket;
import org.junit.Test;
import org.junit.jupiter.api.Assertions;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Spy;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.boot.test.context.SpringBootTest;

import static org.booking.util.IdentifierGenerator.generateId;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.MOCK, classes = TestWebApplication.class)
public class BookingReceiverSpyUnitTest {
    @Spy
    private BookingFacade bookingFacade;

    @InjectMocks
    private BookingReceiver bookingReceiver;

    @Test
    public void testBookTicket() {

        var ticket = new Ticket();
        ticket.setUserId(generateId());
        ticket.setEventId(generateId());
        ticket.setPlace(10);
        ticket.setCategory(Ticket.Category.STANDARD);

        doReturn(ticket).when(bookingFacade).bookTicket(
                ticket.getUserId(),
                ticket.getEventId(),
                ticket.getPlace(),
                ticket.getCategory()
        );

        bookingReceiver.bookTicket(ticket);

        verify(bookingFacade, times(1)).bookTicket(
                ticket.getUserId(),
                ticket.getEventId(),
                ticket.getPlace(),
                ticket.getCategory()
        );
    }

    @Test
    public void testBookTicketIfFails() {

        var ticket = new Ticket();
        ticket.setUserId(generateId());
        ticket.setEventId(generateId());
        ticket.setPlace(10);
        ticket.setCategory(Ticket.Category.STANDARD);

        doThrow(new IllegalArgumentException("Error")).when(bookingFacade).bookTicket(
                ticket.getUserId(),
                ticket.getEventId(),
                ticket.getPlace(),
                ticket.getCategory()
        );

        Assertions.assertThrows(IllegalArgumentException.class, () -> bookingReceiver.bookTicket(ticket));
    }
}

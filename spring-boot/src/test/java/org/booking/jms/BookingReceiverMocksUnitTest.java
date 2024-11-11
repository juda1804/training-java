package org.booking.jms;

import org.booking.facade.BookingFacade;
import org.booking.model.Ticket;
import org.junit.Test;
import org.junit.jupiter.api.Assertions;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.boot.test.context.SpringBootTest;

import static org.mockito.Mockito.when;
import static org.booking.util.IdentifierGenerator.generateId;

@RunWith(MockitoJUnitRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.NONE)
public class BookingReceiverMocksUnitTest {
    @Mock
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

        when(
                bookingFacade.bookTicket(
                        ticket.getUserId(),
                        ticket.getEventId(),
                        ticket.getPlace(),
                        ticket.getCategory()
                )
        ).thenReturn(ticket);

        bookingReceiver.bookTicket(ticket);
    }

    @Test
    public void testBookTicketIfFails() {

        var ticket = new Ticket();
        ticket.setUserId(generateId());
        ticket.setEventId(generateId());
        ticket.setPlace(10);
        ticket.setCategory(Ticket.Category.STANDARD);

        when(
                bookingFacade.bookTicket(
                        ticket.getUserId(),
                        ticket.getEventId(),
                        ticket.getPlace(),
                        ticket.getCategory()
                )
        ).thenThrow(new IllegalArgumentException("Error"));

        Assertions.assertThrows(IllegalArgumentException.class, () -> bookingReceiver.bookTicket(ticket));
    }
}

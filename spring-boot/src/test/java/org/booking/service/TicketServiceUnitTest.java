package org.booking.service;

import org.booking.data.repository.TicketRepository;
import org.booking.model.Ticket;
import org.junit.jupiter.api.Assertions;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.junit.MockitoJUnitRunner;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.function.Function;

import static org.booking.util.IdentifierGenerator.generateId;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.NONE)
public class TicketServiceUnitTest {

    @Mock
    private TicketRepository ticketDao;

    @InjectMocks
    private TicketService ticketService;

    @Test
    public void testBookTicket() {
        var ticket = new Ticket();
        ticket.setId(generateId());
        ticket.setEventId(generateId());
        ticket.setUserId(generateId());
        ticket.setCategory(Ticket.Category.STANDARD);
        ticket.setPlace(1);

        when(ticketDao.save(ticket)).thenReturn(ticket);

        var storedTicket = ticketService.book(ticket);

        Assertions.assertEquals(ticket.getId(), storedTicket.getId());
        Assertions.assertEquals(ticket.getEventId(), storedTicket.getEventId());
        Assertions.assertEquals(ticket.getUserId(), storedTicket.getUserId());
        Assertions.assertEquals(ticket.getCategory(), storedTicket.getCategory());
        Assertions.assertEquals(ticket.getPlace(), storedTicket.getPlace());
    }

    @Test
    public void testGetBookedTicketsByUserId() {
        var userId1 = generateId();

        var ticket1 = new Ticket();
        ticket1.setId(generateId());
        ticket1.setEventId(generateId());
        ticket1.setUserId(userId1);
        ticket1.setCategory(Ticket.Category.STANDARD);
        ticket1.setPlace(1);

        var ticket2 = new Ticket();
        ticket2.setId(generateId());
        ticket2.setEventId(generateId());
        ticket2.setUserId(userId1);
        ticket2.setCategory(Ticket.Category.STANDARD);
        ticket2.setPlace(4);

        var ticket3 = new Ticket();
        ticket3.setId(generateId());
        ticket3.setEventId(generateId());
        ticket3.setUserId(userId1);
        ticket3.setCategory(Ticket.Category.PREMIUM);
        ticket3.setPlace(5);

        var size = 2;
        Function<Integer, Pageable> createPageable = (pageNum) -> PageRequest.of(pageNum, size);

        when(ticketDao.findByUserId(userId1, createPageable.apply(0)))
                .thenReturn(List.of(ticket1, ticket2));
        when(ticketDao.findByUserId(userId1, createPageable.apply(1)))
                .thenReturn(List.of(ticket3));
        when(ticketDao.findByUserId(userId1, createPageable.apply(2)))
                .thenReturn(List.of());

        var ticketPage1 = ticketService.getBookedTicketsByUserId(userId1, 2, 0);
        var ticketPage2 = ticketService.getBookedTicketsByUserId(userId1, 2, 1);
        var ticketPage3 = ticketService.getBookedTicketsByUserId(userId1, 2, 2);

        Assertions.assertEquals(2, ticketPage1.size());
        Assertions.assertEquals(1, ticketPage2.size());
        Assertions.assertEquals(0, ticketPage3.size());
    }

    @Test
    public void testGetBookedTicketsByEventIdWithPagination() {
        var eventId1 = generateId();

        var ticket1 = new Ticket();
        ticket1.setId(generateId());
        ticket1.setEventId(eventId1);
        ticket1.setUserId(generateId());
        ticket1.setCategory(Ticket.Category.STANDARD);
        ticket1.setPlace(1);

        var ticket2 = new Ticket();
        ticket2.setId(generateId());
        ticket2.setEventId(eventId1);
        ticket2.setUserId(generateId());
        ticket2.setCategory(Ticket.Category.PREMIUM);
        ticket2.setPlace(3);

        var ticket3 = new Ticket();
        ticket3.setId(generateId());
        ticket3.setEventId(eventId1);
        ticket3.setUserId(generateId());
        ticket3.setCategory(Ticket.Category.PREMIUM);
        ticket3.setPlace(5);

        var size = 2;
        Function<Integer, Pageable> createPageable = (pageNum) -> PageRequest.of(pageNum, size);

        when(ticketDao.findByEventId(eventId1, createPageable.apply(0)))
                .thenReturn(List.of(ticket1, ticket2));
        when(ticketDao.findByEventId(eventId1, createPageable.apply(1)))
                .thenReturn(List.of(ticket3));
        when(ticketDao.findByEventId(eventId1, createPageable.apply(2)))
                .thenReturn(List.of());

        var ticketPage1 = ticketService.getBookedTicketsByEventId(eventId1, 2, 0);
        var ticketPage2 = ticketService.getBookedTicketsByEventId(eventId1, 2, 1);
        var ticketPage3 = ticketService.getBookedTicketsByEventId(eventId1, 2, 2);

        Assertions.assertEquals(2, ticketPage1.size());
        Assertions.assertEquals(1, ticketPage2.size());
        Assertions.assertEquals(0, ticketPage3.size());
    }

    @Test
    public void testGetBookedTicketsByEventId() {
        var eventId1 = generateId();

        var ticket1 = new Ticket();
        ticket1.setId(generateId());
        ticket1.setEventId(eventId1);
        ticket1.setUserId(generateId());
        ticket1.setCategory(Ticket.Category.STANDARD);
        ticket1.setPlace(1);

        var ticket2 = new Ticket();
        ticket2.setId(generateId());
        ticket2.setEventId(eventId1);
        ticket2.setUserId(generateId());
        ticket2.setCategory(Ticket.Category.PREMIUM);
        ticket2.setPlace(3);

        var ticket3 = new Ticket();
        ticket3.setId(generateId());
        ticket3.setEventId(eventId1);
        ticket3.setUserId(generateId());
        ticket3.setCategory(Ticket.Category.PREMIUM);
        ticket3.setPlace(5);

        when(ticketDao.findByEventId(eventId1)).thenReturn(List.of(ticket1, ticket2, ticket3));

        var ticketPage1 = ticketService.getBookedTicketsByEventId(eventId1);

        Assertions.assertEquals(3, ticketPage1.size());
    }

    @Test
    public void testCancelTicket() {
        var id = generateId();

        var wasDeleted = ticketService.cancel(id);
        Assertions.assertTrue(wasDeleted);
    }
}

package org.booking.service;

import org.booking.data.repository.TicketRepository;
import org.booking.model.Ticket;
import org.junit.Before;
import org.junit.jupiter.api.Assertions;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.util.List;

import static org.booking.util.IdentifierGenerator.generateId;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations={"/applicationContext.xml"})
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.NONE)
public class TicketServiceIntegrationTest {

    @Autowired
    private TicketRepository ticketDao;

    @Autowired
    private TicketService ticketService;

    @Before
    public void resetDb() {
        ticketDao.deleteAll();
    }

    @Test
    public void testSaveTicket() {
        var ticket = new Ticket();
        ticket.setId(generateId());
        ticket.setEventId(generateId());
        ticket.setUserId(generateId());
        ticket.setCategory(Ticket.Category.STANDARD);
        ticket.setPlace(1);

        var storedTicket = ticketService.book(ticket);
        Assertions.assertEquals(ticket.getId(), storedTicket.getId());
        Assertions.assertEquals(ticket.getEventId(), storedTicket.getEventId());
        Assertions.assertEquals(ticket.getUserId(), storedTicket.getUserId());
        Assertions.assertEquals(ticket.getCategory(), storedTicket.getCategory());
        Assertions.assertEquals(ticket.getPlace(), storedTicket.getPlace());
    }

    @Test
    public void testSaveTicketIfItWasAlreadyStored() {
        var id = generateId();

        var ticket1 = new Ticket();
        ticket1.setId(id);
        ticket1.setEventId(generateId());
        ticket1.setUserId(generateId());
        ticket1.setCategory(Ticket.Category.STANDARD);
        ticket1.setPlace(1);

        var storedTicket1 = ticketDao.save(ticket1);
        Assertions.assertEquals(id, storedTicket1.getId());
        Assertions.assertEquals(ticket1.getEventId(), storedTicket1.getEventId());
        Assertions.assertEquals(ticket1.getUserId(), storedTicket1.getUserId());
        Assertions.assertEquals(ticket1.getCategory(), storedTicket1.getCategory());
        Assertions.assertEquals(ticket1.getPlace(), storedTicket1.getPlace());

        var ticket2 = new Ticket();
        ticket2.setId(id);
        ticket2.setEventId(generateId());
        ticket2.setUserId(generateId());
        ticket2.setCategory(Ticket.Category.PREMIUM);
        ticket2.setPlace(5);

        var storedTicket2 = ticketService.book(ticket2);
        Assertions.assertEquals(id, storedTicket2.getId());
        Assertions.assertEquals(ticket2.getEventId(), storedTicket2.getEventId());
        Assertions.assertEquals(ticket2.getUserId(), storedTicket2.getUserId());
        Assertions.assertEquals(ticket2.getCategory(), storedTicket2.getCategory());
        Assertions.assertEquals(ticket2.getPlace(), storedTicket2.getPlace());
    }

    @Test
    public void testFindBookedTicketsByUserId() {
        var userId1 = generateId();
        var userId2 = generateId();
        var userId3 = generateId();

        var ticket1 = new Ticket();
        ticket1.setId(generateId());
        ticket1.setEventId(generateId());
        ticket1.setUserId(userId1);
        ticket1.setCategory(Ticket.Category.STANDARD);
        ticket1.setPlace(1);

        var ticket2 = new Ticket();
        ticket2.setId(generateId());
        ticket2.setEventId(generateId());
        ticket2.setUserId(userId2);
        ticket2.setCategory(Ticket.Category.STANDARD);
        ticket2.setPlace(2);

        var ticket3 = new Ticket();
        ticket3.setId(generateId());
        ticket3.setEventId(generateId());
        ticket3.setUserId(userId3);
        ticket3.setCategory(Ticket.Category.PREMIUM);
        ticket3.setPlace(3);

        var ticket4 = new Ticket();
        ticket4.setId(generateId());
        ticket4.setEventId(generateId());
        ticket4.setUserId(userId1);
        ticket4.setCategory(Ticket.Category.STANDARD);
        ticket4.setPlace(4);

        var ticket5 = new Ticket();
        ticket5.setId(generateId());
        ticket5.setEventId(generateId());
        ticket5.setUserId(userId1);
        ticket5.setCategory(Ticket.Category.PREMIUM);
        ticket5.setPlace(5);

        List.of(ticket1, ticket2, ticket3, ticket4, ticket5)
                .forEach(ticketDao::save);

        var ticketPage1 = ticketService.getBookedTicketsByUserId(userId1, 2, 0);
        var ticketPage2 = ticketService.getBookedTicketsByUserId(userId1, 2, 1);
        var ticketPage3 = ticketService.getBookedTicketsByUserId(userId1, 2, 2);

        Assertions.assertEquals(2, ticketPage1.size());
        Assertions.assertEquals(1, ticketPage2.size());
        Assertions.assertEquals(0, ticketPage3.size());
    }

    @Test
    public void testFindBookedTicketsByEventIdWithPagination() {
        var eventId1 = generateId();
        var eventId2 = generateId();
        var eventId3 = generateId();

        var ticket1 = new Ticket();
        ticket1.setId(generateId());
        ticket1.setEventId(eventId1);
        ticket1.setUserId(generateId());
        ticket1.setCategory(Ticket.Category.STANDARD);
        ticket1.setPlace(1);

        var ticket2 = new Ticket();
        ticket2.setId(generateId());
        ticket2.setEventId(eventId2);
        ticket2.setUserId(generateId());
        ticket2.setCategory(Ticket.Category.STANDARD);
        ticket2.setPlace(2);

        var ticket3 = new Ticket();
        ticket3.setId(generateId());
        ticket3.setEventId(eventId1);
        ticket3.setUserId(generateId());
        ticket3.setCategory(Ticket.Category.PREMIUM);
        ticket3.setPlace(3);

        var ticket4 = new Ticket();
        ticket4.setId(generateId());
        ticket4.setEventId(eventId3);
        ticket4.setUserId(generateId());
        ticket4.setCategory(Ticket.Category.STANDARD);
        ticket4.setPlace(4);

        var ticket5 = new Ticket();
        ticket5.setId(generateId());
        ticket5.setEventId(eventId1);
        ticket5.setUserId(generateId());
        ticket5.setCategory(Ticket.Category.PREMIUM);
        ticket5.setPlace(5);

        List.of(ticket1, ticket2, ticket3, ticket4, ticket5)
                .forEach(ticketDao::save);

        var ticketPage1 = ticketService.getBookedTicketsByEventId(eventId1, 2, 0);
        var ticketPage2 = ticketService.getBookedTicketsByEventId(eventId1, 2, 1);
        var ticketPage3 = ticketService.getBookedTicketsByEventId(eventId1, 2, 2);

        Assertions.assertEquals(2, ticketPage1.size());
        Assertions.assertEquals(1, ticketPage2.size());
        Assertions.assertEquals(0, ticketPage3.size());
    }

    @Test
    public void testFindBookedTicketsByEventId() {
        var eventId1 = generateId();
        var eventId2 = generateId();
        var eventId3 = generateId();

        var ticket1 = new Ticket();
        ticket1.setId(generateId());
        ticket1.setEventId(eventId1);
        ticket1.setUserId(generateId());
        ticket1.setCategory(Ticket.Category.STANDARD);
        ticket1.setPlace(1);

        var ticket2 = new Ticket();
        ticket2.setId(generateId());
        ticket2.setEventId(eventId2);
        ticket2.setUserId(generateId());
        ticket2.setCategory(Ticket.Category.STANDARD);
        ticket2.setPlace(2);

        var ticket3 = new Ticket();
        ticket3.setId(generateId());
        ticket3.setEventId(eventId1);
        ticket3.setUserId(generateId());
        ticket3.setCategory(Ticket.Category.PREMIUM);
        ticket3.setPlace(3);

        var ticket4 = new Ticket();
        ticket4.setId(generateId());
        ticket4.setEventId(eventId3);
        ticket4.setUserId(generateId());
        ticket4.setCategory(Ticket.Category.STANDARD);
        ticket4.setPlace(4);

        var ticket5 = new Ticket();
        ticket5.setId(generateId());
        ticket5.setEventId(eventId1);
        ticket5.setUserId(generateId());
        ticket5.setCategory(Ticket.Category.PREMIUM);
        ticket5.setPlace(5);

        List.of(ticket1, ticket2, ticket3, ticket4, ticket5)
                .forEach(ticketDao::save);

        var ticketPage1 = ticketService.getBookedTicketsByEventId(eventId1);

        Assertions.assertEquals(3, ticketPage1.size());
    }

    @Test
    public void testDeleteTicket() {
        var ticket = new Ticket();
        ticket.setId(generateId());
        ticket.setEventId(generateId());
        ticket.setUserId(generateId());
        ticket.setCategory(Ticket.Category.STANDARD);
        ticket.setPlace(1);

        var storedTicket = ticketDao.save(ticket);
        Assertions.assertEquals(ticket.getId(), storedTicket.getId());
        Assertions.assertEquals(ticket.getEventId(), storedTicket.getEventId());
        Assertions.assertEquals(ticket.getUserId(), storedTicket.getUserId());
        Assertions.assertEquals(ticket.getCategory(), storedTicket.getCategory());
        Assertions.assertEquals(ticket.getPlace(), storedTicket.getPlace());

        var wasDeleted = ticketService.cancel(ticket.getId());
        Assertions.assertTrue(wasDeleted);
    }
}

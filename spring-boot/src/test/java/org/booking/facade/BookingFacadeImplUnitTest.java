package org.booking.facade;

import org.booking.TestWebApplication;
import org.booking.model.Event;
import org.booking.model.Ticket;
import org.booking.model.User;
import org.booking.model.UserAccount;
import org.booking.service.EventService;
import org.booking.service.TicketService;
import org.booking.service.UserAccountService;
import org.booking.service.UserService;
import org.junit.Test;
import org.junit.jupiter.api.Assertions;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.boot.test.context.SpringBootTest;

import java.time.Instant;
import java.time.LocalDate;
import java.util.Date;
import java.util.List;
import java.util.NoSuchElementException;

import static org.booking.CommonUtilTest.convertToDate;
import static org.booking.util.IdentifierGenerator.generateId;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.MOCK, classes = TestWebApplication.class)
public class BookingFacadeImplUnitTest {
    @Mock
    private EventService eventService;

    @Mock
    private TicketService ticketService;

    @Mock
    private UserService userService;

    @Mock
    private UserAccountService userAccountService;

    @InjectMocks
    private BookingFacadeImpl bookingFacade;


    @Test
    public void testCreateEvent() {
        var event = new Event();
        event.setId(generateId());
        event.setDate(Date.from(Instant.now()));
        event.setTitle("Event1");

        when(eventService.create(event)).thenReturn(event);

        var storedEvent = bookingFacade.createEvent(event);
        Assertions.assertEquals(event.getId(), storedEvent.getId());
        Assertions.assertEquals(event.getDate().getTime(), storedEvent.getDate().getTime());
        Assertions.assertEquals(event.getTitle(), storedEvent.getTitle());
    }

    @Test
    public void testGetById() {
        var id = generateId();
        var event = new Event();
        event.setId(id);
        event.setDate(Date.from(Instant.now()));
        event.setTitle("Event1");

        when(eventService.getEventById(id)).thenReturn(event);
        var eventResult = bookingFacade.getEventById(id);

        Assertions.assertEquals(id, eventResult.getId());
    }

    @Test
    public void testGetEventByTitle() {
        var event1 = new Event();
        event1.setId(generateId());
        event1.setDate(Date.from(Instant.now()));
        event1.setTitle("Event1");

        var event2 = new Event();
        event2.setId(generateId());
        event2.setDate(Date.from(Instant.now()));
        event2.setTitle("Event2");

        var event3 = new Event();
        event3.setId(generateId());
        event3.setDate(Date.from(Instant.now()));
        event3.setTitle("Event3");

        when(eventService.getEventsByTitle("Event", 2, 0))
                .thenReturn(List.of(event1, event2));
        when(eventService.getEventsByTitle("Event", 2, 1))
                .thenReturn(List.of(event3));
        when(eventService.getEventsByTitle("Event", 2, 2))
                .thenReturn(List.of());

        var eventsPage1 = bookingFacade.getEventsByTitle("Event", 2, 0);
        var eventsPage2 = bookingFacade.getEventsByTitle("Event", 2, 1);
        var eventsPage3 = bookingFacade.getEventsByTitle("Event", 2, 2);

        Assertions.assertEquals(2, eventsPage1.size());
        Assertions.assertEquals(1, eventsPage2.size());
        Assertions.assertEquals(0, eventsPage3.size());
    }

    @Test
    public void testFilterEventForDay() {

        var event1 = new Event();
        event1.setId(generateId());
        event1.setDate(convertToDate(LocalDate.now().plusDays(1)));
        event1.setTitle("Event2");

        var event2 = new Event();
        event2.setId(generateId());
        event2.setDate(convertToDate(LocalDate.now().plusDays(1)));
        event2.setTitle("Concert1");

        var event3 = new Event();
        event3.setId(generateId());
        event3.setDate(convertToDate(LocalDate.now().plusDays(1)));
        event3.setTitle("Concert2");

        var expectedDay = LocalDate.now().plusDays(1);

        when(eventService.getEventsForDay(convertToDate(expectedDay), 2, 0))
                .thenReturn(List.of(event1, event2));
        when(eventService.getEventsForDay(convertToDate(expectedDay), 2, 1))
                .thenReturn(List.of(event3));
        when(eventService.getEventsForDay(convertToDate(expectedDay), 2, 2))
                .thenReturn(List.of());

        var eventsPage1 = bookingFacade.getEventsForDay(convertToDate(expectedDay), 2, 0);
        var eventsPage2 = bookingFacade.getEventsForDay(convertToDate(expectedDay), 2, 1);
        var eventsPage3 = bookingFacade.getEventsForDay(convertToDate(expectedDay), 2, 2);

        Assertions.assertEquals(2, eventsPage1.size());
        Assertions.assertEquals(1, eventsPage2.size());
        Assertions.assertEquals(0, eventsPage3.size());
    }

    @Test
    public void testUpdateEvent() {
        var event = new Event();
        event.setId(generateId());
        event.setDate(Date.from(Instant.now()));
        event.setTitle("Event1");

        when(eventService.update(event)).thenReturn(event);

        var updatedEvent = bookingFacade.updateEvent(event);
        Assertions.assertEquals(event.getId(), updatedEvent.getId());
        Assertions.assertEquals(event.getDate().getTime(), updatedEvent.getDate().getTime());
        Assertions.assertEquals(event.getTitle(), updatedEvent.getTitle());

    }

    @Test
    public void testDeleteEvent() {
        var id = generateId();

        when(eventService.delete(id)).thenReturn(true);

        var wasDeleted = bookingFacade.deleteEvent(id);
        Assertions.assertTrue(wasDeleted);
    }

    @Test
    public void testDeleteEventIfItHasNotBeenStore() {
        var id = generateId();

        when(eventService.delete(id)).thenReturn(false);

        var wasDeleted = bookingFacade.deleteEvent(id);
        Assertions.assertFalse(wasDeleted);
    }

    @Test
    public void testBookTicket() {

        var user = new User();
        user.setId(generateId());
        user.setName("John");
        user.setEmail("john@email.com");

        var event = new Event();
        event.setId(generateId());
        event.setTitle("Event1");
        event.setDate(convertToDate(LocalDate.now().plusDays(10)));
        event.setTicketPrice(10.0);

        var ticket = new Ticket();
        ticket.setId(generateId());
        ticket.setEventId(event.getId());
        ticket.setUserId(user.getId());
        ticket.setCategory(Ticket.Category.STANDARD);
        ticket.setPlace(1);

        var userAccount1 = new UserAccount();
        userAccount1.setId(generateId());
        userAccount1.setUserId(user.getId());
        userAccount1.setAmount(35.0);

        var userAccount2 = new UserAccount();
        userAccount2.setId(generateId());
        userAccount2.setUserId(user.getId());
        userAccount2.setAmount(10.0);

        var userAccount3 = new UserAccount();
        userAccount3.setId(generateId());
        userAccount3.setUserId(user.getId());
        userAccount3.setAmount(25.0);

        when(eventService.getEventById(event.getId())).thenReturn(event);
        when(userAccountService.findUserAccountByUserId(user.getId())).thenReturn(List.of(userAccount1, userAccount2, userAccount3));
        when(ticketService.book(any())).thenReturn(ticket);

        var storedTicket = bookingFacade.bookTicket(ticket.getUserId(), ticket.getEventId(), ticket.getPlace(), ticket.getCategory());

        Assertions.assertEquals(ticket.getId(), storedTicket.getId());
        Assertions.assertEquals(ticket.getEventId(), storedTicket.getEventId());
        Assertions.assertEquals(ticket.getUserId(), storedTicket.getUserId());
        Assertions.assertEquals(ticket.getCategory(), storedTicket.getCategory());
        Assertions.assertEquals(ticket.getPlace(), storedTicket.getPlace());
    }

    @Test
    public void testBookTicketWhenUserAccountsHasAInsufficientAmount() {

        var user = new User();
        user.setId(generateId());
        user.setName("John");
        user.setEmail("john@email.com");

        var event = new Event();
        event.setId(generateId());
        event.setTitle("Event1");
        event.setDate(convertToDate(LocalDate.now().plusDays(10)));
        event.setTicketPrice(100.0);

        var ticket = new Ticket();
        ticket.setId(generateId());
        ticket.setEventId(event.getId());
        ticket.setUserId(user.getId());
        ticket.setCategory(Ticket.Category.STANDARD);
        ticket.setPlace(1);

        var userAccount1 = new UserAccount();
        userAccount1.setId(generateId());
        userAccount1.setUserId(user.getId());
        userAccount1.setAmount(35.0);

        var userAccount2 = new UserAccount();
        userAccount2.setId(generateId());
        userAccount2.setUserId(user.getId());
        userAccount2.setAmount(10.0);

        var userAccount3 = new UserAccount();
        userAccount3.setId(generateId());
        userAccount3.setUserId(user.getId());
        userAccount3.setAmount(25.0);

        when(eventService.getEventById(event.getId())).thenReturn(event);
        when(userAccountService.findUserAccountByUserId(user.getId())).thenReturn(List.of(userAccount1, userAccount2, userAccount3));

        Assertions.assertThrows(NoSuchElementException.class, () ->
                bookingFacade.bookTicket(ticket.getUserId(), ticket.getEventId(), ticket.getPlace(), ticket.getCategory()));
    }

    @Test
    public void testGetBookedTicketsByUserId() {
        var userId1 = generateId();
        var user = new User();
        user.setId(userId1);
        user.setName("John");
        user.setEmail("john@email.com");

        var eventId1 = generateId();
        var event1 = new Event();
        event1.setId(eventId1);
        event1.setTitle("event1");
        event1.setDate(convertToDate(LocalDate.now().plusDays(3)));

        var eventId2 = generateId();
        var event2 = new Event();
        event2.setId(eventId2);
        event2.setTitle("event2");
        event2.setDate(convertToDate(LocalDate.now().plusDays(1)));

        var eventId3 = generateId();
        var event3 = new Event();
        event3.setId(eventId3);
        event3.setTitle("event1");
        event3.setDate(convertToDate(LocalDate.now().plusDays(5)));

        var ticket1 = new Ticket();
        ticket1.setId(generateId());
        ticket1.setEventId(eventId1);
        ticket1.setUserId(userId1);
        ticket1.setCategory(Ticket.Category.STANDARD);
        ticket1.setPlace(1);

        var ticket2 = new Ticket();
        ticket2.setId(generateId());
        ticket2.setEventId(eventId2);
        ticket2.setUserId(userId1);
        ticket2.setCategory(Ticket.Category.STANDARD);
        ticket2.setPlace(4);

        var ticket3 = new Ticket();
        ticket3.setId(generateId());
        ticket3.setEventId(eventId3);
        ticket3.setUserId(userId1);
        ticket3.setCategory(Ticket.Category.PREMIUM);
        ticket3.setPlace(5);

        when(ticketService.getBookedTicketsByUserId(userId1, 3, 0))
                .thenReturn(List.of(ticket1, ticket2, ticket3));
        when(ticketService.getBookedTicketsByUserId(userId1, 2, 0))
                .thenReturn(List.of(ticket1, ticket2));
        when(ticketService.getBookedTicketsByUserId(userId1, 2, 1))
                .thenReturn(List.of(ticket3));
        when(ticketService.getBookedTicketsByUserId(userId1, 2, 2))
                .thenReturn(List.of());

        when(eventService.getEventById(eventId1)).thenReturn(event1);
        when(eventService.getEventById(eventId2)).thenReturn(event2);
        when(eventService.getEventById(eventId3)).thenReturn(event3);

        var ticketPage1 = bookingFacade.getBookedTickets(user, 2, 0);
        var ticketPage2 = bookingFacade.getBookedTickets(user, 2, 1);
        var ticketPage3 = bookingFacade.getBookedTickets(user, 2, 2);

        Assertions.assertEquals(2, ticketPage1.size());
        Assertions.assertEquals(1, ticketPage2.size());
        Assertions.assertEquals(0, ticketPage3.size());

        var allTickets = bookingFacade.getBookedTickets(user, 3, 0);
        Assertions.assertEquals(List.of(eventId3, eventId1, eventId2), allTickets.stream().map(Ticket::getEventId).toList());
    }

    @Test
    public void testGetBookedTicketsByEventId() {
        var eventId1 = generateId();
        var event = new Event();
        event.setId(eventId1);
        event.setTitle("event1");
        event.setDate(convertToDate(LocalDate.now().plusDays(1)));

        var userId1 = generateId();
        var user1 = new User();
        user1.setId(userId1);
        user1.setName("Maria");
        user1.setEmail("maria@email.com");

        var userId2 = generateId();
        var user2 = new User();
        user2.setId(userId2);
        user2.setName("John");
        user2.setEmail("john@email.com");

        var userId3 = generateId();
        var user3 = new User();
        user3.setId(userId3);
        user3.setName("Aaron");
        user3.setEmail("aaron@email.com");

        var ticket1 = new Ticket();
        ticket1.setId(generateId());
        ticket1.setEventId(eventId1);
        ticket1.setUserId(userId1);
        ticket1.setCategory(Ticket.Category.STANDARD);
        ticket1.setPlace(1);

        var ticket2 = new Ticket();
        ticket2.setId(generateId());
        ticket2.setEventId(eventId1);
        ticket2.setUserId(userId2);
        ticket2.setCategory(Ticket.Category.PREMIUM);
        ticket2.setPlace(3);

        var ticket3 = new Ticket();
        ticket3.setId(generateId());
        ticket3.setEventId(eventId1);
        ticket3.setUserId(userId3);
        ticket3.setCategory(Ticket.Category.PREMIUM);
        ticket3.setPlace(5);

        when(ticketService.getBookedTicketsByEventId(eventId1, 3, 0))
                .thenReturn(List.of(ticket1, ticket2, ticket3));
        when(ticketService.getBookedTicketsByEventId(eventId1, 2, 0))
                .thenReturn(List.of(ticket1, ticket2));
        when(ticketService.getBookedTicketsByEventId(eventId1, 2, 1))
                .thenReturn(List.of(ticket3));
        when(ticketService.getBookedTicketsByEventId(eventId1, 2, 2))
                .thenReturn(List.of());

        when(userService.getUserById(userId1)).thenReturn(user1);
        when(userService.getUserById(userId2)).thenReturn(user2);
        when(userService.getUserById(userId3)).thenReturn(user3);

        var ticketPage1 = bookingFacade.getBookedTickets(event, 2, 0);
        var ticketPage2 = bookingFacade.getBookedTickets(event, 2, 1);
        var ticketPage3 = bookingFacade.getBookedTickets(event, 2, 2);

        Assertions.assertEquals(2, ticketPage1.size());
        Assertions.assertEquals(1, ticketPage2.size());
        Assertions.assertEquals(0, ticketPage3.size());

        var allTickets = bookingFacade.getBookedTickets(event, 3, 0);
        Assertions.assertEquals(List.of(userId3, userId2, userId1), allTickets.stream().map(Ticket::getUserId).toList());
    }

    @Test
    public void testCancelTicket() {
        var id = generateId();

        when(ticketService.cancel(id)).thenReturn(true);

        var wasDeleted = bookingFacade.cancelTicket(id);
        Assertions.assertTrue(wasDeleted);
    }

    @Test
    public void testCancelTicketIfItHasNotBeenStore() {
        var id = generateId();

        when(ticketService.cancel(id)).thenReturn(false);

        var wasDeleted = bookingFacade.cancelTicket(id);
        Assertions.assertFalse(wasDeleted);
    }

    @Test
    public void testCreateUser() {
        var user = new User();
        user.setId(generateId());
        user.setName("John");
        user.setEmail("john@email.com");

        when(userService.create(user)).thenReturn(user);

        var storedTicket = bookingFacade.createUser(user);

        Assertions.assertEquals(user.getId(), storedTicket.getId());
        Assertions.assertEquals(user.getName(), storedTicket.getName());
        Assertions.assertEquals(user.getEmail(), storedTicket.getEmail());
    }

    @Test
    public void testGetUserById() {
        var id = generateId();
        var user = new User();
        user.setId(id);
        user.setName("John");
        user.setEmail("john@email.com");

        when(userService.getUserById(id)).thenReturn(user);

        var result = bookingFacade.getUserById(id);

        Assertions.assertEquals(id, result.getId());
    }

    @Test
    public void testGetUserByEmail() {
        var id = generateId();
        var user = new User();
        user.setId(id);
        user.setName("John");
        user.setEmail("john@email.com");

        when(userService.getUserByEmail(user.getEmail())).thenReturn(user);

        var result = bookingFacade.getUserByEmail(user.getEmail());
        Assertions.assertEquals(id, result.getId());
    }

    @Test
    public void testGetUsersByName() {
        var name = "John";
        var email = "john@email.com";

        var user1 = new User();
        user1.setId(generateId());
        user1.setName(name);
        user1.setEmail(email);

        var user2 = new User();
        user2.setId(generateId());
        user2.setName(name);
        user2.setEmail(email);

        var user3 = new User();
        user3.setId(generateId());
        user3.setName(name);
        user3.setEmail(email);

        when(userService.getUsersByName(name, 2, 0))
                .thenReturn(List.of(user1, user2));
        when(userService.getUsersByName(name, 2, 1))
                .thenReturn(List.of(user3));
        when(userService.getUsersByName(name, 2, 2))
                .thenReturn(List.of());

        var userPage1 = bookingFacade.getUsersByName(name, 2, 0);
        var userPage2 = bookingFacade.getUsersByName(name, 2, 1);
        var userPage3 = bookingFacade.getUsersByName(name, 2, 2);

        Assertions.assertEquals(2, userPage1.size());
        Assertions.assertEquals(1, userPage2.size());
        Assertions.assertEquals(0, userPage3.size());
    }

    @Test
    public void testUpdateUser() {
        var user = new User();
        user.setId(generateId());
        user.setName("John");
        user.setEmail("john@email.com");

        when(userService.update(user)).thenReturn(user);

        var updatedTicket = bookingFacade.updateUser(user);

        Assertions.assertEquals(user.getId(), updatedTicket.getId());
        Assertions.assertEquals(user.getName(), updatedTicket.getName());
        Assertions.assertEquals(user.getEmail(), updatedTicket.getEmail());
    }

    @Test
    public void testDeleteUser() {
        var id = generateId();

        when(userService.delete(id)).thenReturn(true);

        var wasDeleted = bookingFacade.deleteUser(id);
        Assertions.assertTrue(wasDeleted);
    }

    @Test
    public void testDeleteUserIfItHasNotBeenStore() {
        var id = generateId();

        when(userService.delete(id)).thenReturn(false);

        var wasDeleted = bookingFacade.deleteUser(id);
        Assertions.assertFalse(wasDeleted);
    }
}

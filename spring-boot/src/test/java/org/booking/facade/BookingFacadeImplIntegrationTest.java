package org.booking.facade;

import org.booking.TestWebApplication;
import org.booking.data.repository.EventRepository;
import org.booking.data.repository.TicketRepository;
import org.booking.data.repository.UserAccountRepository;
import org.booking.data.repository.UserRepository;
import org.booking.model.Event;
import org.booking.model.Ticket;
import org.booking.model.User;
import org.booking.model.UserAccount;
import org.booking.service.EventService;
import org.booking.service.TicketService;
import org.booking.service.UserAccountService;
import org.booking.service.UserService;
import org.junit.Before;
import org.junit.jupiter.api.Assertions;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.TransactionSynchronizationManager;

import java.time.Instant;
import java.time.LocalDate;
import java.util.Date;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.function.Function;

import static org.booking.CommonUtilTest.convertToDate;
import static org.booking.util.IdentifierGenerator.generateId;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.MOCK, classes = TestWebApplication.class)
public class BookingFacadeImplIntegrationTest {

    @Autowired
    private EventRepository eventDao;
    @Autowired
    private EventService eventService;

    @Autowired
    private TicketRepository ticketRepository;
    @Autowired
    private TicketService ticketService;

    @Autowired
    private UserRepository userDao;
    @Autowired
    private UserService userService;

    @Autowired
    private UserAccountRepository userAccountDao;
    @Autowired
    private UserAccountService userAccountService;

    @Autowired
    private BookingFacade bookingFacade;

    @Before
    public void resetDb() {
        System.out.println("resetDB");
        eventDao.deleteAll();
        ticketRepository.deleteAll();
        userDao.deleteAll();
    }

    @Test
    @Transactional()
    public void givenTransactional_whenCheckingForActiveTransaction_thenReceiveTrue() {
        Assertions.assertTrue(TransactionSynchronizationManager.isActualTransactionActive());
    }

    @Test
    public void testPreloadTickets() {
        bookingFacade.preloadTickets();
        var event = ticketRepository.findById(5454021L);

        Assertions.assertTrue(event.isPresent());
    }

    @Test
    public void testSaveEvent() {
        var event = new Event();
        event.setId(generateId());
        event.setDate(Date.from(Instant.now()));
        event.setTitle("Event1");

        var storedEvent = bookingFacade.createEvent(event);
        Assertions.assertEquals(event.getId(), storedEvent.getId());
        Assertions.assertEquals(event.getDate().getTime(), storedEvent.getDate().getTime());
        Assertions.assertEquals(event.getTitle(), storedEvent.getTitle());
    }

    @Test
    public void testSaveEventIfItWasAlreadyStored() {
        var id = generateId();

        var event1 = new Event();
        event1.setId(id);
        event1.setDate(Date.from(Instant.now()));
        event1.setTitle("Event1");

        var storedEvent1 = eventDao.save(event1);
        Assertions.assertEquals(event1.getId(), storedEvent1.getId());
        Assertions.assertEquals(event1.getDate().getTime(), storedEvent1.getDate().getTime());
        Assertions.assertEquals(event1.getTitle(), storedEvent1.getTitle());

        var event2 = new Event();
        event2.setId(id);
        event2.setDate(Date.from(Instant.now()));
        event2.setTitle("Event2");

        var storedEvent2 = bookingFacade.createEvent(event2);
        Assertions.assertEquals(event2.getId(), storedEvent2.getId());
        Assertions.assertEquals(event2.getDate().getTime(), storedEvent2.getDate().getTime());
        Assertions.assertEquals(event2.getTitle(), storedEvent2.getTitle());
    }

    @Test
    public void testFindById() {
        var id = generateId();
        var event = new Event();
        event.setId(id);
        event.setDate(Date.from(Instant.now()));
        event.setTitle("Event1");

        eventDao.save(event);
        var eventResult = bookingFacade.getEventById(id);

        Assertions.assertEquals(id, eventResult.getId());
    }

    @Test
    public void testFindByIdIfEventHasNotBeenStored() {
        var id = generateId();

        Assertions.assertThrows(NoSuchElementException.class, () -> bookingFacade.getEventById(id));
    }


    @Test
    public void testFindEventByTitle() {
        System.out.println("--- testFindEventByTitle ---");

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

        var event4 = new Event();
        event4.setId(generateId());
        event4.setDate(Date.from(Instant.now()));
        event4.setTitle("Concert");

        List.of(event1, event2, event3, event4)
                .forEach(eventDao::save);

        var eventsPage1 = bookingFacade.getEventsByTitle("Event", 2, 0);
        var eventsPage2 = bookingFacade.getEventsByTitle("Event", 2, 1);
        var eventsPage3 = bookingFacade.getEventsByTitle("Event", 2, 2);

        Assertions.assertEquals(2, eventsPage1.size());
        Assertions.assertEquals(1, eventsPage2.size());
        Assertions.assertEquals(0, eventsPage3.size());
    }

    @Test
    public void testFindEventByDay() {

        var event1 = new Event();
        event1.setId(generateId());
        event1.setDate(convertToDate(LocalDate.now()));
        event1.setTitle("Event1");

        var event2 = new Event();
        event2.setId(generateId());
        event2.setDate(convertToDate(LocalDate.now().plusDays(1)));
        event2.setTitle("Event2");

        var event3 = new Event();
        event3.setId(generateId());
        event3.setDate(convertToDate(LocalDate.now().minusDays(1)));
        event3.setTitle("Event3");

        var event4 = new Event();
        event4.setId(generateId());
        event4.setDate(convertToDate(LocalDate.now().plusDays(1)));
        event4.setTitle("Concert1");

        var event5 = new Event();
        event5.setId(generateId());
        event5.setDate(convertToDate(LocalDate.now().plusDays(1)));
        event5.setTitle("Concert2");

        List.of(event1, event2, event3, event4, event5)
                .forEach(eventDao::save);

        var expectedDay = LocalDate.now().plusDays(1);

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

        var storedEvent = eventDao.save(event);
        Assertions.assertEquals(event.getId(), storedEvent.getId());
        Assertions.assertEquals(event.getDate().getTime(), storedEvent.getDate().getTime());
        Assertions.assertEquals(event.getTitle(), storedEvent.getTitle());

        event.setTitle("Concert1");

        var updatedEvent = bookingFacade.updateEvent(event);
        Assertions.assertEquals(event.getId(), updatedEvent.getId());
        Assertions.assertEquals(event.getDate().getTime(), updatedEvent.getDate().getTime());
        Assertions.assertEquals(event.getTitle(), updatedEvent.getTitle());

    }

    @Test
    public void testUpdateEventIfItHasNotBeenStored() {
        var id = generateId();

        var event1 = new Event();
        event1.setId(id);
        event1.setDate(Date.from(Instant.now()));
        event1.setTitle("Event1");

        var storedEvent1 = bookingFacade.updateEvent(event1);
        Assertions.assertEquals(event1.getId(), storedEvent1.getId());
        Assertions.assertEquals(event1.getDate().getTime(), storedEvent1.getDate().getTime());
        Assertions.assertEquals(event1.getTitle(), storedEvent1.getTitle());
    }

    @Test
    public void testDeleteEvent() {
        var event = new Event();
        event.setId(generateId());
        event.setDate(Date.from(Instant.now()));
        event.setTitle("Event1");

        var storedEvent = eventDao.save(event);
        Assertions.assertEquals(event.getId(), storedEvent.getId());
        Assertions.assertEquals(event.getDate().getTime(), storedEvent.getDate().getTime());
        Assertions.assertEquals(event.getTitle(), storedEvent.getTitle());

        var wasDeleted = bookingFacade.deleteEvent(event.getId());
        Assertions.assertTrue(wasDeleted);
    }

    @Test
    public void testBookTicketWhenTicketCanBeBookedWithOneUserAccount() {
        var user = new User();
        user.setId(generateId());
        user.setName("John");
        user.setEmail("john@email.com");

        var event = new Event();
        event.setId(generateId());
        event.setTitle("Event1");
        event.setDate(convertToDate(LocalDate.now().plusDays(10)));
        event.setTicketPrice(25.0);

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

        bookingFacade.createUser(user);

        bookingFacade.createEvent(event);

        List.of(userAccount1, userAccount2, userAccount3).forEach(bookingFacade::createUserAccount);

        var storedTicket = bookingFacade.bookTicket(ticket.getUserId(), ticket.getEventId(), ticket.getPlace(), ticket.getCategory());
        Assertions.assertEquals(ticket.getEventId(), storedTicket.getEventId());
        Assertions.assertEquals(ticket.getUserId(), storedTicket.getUserId());
        Assertions.assertEquals(ticket.getCategory(), storedTicket.getCategory());
        Assertions.assertEquals(ticket.getPlace(), storedTicket.getPlace());

        var accounts = userAccountService.findUserAccountByUserId(user.getId());
        Function<Long, Optional<Double>> getAccountAmount = accountId ->
                accounts.stream()
                        .filter(userAccount -> userAccount.getId() == accountId)
                        .map(UserAccount::getAmount)
                        .findFirst();

        Assertions.assertEquals(3, accounts.size());

        Assertions.assertEquals(Optional.of(10.0), getAccountAmount.apply(userAccount1.getId()));
        Assertions.assertEquals(Optional.of(10.0), getAccountAmount.apply(userAccount2.getId()));
        Assertions.assertEquals(Optional.of(25.0), getAccountAmount.apply(userAccount3.getId()));
    }

    @Test
    public void testBookTicketWhenTicketNeedToBeBookedWithTwoUserAccounts() {
        var user = new User();
        user.setId(generateId());
        user.setName("John");
        user.setEmail("john@email.com");

        var event = new Event();
        event.setId(generateId());
        event.setTitle("Event1");
        event.setDate(convertToDate(LocalDate.now().plusDays(10)));
        event.setTicketPrice(60.0);

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

        bookingFacade.createUser(user);

        bookingFacade.createEvent(event);

        List.of(userAccount1, userAccount2, userAccount3).forEach(bookingFacade::createUserAccount);

        var storedTicket = bookingFacade.bookTicket(ticket.getUserId(), ticket.getEventId(), ticket.getPlace(), ticket.getCategory());
        Assertions.assertEquals(ticket.getEventId(), storedTicket.getEventId());
        Assertions.assertEquals(ticket.getUserId(), storedTicket.getUserId());
        Assertions.assertEquals(ticket.getCategory(), storedTicket.getCategory());
        Assertions.assertEquals(ticket.getPlace(), storedTicket.getPlace());

        var accounts = userAccountService.findUserAccountByUserId(user.getId());
        Function<Long, Optional<Double>> getAccountAmount = accountId ->
                accounts.stream()
                        .filter(userAccount -> userAccount.getId() == accountId)
                        .map(UserAccount::getAmount)
                        .findFirst();

        Assertions.assertEquals(3, accounts.size());

        Assertions.assertEquals(Optional.of(0.0), getAccountAmount.apply(userAccount1.getId()));
        Assertions.assertEquals(Optional.of(10.0), getAccountAmount.apply(userAccount2.getId()));
        Assertions.assertEquals(Optional.of(0.0), getAccountAmount.apply(userAccount3.getId()));
    }

    @Test
    public void testBookTicketWhenTicketNeedToBeBookedWithMultipleUserAccount() {
        var user = new User();
        user.setId(generateId());
        user.setName("John");
        user.setEmail("john@email.com");

        var event = new Event();
        event.setId(generateId());
        event.setTitle("Event1");
        event.setDate(convertToDate(LocalDate.now().plusDays(10)));
        event.setTicketPrice(62.0);

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

        bookingFacade.createUser(user);

        bookingFacade.createEvent(event);

        List.of(userAccount1, userAccount2, userAccount3).forEach(bookingFacade::createUserAccount);

        var storedTicket = bookingFacade.bookTicket(ticket.getUserId(), ticket.getEventId(), ticket.getPlace(), ticket.getCategory());
        Assertions.assertEquals(ticket.getEventId(), storedTicket.getEventId());
        Assertions.assertEquals(ticket.getUserId(), storedTicket.getUserId());
        Assertions.assertEquals(ticket.getCategory(), storedTicket.getCategory());
        Assertions.assertEquals(ticket.getPlace(), storedTicket.getPlace());

        var accounts = userAccountService.findUserAccountByUserId(user.getId());
        Function<Long, Optional<Double>> getAccountAmount = accountId ->
                accounts.stream()
                        .filter(userAccount -> userAccount.getId() == accountId)
                        .map(UserAccount::getAmount)
                        .findFirst();

        Assertions.assertEquals(3, accounts.size());

        Assertions.assertEquals(Optional.of(0.0), getAccountAmount.apply(userAccount1.getId()));
        Assertions.assertEquals(Optional.of(8.0), getAccountAmount.apply(userAccount2.getId()));
        Assertions.assertEquals(Optional.of(0.0), getAccountAmount.apply(userAccount3.getId()));
    }

    @Test
    public void testBookTicketIfItWasAlreadyStored() {
        var user = new User();
        user.setId(generateId());
        user.setName("John");
        user.setEmail("john@email.com");

        var event = new Event();
        event.setId(generateId());
        event.setTitle("Event1");
        event.setDate(convertToDate(LocalDate.now().plusDays(10)));
        event.setTicketPrice(10.0);

        var id = generateId();

        var ticket1 = new Ticket();
        ticket1.setId(id);
        ticket1.setEventId(event.getId());
        ticket1.setUserId(user.getId());
        ticket1.setCategory(Ticket.Category.STANDARD);
        ticket1.setPlace(1);

        var userAccount3 = new UserAccount();
        userAccount3.setId(generateId());
        userAccount3.setUserId(user.getId());
        userAccount3.setAmount(25.0);

        bookingFacade.createUser(user);

        bookingFacade.createEvent(event);

        bookingFacade.createUserAccount(userAccount3);

        var storedTicket1 = ticketRepository.save(ticket1);
        Assertions.assertEquals(id, storedTicket1.getId());
        Assertions.assertEquals(ticket1.getEventId(), storedTicket1.getEventId());
        Assertions.assertEquals(ticket1.getUserId(), storedTicket1.getUserId());
        Assertions.assertEquals(ticket1.getCategory(), storedTicket1.getCategory());
        Assertions.assertEquals(ticket1.getPlace(), storedTicket1.getPlace());

        var ticket2 = new Ticket();
        ticket2.setId(id);
        ticket2.setEventId(event.getId());
        ticket2.setUserId(user.getId());
        ticket2.setCategory(Ticket.Category.PREMIUM);
        ticket2.setPlace(5);

        var storedTicket2 = bookingFacade.bookTicket(ticket2.getUserId(), ticket2.getEventId(), ticket2.getPlace(), ticket2.getCategory());
        Assertions.assertEquals(ticket2.getEventId(), storedTicket2.getEventId());
        Assertions.assertEquals(ticket2.getUserId(), storedTicket2.getUserId());
        Assertions.assertEquals(ticket2.getCategory(), storedTicket2.getCategory());
        Assertions.assertEquals(ticket2.getPlace(), storedTicket2.getPlace());
    }

    @Test
    public void testBookTicketIfThePlaceHadAlreadyBeenBooked() {
        var user = new User();
        user.setId(generateId());
        user.setName("John");
        user.setEmail("john@email.com");

        var event = new Event();
        event.setId(generateId());
        event.setTitle("Event1");
        event.setDate(convertToDate(LocalDate.now().plusDays(10)));
        event.setTicketPrice(10.0);

        var id = generateId();

        var ticket1 = new Ticket();
        ticket1.setId(id);
        ticket1.setEventId(event.getId());
        ticket1.setUserId(user.getId());
        ticket1.setCategory(Ticket.Category.STANDARD);
        ticket1.setPlace(1);

        var userAccount3 = new UserAccount();
        userAccount3.setId(generateId());
        userAccount3.setUserId(user.getId());
        userAccount3.setAmount(25.0);

        bookingFacade.createUser(user);

        bookingFacade.createEvent(event);

        bookingFacade.createUserAccount(userAccount3);

        var storedTicket1 = ticketRepository.save(ticket1);
        Assertions.assertEquals(id, storedTicket1.getId());
        Assertions.assertEquals(ticket1.getEventId(), storedTicket1.getEventId());
        Assertions.assertEquals(ticket1.getUserId(), storedTicket1.getUserId());
        Assertions.assertEquals(ticket1.getCategory(), storedTicket1.getCategory());
        Assertions.assertEquals(ticket1.getPlace(), storedTicket1.getPlace());

        var ticket2 = new Ticket();
        ticket2.setId(id);
        ticket2.setEventId(event.getId());
        ticket2.setUserId(user.getId());
        ticket2.setCategory(Ticket.Category.PREMIUM);
        ticket2.setPlace(1);

        Assertions.assertThrows(IllegalStateException.class, () ->
                bookingFacade.bookTicket(ticket2.getUserId(), ticket2.getEventId(), ticket2.getPlace(), ticket2.getCategory()));
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
        event.setTicketPrice(90.0);

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

        bookingFacade.createUser(user);

        bookingFacade.createEvent(event);

        List.of(userAccount1, userAccount2, userAccount3).forEach(bookingFacade::createUserAccount);

        Assertions.assertThrows(NoSuchElementException.class, () ->
                bookingFacade.bookTicket(ticket.getUserId(), ticket.getEventId(), ticket.getPlace(), ticket.getCategory()));
    }

    @Test
    public void testFindBookedTicketsByUserId() {
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
        event3.setTitle("event3");
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
        ticket2.setUserId(userId2);
        ticket2.setCategory(Ticket.Category.STANDARD);
        ticket2.setPlace(2);

        var ticket3 = new Ticket();
        ticket3.setId(generateId());
        ticket3.setEventId(eventId3);
        ticket3.setUserId(userId3);
        ticket3.setCategory(Ticket.Category.PREMIUM);
        ticket3.setPlace(3);

        var ticket4 = new Ticket();
        ticket4.setId(generateId());
        ticket4.setEventId(eventId3);
        ticket4.setUserId(userId1);
        ticket4.setCategory(Ticket.Category.STANDARD);
        ticket4.setPlace(4);

        var ticket5 = new Ticket();
        ticket5.setId(generateId());
        ticket5.setEventId(eventId2);
        ticket5.setUserId(userId1);
        ticket5.setCategory(Ticket.Category.PREMIUM);
        ticket5.setPlace(5);

        List.of(ticket1, ticket2, ticket3, ticket4, ticket5)
                .forEach(ticketRepository::save);

        List.of(user1, user2, user3)
                .forEach(userDao::save);

        List.of(event1, event2, event3)
                .forEach(eventDao::save);

        var ticketPage1 = bookingFacade.getBookedTickets(user1, 2, 0);
        var ticketPage2 = bookingFacade.getBookedTickets(user1, 2, 1);
        var ticketPage3 = bookingFacade.getBookedTickets(user1, 2, 2);

        Assertions.assertEquals(2, ticketPage1.size());
        Assertions.assertEquals(1, ticketPage2.size());
        Assertions.assertEquals(0, ticketPage3.size());
    }

    @Test
    public void testFindBookedTicketsByEventId() {
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
        event3.setTitle("event3");
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
        ticket2.setUserId(userId2);
        ticket2.setCategory(Ticket.Category.STANDARD);
        ticket2.setPlace(2);

        var ticket3 = new Ticket();
        ticket3.setId(generateId());
        ticket3.setEventId(eventId1);
        ticket3.setUserId(userId3);
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
        ticket5.setUserId(userId2);
        ticket5.setCategory(Ticket.Category.PREMIUM);
        ticket5.setPlace(5);

        List.of(ticket1, ticket2, ticket3, ticket4, ticket5)
                .forEach(ticketRepository::save);

        List.of(user1, user2, user3)
                .forEach(userDao::save);

        List.of(event1, event2, event3)
                .forEach(eventDao::save);

        var ticketPage1 = bookingFacade.getBookedTickets(event1, 2, 0);
        var ticketPage2 = bookingFacade.getBookedTickets(event1, 2, 1);
        var ticketPage3 = bookingFacade.getBookedTickets(event1, 2, 2);

        Assertions.assertEquals(2, ticketPage1.size());
        Assertions.assertEquals(1, ticketPage2.size());
        Assertions.assertEquals(0, ticketPage3.size());
    }

    @Test
    public void testDeleteTicket() {
        var ticket = new Ticket();
        ticket.setId(generateId());
        ticket.setEventId(generateId());
        ticket.setUserId(generateId());
        ticket.setCategory(Ticket.Category.STANDARD);
        ticket.setPlace(1);

        var storedTicket = ticketRepository.save(ticket);
        Assertions.assertEquals(ticket.getId(), storedTicket.getId());
        Assertions.assertEquals(ticket.getEventId(), storedTicket.getEventId());
        Assertions.assertEquals(ticket.getUserId(), storedTicket.getUserId());
        Assertions.assertEquals(ticket.getCategory(), storedTicket.getCategory());
        Assertions.assertEquals(ticket.getPlace(), storedTicket.getPlace());

        var wasDeleted = bookingFacade.cancelTicket(ticket.getId());
        Assertions.assertTrue(wasDeleted);
    }

}

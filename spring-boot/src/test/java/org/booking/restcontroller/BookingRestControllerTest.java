package org.booking.restcontroller;

import org.booking.TestWebApplication;
import org.booking.auth.JwtUtil;
import org.booking.data.repository.EventRepository;
import org.booking.data.repository.TicketRepository;
import org.booking.data.repository.UserAccountRepository;
import org.booking.data.repository.UserRepository;
import org.booking.model.*;
import org.booking.restcontroller.dto.BookingResponse;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;

import java.time.LocalDate;
import java.util.*;

import static org.booking.CommonUtilTest.convertToDate;
import static org.booking.util.DateConverter.convertToLocalDate;
import static org.booking.util.IdentifierGenerator.generateId;

@SpringBootTest(classes = TestWebApplication.class, webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class BookingRestControllerTest {

    @Autowired
    private UserRepository userDao;

    @Autowired
    private EventRepository eventDao;

    @Autowired
    private TicketRepository ticketDao;

    @Autowired
    private UserAccountRepository accountDao;

    @Autowired
    private TestRestTemplate testRestTemplate;

    @BeforeEach
    public void clearDB() {
        userDao.deleteAll();
        eventDao.deleteAll();
        ticketDao.deleteAll();
        addAuthorizationHeader();
    }

    @Test
    public void testCreateNewUserAndUpdateTheirEmail() {
        var user = new User();
        user.setName("Joe");
        user.setEmail("joe@email.com");

        var response = testRestTemplate.postForEntity("/users/new", user, User.class);

        var maybeUser = userDao.findById(response.getBody().getId());

        Assertions.assertTrue(maybeUser.isPresent());

        var createdUser = maybeUser.get();

        Assertions.assertEquals(user.getName(), createdUser.getName());
        Assertions.assertEquals(user.getEmail(), createdUser.getEmail());

        var updatedEmail = "joe2@email.com";

        user.setEmail(updatedEmail);

        var url = String.format("/users/%s", user.getId());
        var request = new HttpEntity<>(user);

        testRestTemplate.exchange(url, HttpMethod.PUT, request, User.class);

        var maybeUpdatedUser = userDao.findById(user.getId())
                .stream()
                .findFirst();

        Assertions.assertTrue(maybeUpdatedUser.isPresent());

        var updatedUser = maybeUpdatedUser.get();

        Assertions.assertEquals(user.getName(), updatedUser.getName());
        Assertions.assertEquals(updatedEmail, updatedUser.getEmail());
    }

    @Test
    public void testListUsers() {
        var user1 = new User();
        user1.setId(generateId());
        user1.setName("Alejandra");
        user1.setEmail("alejandra@email.com");

        var user2 = new User();
        user2.setId(generateId());
        user2.setName("Alejandro");
        user2.setEmail("alejandro@email.com");

        var user3 = new User();
        user3.setId(generateId());
        user3.setName("Alex");
        user3.setEmail("alex@email.com");

        userDao.saveAll(List.of(user1, user2, user3));

        var url1 = String.format("/users?name=%s&pageSize=%s&pageNum=%s", "Al", "2", "0");
        var users1 = Optional.ofNullable(testRestTemplate.getForEntity(url1, User[].class).getBody())
                .stream()
                .flatMap(Arrays::stream)
                .toList();

        Assertions.assertEquals(2, users1.size());

        var url2 = String.format("/users?name=%s&pageSize=%s&pageNum=%s", "Al", "2", "1");
        var users2 = Optional.ofNullable(testRestTemplate.getForEntity(url2, User[].class).getBody())
                .stream()
                .flatMap(Arrays::stream)
                .toList();

        Assertions.assertEquals(1, users2.size());
    }

    @Test
    public void testFindUserById() {
        var user1 = new User();
        user1.setId(generateId());
        user1.setName("Alejandra");
        user1.setEmail("alejandra@email.com");

        var user2 = new User();
        user2.setId(generateId());
        user2.setName("Alejandro");
        user2.setEmail("alejandro@email.com");

        var user3 = new User();
        user3.setId(generateId());
        user3.setName("Alex");
        user3.setEmail("alex@email.com");

        userDao.saveAll(List.of(user1, user2, user3));

        var url = String.format("/users/%s", user1.getId());
        var user = testRestTemplate.getForObject(url, User.class);

        Assertions.assertEquals(user1.getId(), user.getId());
    }

    @Test
    public void testFindUserByIdIfUserIsNotFound() {
        var user1 = new User();
        user1.setId(generateId());
        user1.setName("Alejandra");
        user1.setEmail("alejandra@email.com");

        var user2 = new User();
        user2.setId(generateId());
        user2.setName("Alejandro");
        user2.setEmail("alejandro@email.com");

        var user3 = new User();
        user3.setId(generateId());
        user3.setName("Alex");
        user3.setEmail("alex@email.com");

        userDao.saveAll(List.of(user1, user2, user3));

        var userId = "4000000";

        var url = String.format("/users/%s", userId);
        var response = testRestTemplate.getForObject(url, BookingResponse.class);

        Assertions.assertEquals(String.format("User with id: %s not found", userId), response.message());
    }

    @Test
    public void testFindUserByEmail() {
        var user1 = new User();
        user1.setId(generateId());
        user1.setName("Alejandra");
        user1.setEmail("alejandra@email.com");

        var user2 = new User();
        user2.setId(generateId());
        user2.setName("Alejandro");
        user2.setEmail("alejandro@email.com");

        var user3 = new User();
        user3.setId(generateId());
        user3.setName("Alex");
        user3.setEmail("alex@email.com");

        userDao.saveAll(List.of(user1, user2, user3));

        var url = String.format("/users/email/%s", user1.getEmail());
        var user = testRestTemplate.getForObject(url, User.class);

        Assertions.assertEquals(user1.getName(), user.getName());
    }

    @Test
    public void testCreateNewUserAndDeleteIt() {
        var user = new User();
        user.setName("Joe");
        user.setEmail("joe@email.com");

        var response = testRestTemplate.postForEntity("/users/new", user, User.class);

        var maybeUser = userDao.findById(response.getBody().getId());

        Assertions.assertTrue(maybeUser.isPresent());

        var createdUser = maybeUser.get();

        Assertions.assertEquals(user.getName(), createdUser.getName());
        Assertions.assertEquals(user.getEmail(), createdUser.getEmail());

        var updatedEmail = "joe2@email.com";

        user.setEmail(updatedEmail);

        var url = String.format("/users/%s", user.getId());

        testRestTemplate.delete(url);

        var maybeUpdatedUser = userDao.findById(user.getId())
                .stream()
                .findFirst();

        Assertions.assertFalse(maybeUpdatedUser.isEmpty());
    }

    @Test
    public void testCreateNewEventAndUpdateTheirTitle() {
        var event = new Event();
        event.setId(generateId());
        event.setTitle("Romulus");
        event.setDate(convertToDate(LocalDate.now()));
        event.setTicketPrice(20);

        var response = testRestTemplate.postForEntity("/events/new", event, Event.class);

        var maybeEvent = eventDao.findById(response.getBody().getId());

        Assertions.assertTrue(maybeEvent.isPresent());

        var createdEvent = maybeEvent.get();

        Assertions.assertEquals(event.getTitle(), createdEvent.getTitle());
        Assertions.assertEquals(convertToLocalDate(event.getDate()), convertToLocalDate(createdEvent.getDate()));
        Assertions.assertEquals(event.getTicketPrice(), createdEvent.getTicketPrice());

        var updatedTitle = "Romulus II";

        event.setTitle(updatedTitle);

        var url = String.format("/events/%s", event.getId());
        var request = new HttpEntity<>(event);

        testRestTemplate.exchange(url, HttpMethod.PUT, request, Event.class);

        var maybeUpdatedEvent = eventDao.findById(event.getId()).stream().findFirst();

        Assertions.assertTrue(maybeUpdatedEvent.isPresent());

        var updatedEvent = maybeUpdatedEvent.get();

        Assertions.assertEquals(event.getTitle(), updatedEvent.getTitle());
        Assertions.assertEquals(convertToLocalDate(event.getDate()), convertToLocalDate(updatedEvent.getDate()));
        Assertions.assertEquals(event.getTicketPrice(), updatedEvent.getTicketPrice());
    }

    @Test
    public void testListEventsByTitle() {
        var event1 = new Event();
        event1.setId(generateId());
        event1.setTitle("Alien I");
        event1.setDate(convertToDate(LocalDate.now()));
        event1.setTicketPrice(20);

        var event2 = new Event();
        event2.setId(generateId());
        event2.setTitle("Alien II");
        event2.setDate(convertToDate(LocalDate.now().plusDays(20)));
        event2.setTicketPrice(15);

        var event3 = new Event();
        event3.setId(generateId());
        event3.setTitle("Alien vs Predator");
        event3.setDate(convertToDate(LocalDate.now().plusDays(20)));
        event3.setTicketPrice(15);

        eventDao.saveAll(List.of(event1, event2, event3));

        var url1 = String.format("/events?title=%s&pageSize=%s&pageNum=%s", "Alien", "2", "0");
        var events1 = Optional.ofNullable(testRestTemplate.getForEntity(url1, Event[].class).getBody())
                .stream()
                .flatMap(Arrays::stream)
                .toList();

        Assertions.assertEquals(2, events1.size());

        var url2 = String.format("/events?title=%s&pageSize=%s&pageNum=%s", "Alien", "2", "1");
        var events2 = Optional.ofNullable(testRestTemplate.getForEntity(url2, Event[].class).getBody())
                .stream()
                .flatMap(Arrays::stream)
                .toList();

        Assertions.assertEquals(1, events2.size());
    }

    @Test
    public void testListEventsByDate() {
        var now = LocalDate.now();

        var event1 = new Event();
        event1.setId(generateId());
        event1.setTitle("World war I");
        event1.setDate(convertToDate(now.plusDays(1)));
        event1.setTicketPrice(20);

        var event2 = new Event();
        event2.setId(generateId());
        event2.setTitle("Star wars I");
        event2.setDate(convertToDate(now.plusDays(1)));
        event2.setTicketPrice(20);

        var event3 = new Event();
        event3.setId(generateId());
        event3.setTitle("Star wars II");
        event3.setDate(convertToDate(now.plusDays(1)));
        event3.setTicketPrice(20);

        var event4 = new Event();
        event4.setId(generateId());
        event4.setTitle("Star wars III");
        event4.setDate(convertToDate(now.plusDays(2)));
        event4.setTicketPrice(20);

        List.of(event1, event2, event3, event4)
                .forEach(event -> testRestTemplate.postForEntity("/events/new", event, Event.class));

        var url1 = String.format("/events?date=%s&pageSize=%s&pageNum=%s", now.plusDays(1), "2", "0");
        var events1 = Optional.ofNullable(testRestTemplate.getForEntity(url1, Event[].class).getBody())
                .stream()
                .flatMap(Arrays::stream)
                .toList();

        Assertions.assertEquals(2, events1.size());

        var url2 = String.format("/events?date=%s&pageSize=%s&pageNum=%s", now.plusDays(1), "2", "1");
        var events2 = Optional.ofNullable(testRestTemplate.getForEntity(url2, Event[].class).getBody())
                .stream()
                .flatMap(Arrays::stream)
                .toList();

        Assertions.assertEquals(1, events2.size());
    }

    @Test
    public void testFindEventById() {
        var event1 = new Event();
        event1.setId(generateId());
        event1.setTitle("Romulus");
        event1.setDate(convertToDate(LocalDate.now()));
        event1.setTicketPrice(20);

        var event2 = new Event();
        event2.setId(generateId());
        event2.setTitle("Star wars");
        event2.setDate(convertToDate(LocalDate.now().plusDays(20)));
        event2.setTicketPrice(15);

        var event3 = new Event();
        event3.setId(generateId());
        event3.setTitle("Star wars II");
        event3.setDate(convertToDate(LocalDate.now().plusDays(20)));
        event3.setTicketPrice(15);

        eventDao.saveAll(List.of(event1, event2, event3));

        var url = String.format("/events/%s", event1.getId());
        var user = testRestTemplate.getForObject(url, User.class);

        Assertions.assertEquals(event1.getId(), user.getId());
    }

    @Test
    public void testFindEventByIdIfEventIsNotFound() {
        var event1 = new Event();
        event1.setId(generateId());
        event1.setTitle("Romulus");
        event1.setDate(convertToDate(LocalDate.now()));
        event1.setTicketPrice(20);

        var event2 = new Event();
        event2.setId(generateId());
        event2.setTitle("Star wars");
        event2.setDate(convertToDate(LocalDate.now().plusDays(20)));
        event2.setTicketPrice(15);

        var event3 = new Event();
        event3.setId(generateId());
        event3.setTitle("Star wars II");
        event3.setDate(convertToDate(LocalDate.now().plusDays(20)));
        event3.setTicketPrice(15);

        eventDao.saveAll(List.of(event1, event2, event3));

        var eventId = "4000000";

        var url = String.format("/events/%s", eventId);
        var response = testRestTemplate.getForObject(url, BookingResponse.class);

        Assertions.assertEquals(String.format("Event with id %s not found", eventId), response.message());
    }

    @Test
    public void testCreateNewEventAndDeleteIt() {
        var event = new Event();
        event.setId(generateId());
        event.setTitle("Romulus");
        event.setDate(convertToDate(LocalDate.now()));
        event.setTicketPrice(20);

        var response = testRestTemplate.postForEntity("/events/new", event, Event.class);

        var maybeEvent = eventDao.findById(response.getBody().getId());

        Assertions.assertTrue(maybeEvent.isPresent());

        var createdEvent = maybeEvent.get();

        Assertions.assertEquals(event.getTitle(), createdEvent.getTitle());
        Assertions.assertEquals(convertToLocalDate(event.getDate()), convertToLocalDate(createdEvent.getDate()));
        Assertions.assertEquals(event.getTicketPrice(), createdEvent.getTicketPrice());

        var url = String.format("/events/%s", event.getId());

        testRestTemplate.delete(url);

        var maybeUpdatedUser = userDao.findById(event.getId()).stream().findFirst();

        Assertions.assertTrue(maybeUpdatedUser.isEmpty());
    }

    @Test
    public void testCreateNewUserAccount() {
        var user = new User();
        user.setName("Joe");
        user.setEmail("joe@email.com");

        var userResponse = testRestTemplate.postForEntity("/users/new", user, User.class);

        var createdUser = userResponse.getBody();

        var account = new UserAccount();
        account.setUserId(createdUser.getId());
        account.setAmount(50);

        var accountResponse = testRestTemplate.postForEntity("/users/account/new", account, UserAccount.class);

        var maybeCreatedAccount = accountDao.findById(accountResponse.getBody().getId());

        Assertions.assertTrue(maybeCreatedAccount.isPresent());

        var createdAccount = maybeCreatedAccount.get();

        Assertions.assertEquals(account.getUserId(), createdAccount.getUserId());
        Assertions.assertEquals(account.getAmount(), createdAccount.getAmount());
    }

    @Test
    public void testCreateNewUserAccountAndRefillIt() {
        var user = new User();
        user.setName("Joe");
        user.setEmail("joe@email.com");

        var userResponse = testRestTemplate.postForEntity("/users/new", user, User.class);

        var createdUser = userResponse.getBody();

        var account = new UserAccount();
        account.setUserId(createdUser.getId());
        account.setAmount(50);

        var accountResponse = testRestTemplate.postForEntity("/users/account/new", account, UserAccount.class);

        var maybeCreatedAccount = accountDao.findById(accountResponse.getBody().getId());

        Assertions.assertTrue(maybeCreatedAccount.isPresent());

        var createdAccount = maybeCreatedAccount.get();

        Assertions.assertEquals(account.getUserId(), createdAccount.getUserId());
        Assertions.assertEquals(account.getAmount(), createdAccount.getAmount());

        var url = String.format("/users/account/%s", createdAccount.getId());

        account.setAmount(20);

        var request = new HttpEntity<>(account);

        var response = testRestTemplate.exchange(url, HttpMethod.PUT, request, BookingResponse.class);

        Assertions.assertEquals("Account was refilled successfully", response.getBody().message());

        var maybeRefilledAccount = accountDao.findById(createdAccount.getId());

        Assertions.assertTrue(maybeRefilledAccount.isPresent());

        var refilledAccount = maybeRefilledAccount.get();

        Assertions.assertEquals(70, refilledAccount.getAmount());
    }

    @Test
    public void testCreateNewUserAccountAndDeleteIt() {
        var user = new User();
        user.setName("Joe");
        user.setEmail("joe@email.com");

        var userResponse = testRestTemplate.postForEntity("/users/new", user, User.class);

        var createdUser = userResponse.getBody();

        var account = new UserAccount();
        account.setUserId(createdUser.getId());
        account.setAmount(50);

        var accountResponse = testRestTemplate.postForEntity("/users/account/new", account, UserAccount.class);
        var maybeCreatedAccount = accountDao.findById(accountResponse.getBody().getId());

        Assertions.assertTrue(maybeCreatedAccount.isPresent());

        var createdAccount = maybeCreatedAccount.get();

        Assertions.assertEquals(account.getUserId(), createdAccount.getUserId());
        Assertions.assertEquals(account.getAmount(), createdAccount.getAmount());

        var url = String.format("/users/account/%s", createdAccount.getId());

        testRestTemplate.delete(url);

        var deletedAccount = accountDao.findById(createdAccount.getId());

        Assertions.assertTrue(deletedAccount.isEmpty());
    }

    @Test
    public void testBookTicket() {
        var user = new User();
        user.setName("Joe");
        user.setEmail("joe@email.com");

        var event = new Event();
        event.setTitle("Romulus");
        event.setDate(convertToDate(LocalDate.now()));
        event.setTicketPrice(20);

        var userResponse = testRestTemplate.postForEntity("/users/new", user, User.class);

        var createdUser = userResponse.getBody();

        var account = new UserAccount();
        account.setUserId(createdUser.getId());
        account.setAmount(50);

        var accountResponse = testRestTemplate.postForEntity("/users/account/new", account, UserAccount.class);
        var maybeCreatedAccount = accountDao.findById(accountResponse.getBody().getId());

        Assertions.assertTrue(maybeCreatedAccount.isPresent());

        var eventResponse = testRestTemplate.postForEntity("/events/new", event, Event.class);

        var createdEvent = eventResponse.getBody();

        var ticket = new Ticket();
        ticket.setUserId(createdUser.getId());
        ticket.setEventId(createdEvent.getId());
        ticket.setCategory(Ticket.Category.STANDARD);
        ticket.setPlace(10);

        var ticketResponse = testRestTemplate.postForEntity("/tickets/new", ticket, Ticket.class);

        var bookedTicketResponse = ticketResponse.getBody();

        var maybeBookedTicket = ticketDao.findById(bookedTicketResponse.getId());

        Assertions.assertTrue(maybeBookedTicket.isPresent());

        var bookedTicket = maybeBookedTicket.get();

        Assertions.assertEquals(ticket.getUserId(), bookedTicket.getUserId());
        Assertions.assertEquals(ticket.getEventId(), bookedTicket.getEventId());
        Assertions.assertEquals(ticket.getCategory(), bookedTicket.getCategory());
        Assertions.assertEquals(ticket.getPlace(), bookedTicket.getPlace());
    }

    @Test
    public void testBookTicketIfAccountDoesNotHaveSufficientBalance() {
        var user = new User();
        user.setName("Joe");
        user.setEmail("joe@email.com");

        var event = new Event();
        event.setTitle("Romulus");
        event.setDate(convertToDate(LocalDate.now()));
        event.setTicketPrice(30);

        var userResponse = testRestTemplate.postForEntity("/users/new", user, User.class);

        var createdUser = userResponse.getBody();

        var account = new UserAccount();
        account.setUserId(createdUser.getId());
        account.setAmount(20);

        var accountResponse = testRestTemplate.postForEntity("/users/account/new", account, UserAccount.class);
        var maybeCreatedAccount = accountDao.findById(accountResponse.getBody().getId());

        Assertions.assertTrue(maybeCreatedAccount.isPresent());

        var eventResponse = testRestTemplate.postForEntity("/events/new", event, Event.class);

        var createdEvent = eventResponse.getBody();

        var ticket = new Ticket();
        ticket.setUserId(createdUser.getId());
        ticket.setEventId(createdEvent.getId());
        ticket.setCategory(Ticket.Category.STANDARD);
        ticket.setPlace(10);

        var ticketResponse = testRestTemplate.postForEntity("/tickets/new", ticket, Ticket.class);

        var bookedTicketResponse = ticketResponse.getBody();

        var maybeBookedTicket = ticketDao.findById(bookedTicketResponse.getId());

        Assertions.assertTrue(maybeBookedTicket.isEmpty());
    }

    @Test
    public void testBookTicketIfThePlaceHadAlreadyBeenBooked() {
        var user = new User();
        user.setName("Joe");
        user.setEmail("joe@email.com");

        var event = new Event();
        event.setTitle("Romulus");
        event.setDate(convertToDate(LocalDate.now()));
        event.setTicketPrice(20);

        var userResponse = testRestTemplate.postForEntity("/users/new", user, User.class);

        var createdUser = userResponse.getBody();

        var account = new UserAccount();
        account.setUserId(createdUser.getId());
        account.setAmount(50);

        var accountResponse = testRestTemplate.postForEntity("/users/account/new", account, UserAccount.class);
        var maybeCreatedAccount = accountDao.findById(accountResponse.getBody().getId());

        Assertions.assertTrue(maybeCreatedAccount.isPresent());

        var eventResponse = testRestTemplate.postForEntity("/events/new", event, Event.class);

        var createdEvent = eventResponse.getBody();

        var ticket = new Ticket();
        ticket.setUserId(createdUser.getId());
        ticket.setEventId(createdEvent.getId());
        ticket.setCategory(Ticket.Category.STANDARD);
        ticket.setPlace(10);

        var ticketResponse1 = testRestTemplate.postForEntity("/tickets/new", ticket, Ticket.class);

        var bookedTicketResponse1 = ticketResponse1.getBody();

        var maybeBookedTicket = ticketDao.findById(bookedTicketResponse1.getId());

        Assertions.assertTrue(maybeBookedTicket.isPresent());

        var bookedTicket = maybeBookedTicket.get();

        Assertions.assertEquals(ticket.getUserId(), bookedTicket.getUserId());
        Assertions.assertEquals(ticket.getEventId(), bookedTicket.getEventId());
        Assertions.assertEquals(ticket.getCategory(), bookedTicket.getCategory());
        Assertions.assertEquals(ticket.getPlace(), bookedTicket.getPlace());

        var ticketResponse2 = testRestTemplate.postForEntity("/tickets/new", ticket, BookingResponse.class);

        var bookedTicketResponse2 = ticketResponse2.getBody();

        Assertions.assertEquals("This place had already been booked", bookedTicketResponse2.message());
    }

    public void testBookTicketUsingJms() throws InterruptedException {
        var user = new User();
        user.setName("Joe");
        user.setEmail("joe@email.com");

        var event = new Event();
        event.setTitle("Romulus");
        event.setDate(convertToDate(LocalDate.now()));
        event.setTicketPrice(20);

        var userResponse = testRestTemplate.postForEntity("/users/new", user, User.class);

        var createdUser = userResponse.getBody();

        var account = new UserAccount();
        account.setUserId(createdUser.getId());
        account.setAmount(50);

        var accountResponse = testRestTemplate.postForEntity("/users/account/new", account, UserAccount.class);
        var maybeCreatedAccount = accountDao.findById(accountResponse.getBody().getId());

        Assertions.assertTrue(maybeCreatedAccount.isPresent());

        var eventResponse = testRestTemplate.postForEntity("/events/new", event, Event.class);

        var createdEvent = eventResponse.getBody();

        var ticket = new Ticket();
        ticket.setUserId(createdUser.getId());
        ticket.setEventId(createdEvent.getId());
        ticket.setCategory(Ticket.Category.STANDARD);
        ticket.setPlace(10);

        var ticketResponse = testRestTemplate.postForEntity("/tickets/new/queue", ticket, BookingResponse.class);

        var bookingResponse = ticketResponse.getBody();

        Assertions.assertEquals("Ticket is being booked asynchronously", bookingResponse.message());

        Thread.sleep(300);

        var maybeBookedTicket = ticketDao.findAll().stream()
                .filter(t -> t.getUserId() == createdUser.getId())
                .findFirst();

        Assertions.assertTrue(maybeBookedTicket.isPresent());

        var bookedTicket = maybeBookedTicket.get();

        Assertions.assertEquals(ticket.getUserId(), bookedTicket.getUserId());
        Assertions.assertEquals(ticket.getEventId(), bookedTicket.getEventId());
        Assertions.assertEquals(ticket.getCategory(), bookedTicket.getCategory());
        Assertions.assertEquals(ticket.getPlace(), bookedTicket.getPlace());
    }

    @Test
    public void testFindBookedTicketByUserId() {
        var user = new User();
        user.setName("Joe");
        user.setEmail("joe@email.com");

        var event = new Event();
        event.setTitle("Romulus");
        event.setDate(convertToDate(LocalDate.now()));
        event.setTicketPrice(20);

        var userResponse = testRestTemplate.postForEntity("/users/new", user, User.class);

        var createdUser = userResponse.getBody();

        var account = new UserAccount();
        account.setUserId(createdUser.getId());
        account.setAmount(50);

        var accountResponse = testRestTemplate.postForEntity("/users/account/new", account, UserAccount.class);
        var maybeCreatedAccount = accountDao.findById(accountResponse.getBody().getId());

        Assertions.assertTrue(maybeCreatedAccount.isPresent());

        var eventResponse = testRestTemplate.postForEntity("/events/new", event, Event.class);

        var createdEvent = eventResponse.getBody();

        var ticket = new Ticket();
        ticket.setUserId(createdUser.getId());
        ticket.setEventId(createdEvent.getId());
        ticket.setCategory(Ticket.Category.STANDARD);
        ticket.setPlace(10);

        var ticketResponse = testRestTemplate.postForEntity("/tickets/new", ticket, Ticket.class);

        var bookedTicketResponse = ticketResponse.getBody();

        var maybeBookedTicket = ticketDao.findById(bookedTicketResponse.getId());

        Assertions.assertTrue(maybeBookedTicket.isPresent());

        var bookedTicket = maybeBookedTicket.get();

        var url = String.format("/tickets?userId=%s&pageSize=%s&pageNum=%s", bookedTicket.getUserId(), 1, 0);

        var ticketsResponse = Optional.ofNullable(testRestTemplate.getForEntity(url, Ticket[].class).getBody())
                        .stream()
                        .flatMap(Arrays::stream)
                        .toList();

        Assertions.assertEquals(1, ticketsResponse.size());
    }

    @Test
    public void testFindBookedTicketByEventId() {
        var user = new User();
        user.setName("Joe");
        user.setEmail("joe@email.com");

        var event = new Event();
        event.setTitle("Romulus");
        event.setDate(convertToDate(LocalDate.now()));
        event.setTicketPrice(20);

        var userResponse = testRestTemplate.postForEntity("/users/new", user, User.class);

        var createdUser = userResponse.getBody();

        var account = new UserAccount();
        account.setUserId(createdUser.getId());
        account.setAmount(50);

        var accountResponse = testRestTemplate.postForEntity("/users/account/new", account, UserAccount.class);
        var maybeCreatedAccount = accountDao.findById(accountResponse.getBody().getId());

        Assertions.assertTrue(maybeCreatedAccount.isPresent());

        var eventResponse = testRestTemplate.postForEntity("/events/new", event, Event.class);

        var createdEvent = eventResponse.getBody();

        var ticket = new Ticket();
        ticket.setUserId(createdUser.getId());
        ticket.setEventId(createdEvent.getId());
        ticket.setCategory(Ticket.Category.STANDARD);
        ticket.setPlace(10);

        var ticketResponse = testRestTemplate.postForEntity("/tickets/new", ticket, Ticket.class);

        var bookedTicketResponse = ticketResponse.getBody();

        var maybeBookedTicket = ticketDao.findById(bookedTicketResponse.getId());

        Assertions.assertTrue(maybeBookedTicket.isPresent());

        var bookedTicket = maybeBookedTicket.get();

        var url = String.format("/tickets?eventId=%s&pageSize=%s&pageNum=%s", bookedTicket.getEventId(), 1, 0);

        var ticketsResponse = Optional.ofNullable(testRestTemplate.getForEntity(url, Ticket[].class).getBody())
                .stream()
                .flatMap(Arrays::stream)
                .toList();

        Assertions.assertEquals(1, ticketsResponse.size());
    }

    @Test
    public void testBookTicketAndCancelIt() {
        var user = new User();
        user.setName("Joe");
        user.setEmail("joe@email.com");

        var event = new Event();
        event.setTitle("Romulus");
        event.setDate(convertToDate(LocalDate.now()));
        event.setTicketPrice(20);

        var userResponse = testRestTemplate.postForEntity("/users/new", user, User.class);

        var createdUser = userResponse.getBody();

        var account = new UserAccount();
        account.setUserId(createdUser.getId());
        account.setAmount(50);

        var accountResponse = testRestTemplate.postForEntity("/users/account/new", account, UserAccount.class);
        var maybeCreatedAccount = accountDao.findById(accountResponse.getBody().getId());

        Assertions.assertTrue(maybeCreatedAccount.isPresent());

        var eventResponse = testRestTemplate.postForEntity("/events/new", event, Event.class);

        var createdEvent = eventResponse.getBody();

        var ticket = new Ticket();
        ticket.setUserId(createdUser.getId());
        ticket.setEventId(createdEvent.getId());
        ticket.setCategory(Ticket.Category.STANDARD);
        ticket.setPlace(10);

        var ticketResponse = testRestTemplate.postForEntity("/tickets/new", ticket, Ticket.class);

        var bookedTicketResponse = ticketResponse.getBody();

        var maybeBookedTicket = ticketDao.findById(bookedTicketResponse.getId());

        Assertions.assertTrue(maybeBookedTicket.isPresent());

        var url = String.format("/tickets/%s", maybeBookedTicket.get().getId());

        testRestTemplate.delete(url);

        var maybeDeletedTicket = ticketDao.findById(bookedTicketResponse.getId());

        Assertions.assertTrue(maybeDeletedTicket.isEmpty());
    }

    private void addAuthorizationHeader() {
        var userInfo = new UserInfo();
        userInfo.setId(Long.valueOf(generateId()).intValue());
        userInfo.setLogin("maria");
        userInfo.setEmail("maria@email.com");

        var token = JwtUtil.createToken(userInfo);

        testRestTemplate.getRestTemplate().setInterceptors(
                Collections.singletonList((request, body, execution) -> {
                    request.getHeaders()
                            .add("Authorization", String.format("Bearer %s", token));
                    return execution.execute(request, body);
                }));
    }

}

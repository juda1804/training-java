package org.booking.restcontroller;

import org.booking.facade.BookingFacade;
import org.booking.model.*;
import org.booking.restcontroller.dto.BookingResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.function.BiFunction;

import static org.booking.util.IdentifierGenerator.generateId;

@RestController
public class BookingRestController {
    private final Logger LOGGER =LoggerFactory.getLogger(BookingRestController.class);


    private final BookingFacade bookingFacade;


    private final JmsTemplate jmsTemplate;

    @Value("${jms.message.destination}")
    private String bookingMessage;

    public BookingRestController(BookingFacade bookingFacade, JmsTemplate jmsTemplate) {
        this.bookingFacade = bookingFacade;
        this.jmsTemplate = jmsTemplate;
    }

    /**
     * Creates new user. User id is auto-generated.
     * @param user User data without user id.
     * @return Created User object.
     */
    @PostMapping("/users/new")
    public ResponseEntity<User> createNewUser(@RequestBody User user) {
        LOGGER.debug("Creating new user with name: {}, email: {}", user.getName(), user.getEmail());

        user.setId(generateId());

        var createdUser = bookingFacade.createUser(user);

        URI uri = ServletUriComponentsBuilder.fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(createdUser.getId())
                .toUri();

        return ResponseEntity.created(uri)
                .body(createdUser);
    }

    /**
     * Updates user using given data.
     * @param userId user id of the created user
     * @param user User data for update.
     * @return Updated User object.
     */
    @PutMapping("/users/{id}")
    public ResponseEntity<User> updateUser(
            @PathVariable("id") long userId,
            @RequestBody User user) {
        LOGGER.info("Updating existing user with id: {}, name: {}, email: {}",
                userId, user.getName(), user.getEmail());

        user.setId(userId);

        var createdUser = bookingFacade.updateUser(user);

        return ResponseEntity.ok(createdUser);
    }

    /**
     * Gets user by its id.
     * @param userId user id of the created user
     * @return User.
     */
    @GetMapping("/users/{id}")
    public ResponseEntity<User> findUserById(@PathVariable("id") long userId) {
        LOGGER.info("Searching existing user with id: {}", userId);

        var user = bookingFacade.getUserById(userId);

        return ResponseEntity.ok(user);
    }

    /**
     * Gets user by its email. Email is strictly matched.
     * @param email email of the created user
     * @return User.
     */
    @GetMapping("/users/email/{email}")
    public ResponseEntity<User> findUserByEmail(@PathVariable("email") String email) {
        LOGGER.info("Searching existing user with email: {}", email);

        var user = bookingFacade.getUserByEmail(email);

        return ResponseEntity.ok(user);
    }

    /**
     * Get list of users by matching name. Name is matched using 'contains' approach.
     * In case nothing was found, empty list is returned.
     * @param name Users name or it's part.
     * @param pageSize Pagination param. Number of users to return on a page.
     * @param pageNum Pagination param. Number of the page to return. Starts from 0.
     * @return List of users.
     */
    @GetMapping("/users")
    public ResponseEntity<List<User>> findUserByNameLike(
            @RequestParam("name") String name,
            @RequestParam(value = "pageSize", defaultValue = "20") Integer pageSize,
            @RequestParam(value = "pageNum", defaultValue = "0") Integer pageNum) {
        LOGGER.info("Searching existing user with name: {} in page: {} with page size: {}",
                name, pageSize, pageNum);

        var users = bookingFacade.getUsersByName(name, pageSize, pageNum);

        return ResponseEntity.ok(users);
    }

    /**
     * Deletes user by its id.
     * @param userId User id.
     * @return Message to indicate if user was deleted successfully
     */
    @DeleteMapping("/users/{id}")
    public ResponseEntity<BookingResponse> deleteUser(@PathVariable("id") long userId) {
        LOGGER.info("Deleting existing user with id: {}", userId);

        var wasDeleted = bookingFacade.deleteUser(userId);

        var response =  wasDeleted
                ? new BookingResponse("User was deleted successfully")
                : new BookingResponse("User cannot be deleted");

        return ResponseEntity.ok(response);
    }

    /**
     * Creates new event. Event id is auto-generated.
     * @param event Event data without the event id.
     * @return Created Event object.
     */
    @PostMapping("/events/new")
    public ResponseEntity<Event> createNewEvent(@RequestBody Event event) {
        LOGGER.info("Creating new event with title: {}, date: {}, price: {}",
                event.getTitle(), event.getDate(), event.getTicketPrice());

        event.setId(generateId());

        var createdEvent = bookingFacade.createEvent(event);

        URI uri = ServletUriComponentsBuilder.fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(createdEvent.getId())
                .toUri();

        return ResponseEntity.created(uri)
                .body(createdEvent);
    }

    /**
     * Updates event using given data.
     * @param eventId event id of the created event
     * @param event Event data for update
     * @return Updated Event object.
     */
    @PutMapping("/events/{id}")
    public ResponseEntity<Event> updateEvent(
            @PathVariable("id") long eventId,
            @RequestBody Event event) {
        LOGGER.info("Updating existing event with id: {}, title: {}, date: {}, price: {}",
                eventId, event.getTitle(), event.getDate(), event.getTicketPrice());

        event.setId(eventId);

        var createdEvent = bookingFacade.updateEvent(event);

        return ResponseEntity.ok(createdEvent);
    }

    /**
     * Gets event by its id.
     * @param eventId event id of the created event
     * @return Event.
     */
    @GetMapping("/events/{id}")
    public ResponseEntity<Event> findEventById(@PathVariable("id") long eventId) {
        LOGGER.info("Searching existing event with id: {}", eventId);

        var user = bookingFacade.getEventById(eventId);

        return ResponseEntity.ok(user);
    }

    /**
     * Get list of events by matching title or for specified date.
     * Title is matched using 'contains' approach.
     * Date should match with the date of the event.
     * In case nothing was found, empty list is returned.
     * In case neither title nor date has not been provided, a BookingResponse will be returned
     * to indicate that a valid param must be provided
     * @param title Event title or it's part.
     * @param pageSize Pagination param. Number of events to return on a page.
     * @param pageNum Pagination param. Number of the page to return. Starts from 0.
     * @return List of events.
     */
    @GetMapping("/events")
    public ResponseEntity<List<Event>> findEvents(
            @RequestParam(value = "title", required = false) String title,
            @RequestParam(value = "date", required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) Date date,
            @RequestParam(value = "pageSize", defaultValue = "20") Integer pageSize,
            @RequestParam(value = "pageNum", defaultValue = "0") Integer pageNum) {

        LOGGER.info("title: {}, date: {}, pageSize: {}, pageNum: {}", title, date, pageSize, pageNum);

        var response = Optional.ofNullable(title).map(this::findEventsByTitle)
                .or(() -> Optional.ofNullable(date).map(this::findEventsByDate))
                .map(filterEvents -> filterEvents.apply(pageSize, pageNum))
                .orElseThrow(() -> new IllegalArgumentException("Provide a valid param to perform the search"));

        return ResponseEntity.ok(response);
    }

    /**
     * Deletes event by its id.
     * @param eventId Event id.
     * @return Flag that shows whether event has been deleted.
     */
    @DeleteMapping("/events/{id}")
    public ResponseEntity<BookingResponse> deleteEvent(@PathVariable("id") long eventId) {
        LOGGER.info("Deleting existing event with id: {}", eventId);

        var wasDeleted = bookingFacade.deleteEvent(eventId);

        var response = wasDeleted
                ? new BookingResponse("Event was deleted successfully")
                : new BookingResponse("Event cannot be deleted");

        return ResponseEntity.ok(response);
    }

    /**
     * Create a new user account for a existing user.
     * @param account User account.
     * @return Created user account
     */
    @PostMapping("/users/account/new")
    public ResponseEntity<UserAccount> createNewUserAccount(@RequestBody UserAccount account) {
        LOGGER.debug("Creating new user account for user with id {}, amount: {}",
                account.getUserId(), account.getAmount());

        account.setId(generateId());

        var createdAccount = bookingFacade.createUserAccount(account);

        URI uri = ServletUriComponentsBuilder.fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(createdAccount.getId())
                .toUri();

        return ResponseEntity.created(uri)
                .body(createdAccount);
    }

    /**
     * Refill existing user account for a existing user.
     * @param account user account id.
     * @param id account id of the created user account.
     * @return Message that indicates if the account has been refilled
     */
    @PutMapping("/users/account/{id}")
    public ResponseEntity<BookingResponse> refillUserAccount(
            @RequestBody UserAccount account,
            @PathVariable("id") Long id) {
        LOGGER.info("Refill user account with id: {} and amount: {}",
                id, account.getAmount());

        var wasRefill = bookingFacade.refillUserAccount(id, account.getAmount());

        var response =  wasRefill
                ? new BookingResponse("Account was refilled successfully")
                : new BookingResponse("Account could not be refilled");

        return ResponseEntity.ok(response);
    }

    /**
     * Delete existing user account for a existing user.
     * @param accountId user account id.
     * @return Message that indicates if the account has been refilled
     */
    @DeleteMapping("/users/account/{id}")
    public ResponseEntity<BookingResponse> deleteUserAccount(@PathVariable("id") long accountId) {
        LOGGER.info("Deleting existing user account with id: {}", accountId);

        var wasDeleted = bookingFacade.deleteAccount(accountId);

        var response = wasDeleted
                ? new BookingResponse("User account was deleted successfully")
                : new BookingResponse("User account cannot be deleted");

        return ResponseEntity.ok(response);
    }

    /**
     * Book ticket for a specified event on behalf of specified user.
     * @param ticket Ticket data without the ticket id.
     * @return Booked ticket object.
     */
    @PostMapping("/tickets/new")
    public ResponseEntity<Ticket> bookTicket(@RequestBody Ticket ticket) {
        LOGGER.info("Booking ticket for event with id: {} and user id: {} in place: {} with category: {}",
                ticket.getEventId(), ticket.getUserId(), ticket.getPlace(), ticket.getCategory());

        var bookedTicket = bookingFacade.bookTicket(
                ticket.getUserId(),
                ticket.getEventId(),
                ticket.getPlace(),
                ticket.getCategory());

        URI uri = ServletUriComponentsBuilder.fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(bookedTicket.getId())
                .toUri();

        return ResponseEntity.created(uri)
                .body(bookedTicket);
    }

    /**
     * Book ticket for a specified event on behalf of specified user using JMS.
     * @param ticket Ticket data without the ticket id.
     * @return Booked ticket object.
     */
    @PostMapping("/tickets/new/queue")
    public ResponseEntity<BookingResponse> bookTicketQueue(@RequestBody Ticket ticket) {
        LOGGER.info("[JMS] Booking ticket for event with id: {} and user id: {} in place: {} with category: {}",
                ticket.getEventId(), ticket.getUserId(), ticket.getPlace(), ticket.getCategory());

        jmsTemplate.convertAndSend(bookingMessage, ticket);

        return ResponseEntity.accepted()
                .body(new BookingResponse("Ticket is being booked asynchronously"));
    }

    /**
     * Get all booked tickets for specified user or event.
     * Tickets should be sorted by event date in descending order if retrieved using the user id.
     * Tickets should be sorted in by user email in ascending order if retrieved using the event id.
     * @param userId User id
     * @param eventId event id
     * @param pageSize Pagination param. Number of tickets to return on a page.
     * @param pageNum Pagination param. Number of the page to return. Starts from 1.
     * @return List of Ticket objects.
     */
    @GetMapping("/tickets")
    public ResponseEntity<List<Ticket>> findBookedTickets(
            @RequestParam(value = "userId", required = false) Long userId,
            @RequestParam(value = "eventId", required = false) Long eventId,
            @RequestParam(value = "pageSize", defaultValue = "20") Integer pageSize,
            @RequestParam(value = "pageNum", defaultValue = "0") Integer pageNum) {

        var response = Optional.ofNullable(userId).filter(id -> id > 0).map(this::findTicketsByUserId)
                .or(() -> Optional.ofNullable(eventId).filter(id -> id > 0).map(this::findTicketsByEventId))
                .map(filteredTickets -> filteredTickets.apply(pageSize, pageNum))
                .orElseThrow(() -> new IllegalArgumentException("Provide a valid param to perform the search"));

        return ResponseEntity.ok(response);
    }

    /**
     * Cancel ticket with a specified id.
     * @param ticketId Ticket id.
     * @return Message to indicate if ticket has been canceled.
     */
    @DeleteMapping("/tickets/{id}")
    public ResponseEntity<BookingResponse> cancelTicket(@PathVariable("id") long ticketId) {
        LOGGER.info("Canceling existing ticket with id: {}", ticketId);

        var wasDeleted = bookingFacade.cancelTicket(ticketId);

        var response = wasDeleted
                ? new BookingResponse("User account was deleted successfully")
                : new BookingResponse("User account cannot be deleted");

        return ResponseEntity.ok(response);
    }

    private BiFunction<Integer, Integer, List<Event>> findEventsByTitle(String title) {
        LOGGER.info("Searching existing user with title: {}", title);
        return (pageSize, pageNum) -> bookingFacade.getEventsByTitle(title, pageSize, pageNum);
    }

    private BiFunction<Integer, Integer, List<Event>> findEventsByDate(Date date) {
        LOGGER.info("Searching existing user with date: {}", date);
        return (pageSize, pageNum) ->
                bookingFacade.getEventsForDay(date, pageSize, pageNum);
    }

    private BiFunction<Integer, Integer, List<Ticket>> findTicketsByUserId(long userId) {
        LOGGER.info("Searching booked tickets for user with id: {}", userId);
        var user = bookingFacade.getUserById(userId);
        return (pageSize, pageNum) -> bookingFacade.getBookedTickets(user, pageSize, pageNum);
    }

    private BiFunction<Integer, Integer, List<Ticket>> findTicketsByEventId(long eventId) {
        LOGGER.info("Searching booked tickets for event with id: {}", eventId);
        var event = bookingFacade.getEventById(eventId);
        return (pageSize, pageNum) -> bookingFacade.getBookedTickets(event, pageSize, pageNum);
    }

}
package app.facade;

import app.domain.Event;
import app.domain.Ticket;
import app.domain.TicketCategory;
import app.domain.User;
import app.service.EventService;
import app.service.TicketService;
import app.service.UserService;
import java.time.LocalDateTime;
import java.util.Optional;
import org.springframework.stereotype.Service;

@Service
public class BookingFacadeImpl implements BookingFacade {
    private final UserService userService;
    private final EventService eventService;
    private final TicketService ticketService;

    public BookingFacadeImpl(UserService userService, EventService eventService, TicketService ticketService) {
        this.userService = userService;
        this.eventService = eventService;
        this.ticketService = ticketService;
    }

    @Override
    public User createUser(Long id, String name, String email) {
        User user = new User(id, name, email);
        return userService.createUser(user);
    }

    @Override
    public Event createEvent(Long id, String title, LocalDateTime date) {
        return eventService.createEvent(new Event(id, title, LocalDateTime.now()));
    }

    @Override
    public Ticket bookTicket(Long ticketId, Long eventId, Long userId, Integer place, String category) {
        TicketCategory ticketCategory = TicketCategory.valueOf(category);
        Ticket ticket = new Ticket(ticketId, userId, eventId, place, ticketCategory);
        ticketService.bookTicket(ticket);
        return ticket;
    }

    @Override
    public Optional<Ticket> getTicketById(Long ticketId) {
        return Optional.ofNullable(ticketService.getTicketById(ticketId));
    }

    @Override
    public Optional<Event> getEventById(Long eventId) {
        return Optional.ofNullable(eventService.getEventById(eventId));
    }

    @Override
    public Optional<User> getUserById(Long userId) {
        return Optional.ofNullable(userService.getUserById(userId));
    }
}
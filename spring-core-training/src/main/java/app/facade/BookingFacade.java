package app.facade;

import app.domain.Event;
import app.domain.Ticket;
import app.domain.User;
import java.time.LocalDateTime;
import java.util.Optional;

public interface BookingFacade {
    User createUser(Long id, String name, String email);
    Event createEvent(Long id, String title, LocalDateTime date);
    Ticket bookTicket(Long ticketId, Long eventId, Long userId, Integer place, String category);
    Optional<Ticket> getTicketById(Long ticketId);
    Optional<Event> getEventById(Long eventId);
    Optional<User> getUserById(Long userId);
}
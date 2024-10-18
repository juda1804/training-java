package app.facade;

import app.domain.Event;
import app.domain.Ticket;
import app.domain.User;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import org.springframework.data.domain.Page;

public interface BookingFacade {
    User createUser(String name, String email);
    Event createEvent(String title, LocalDateTime date);
    Ticket bookTicket(Long eventId, Long userId, Integer place, String category);
    Optional<Ticket> getTicketById(Long ticketId);
    Optional<Event> getEventById(Long eventId);
    void preloadTickets(String path);
    Optional<User> getUserById(Long userId);
    List<Ticket> getAllTickets();
    List<User> getAllUsers();
    Page<Ticket> getBookedTickets(User user, int pageSize, int pageNum);
}
package app.facade;

import app.domain.Event;
import app.domain.Ticket;
import app.domain.TicketBooked;
import app.domain.User;
import app.domain.UserAccount;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import org.springframework.data.domain.Page;

public interface BookingFacade {
    User createUser(String name, String email);
    UserAccount addBalance(Long userId, long amount);
    Event createEvent(String title, LocalDateTime date);

    void preloadTickets(String path);

    Ticket createTicket(Long eventId, int place, long price, String category);

    TicketBooked bookTicket(Long ticketId, Long userId);

    Optional<Ticket> getTicketById(Long ticketId);
    Optional<Event> getEventById(Long eventId);
    Optional<User> getUserById(Long userId);

    List<Ticket> getAllTickets();
    List<User> getAllUsers();

    Page<TicketBooked> getBookedTickets(User user, int pageSize, int pageNum);
}
package app.facade;

import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

import app.domain.Event;
import app.domain.Ticket;
import app.domain.TicketCategory;
import app.domain.User;
import app.service.EventService;
import app.service.TicketService;
import app.service.UserService;
import java.time.LocalDateTime;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

class BookingFacadeImplTest {

    @Mock
    private UserService userService;

    @Mock
    private EventService eventService;

    @Mock
    private TicketService ticketService;

    @InjectMocks
    private BookingFacadeImpl bookingFacade;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void createUser_success() {
        Long id = 1L;
        String name = "John";
        String email = "john@example.com";

        User user = new User(id, name, email);
        when(userService.createUser(any(User.class))).thenReturn(user);

        User createdUser = bookingFacade.createUser(id, name, email);

        assertNotNull(createdUser);
        assertEquals(id, createdUser.getId());
        assertEquals(name, createdUser.getName());
        assertEquals(email, createdUser.getEmail());

        verify(userService, times(1)).createUser(any(User.class));
    }

    @Test
    void createEvent_success() {
        Long id = 1L;
        String title = "Concert";
        LocalDateTime date = LocalDateTime.now();

        Event event = new Event(id, title, date);
        when(eventService.createEvent(any(Event.class))).thenReturn(event);

        Event createdEvent = bookingFacade.createEvent(id, title, date);

        assertNotNull(createdEvent);
        assertEquals(id, createdEvent.getId());
        assertEquals(title, createdEvent.getTitle());
        assertEquals(date, createdEvent.getDateTime());

        verify(eventService, times(1)).createEvent(any(Event.class));
    }
    @Test
    void bookTicket_success() {
        Long ticketId = 1L;
        Long eventId = 2L;
        Long userId = 3L;
        Integer place = 10;
        String category = "VIP";

        doNothing().when(ticketService).bookTicket(any(Ticket.class));

        Ticket bookedTicket = bookingFacade.bookTicket(ticketId, eventId, userId, place, category);

        assertNotNull(bookedTicket);
        assertEquals(ticketId, bookedTicket.getId());
        assertEquals(eventId, bookedTicket.getEventId());
        assertEquals(userId, bookedTicket.getUserId());
        assertEquals(place, bookedTicket.getPlace());
        assertEquals(TicketCategory.VIP, bookedTicket.getCategory());

        verify(ticketService, times(1)).bookTicket(any(Ticket.class));
    }
}
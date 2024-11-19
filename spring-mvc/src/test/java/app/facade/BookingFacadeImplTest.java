package app.facade;

import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

import app.domain.Event;
import app.domain.Ticket;
import app.domain.TicketBooked;
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

        User user = new User();
        user.setId(id);
        user.setName("John");
        user.setEmail(email);

        when(userService.createUser(any(User.class))).thenReturn(user);

        User createdUser = bookingFacade.createUser(name, email);

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

        Event createdEvent = bookingFacade.createEvent(title, date);

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
        Long price = 10000l;
        String category = "VIP";


        Event event = new Event(eventId, "", LocalDateTime.now());
        User user = new User();
        user.setId(userId);
        user.setName("John");
        user.setEmail("john@example.com");


        TicketBooked ticketBooked = new TicketBooked();
        ticketBooked.setId(ticketId);
        ticketBooked.setUser(user);
        ticketBooked.setTicket(new Ticket(ticketId, event, place, price, TicketCategory.valueOf(category)));

        when(ticketService.bookTicket(anyLong(), anyLong())).thenReturn(ticketBooked);

        TicketBooked bookedTicket = bookingFacade.bookTicket(eventId, userId);

        assertNotNull(bookedTicket);
        assertEquals(ticketId, bookedTicket.getId());
        assertEquals(place, bookedTicket.getTicket().getPlace());
        assertEquals(category, bookedTicket.getTicket().getCategory().name());

        verify(ticketService, times(1)).bookTicket(anyLong(), anyLong());
    }
}
package app.service;

import static org.junit.jupiter.api.Assertions.*;

import app.dao.TicketRepository;
import app.domain.Event;
import app.domain.TicketCategory;
import com.github.javafaker.Faker;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.Optional;
import java.util.concurrent.TimeUnit;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import app.domain.Ticket;

import static org.mockito.Mockito.*;

class TicketServiceTest {
    private Faker faker =  Faker.instance();

    @Mock
    private TicketRepository ticketDao;

    @InjectMocks
    private TicketService ticketService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testBookTicket() {
        // Arrange
        Event event = new Event(1L, faker.rockBand().name(), LocalDateTime.ofEpochSecond(faker.date().future(300, TimeUnit.DAYS).getTime(), 0, ZoneOffset.of("Z")));
        Ticket ticket = new Ticket(1L,  event, 1, 150000,  TicketCategory.VIP);

        // Act
        ticketService.createTicket(ticket);

        // Verify
        verify(ticketDao, times(1)).save(ticket);
    }

    @Test
    void testGetTicketById() {
        // Arrange
        Long ticketId = 1L;
        Ticket ticket = getTicket(1000l, getEvent());
        when(ticketDao.findById(ticketId)).thenReturn(Optional.of(ticket));

        // Act
        Ticket foundTicket = ticketService.getTicketById(ticketId);

        // Assert
        assertNotNull(foundTicket);
        assertEquals(ticketId, foundTicket.getId());
        assertEquals(1L, foundTicket.getEvent().getId());
        assertEquals(TicketCategory.VIP, foundTicket.getCategory());

        // Verify
        verify(ticketDao, times(1)).findById(ticketId);
    }

    private Ticket getTicket(long amount, Event event) {
        Ticket ticket = new Ticket();
        ticket.setId(1L);
        ticket.setPrice(amount);
        ticket.setCategory(TicketCategory.VIP);
        ticket.setEvent(event);
        return ticket;

    }

    private Event getEvent() {
        return new Event(1L, faker.rockBand().name(), LocalDateTime.now());
    }
}

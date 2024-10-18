package app.service;

import static org.junit.jupiter.api.Assertions.*;

import app.dao.TicketRepository;
import app.domain.TicketCategory;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import app.domain.Ticket;

import static org.mockito.Mockito.*;

class TicketServiceTest {

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
        Ticket ticket = new Ticket(1L, 1L, 123L, 1, TicketCategory.VIP);

        // Act
        ticketService.bookTicket(ticket);

        // Verify
        verify(ticketDao, times(1)).save(ticket);
    }

    @Test
    void testGetTicketById() {
        // Arrange
        Long ticketId = 1L;
        Ticket ticket = new Ticket(ticketId, 1L, 123L, 1, TicketCategory.VIP);
        when(ticketDao.findById(ticketId)).thenReturn(Optional.of(ticket));

        // Act
        Ticket foundTicket = ticketService.getTicketById(ticketId);

        // Assert
        assertNotNull(foundTicket);
        assertEquals(ticketId, foundTicket.getId());
        assertEquals(123L, foundTicket.getEventId());
        assertEquals(TicketCategory.VIP, foundTicket.getCategory());

        // Verify
        verify(ticketDao, times(1)).findById(ticketId);
    }
}
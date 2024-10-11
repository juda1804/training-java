package app.service;

import static org.junit.jupiter.api.Assertions.*;

import app.dao.TicketDao;
import app.domain.TicketCategory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import app.domain.Ticket;

import static org.mockito.Mockito.*;

class TicketServiceTest {

    @Mock
    private TicketDao ticketDao;

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
        verify(ticketDao, times(1)).saveTicket(ticket);
    }

    @Test
    void testGetTicketById() {
        // Arrange
        Long ticketId = 1L;
        Ticket ticket = new Ticket(ticketId, 1L, 123L, 1, TicketCategory.VIP);
        when(ticketDao.findTicketById(ticketId)).thenReturn(ticket);

        // Act
        Ticket foundTicket = ticketService.getTicketById(ticketId);

        // Assert
        assertNotNull(foundTicket);
        assertEquals(ticketId, foundTicket.getId());
        assertEquals(123L, foundTicket.getEventId());
        assertEquals(TicketCategory.VIP, foundTicket.getCategory());

        // Verify
        verify(ticketDao, times(1)).findTicketById(ticketId);
    }
}
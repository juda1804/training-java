package app.service;

import app.dao.TicketDao;
import app.domain.Ticket;
import java.util.List;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class TicketService {
    private final TicketDao ticketDao;

    public TicketService(TicketDao ticketDao) {
        this.ticketDao = ticketDao;
    }

    public void bookTicket(Ticket ticket) {
        log.info("Booking ticket {}", ticket);
        ticketDao.saveTicket(ticket);
    }

    public Ticket getTicketById(Long id) {
        log.info("Get ticket by id {}", id);
        return ticketDao.findTicketById(id);
    }
}

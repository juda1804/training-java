package app.service;

import app.dao.TicketRepository;
import app.domain.Event;
import app.domain.Ticket;
import java.util.List;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class TicketService {
    private final TicketRepository ticketDao;

    public TicketService(TicketRepository ticketDao) {
        this.ticketDao = ticketDao;
    }

    public void bookTicket(Ticket ticket) {
        Ticket save = ticketDao.save(ticket);
        log.info("Booking ticket {}", save);
    }

    public Ticket getTicketById(Long id) {
        log.info("Get ticket by id {}", id);
        return ticketDao.findById(id).orElse(null);
    }

    public List<Ticket> getAllTickets() {
        log.info("Get all tickets");
        return ticketDao.findAll();
    }


    public Page<Ticket> searchTickets(Long userId, Pageable pageable) {
        return ticketDao.findAllByUserId(userId, pageable);
    }
}

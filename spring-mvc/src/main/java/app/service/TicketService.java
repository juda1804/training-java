package app.service;

import app.dao.TicketBookedRepository;
import app.dao.TicketRepository;
import app.domain.Ticket;
import app.domain.TicketBooked;
import app.domain.TicketCategory;
import app.domain.UserAccount;
import java.util.List;
import java.util.Optional;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
public class TicketService {
    private final TicketRepository ticketDao;
    private final TicketBookedRepository ticketBookedRepository;
    private final UserService userService;

    public TicketService(TicketRepository ticketDao,
                         TicketBookedRepository ticketBookedRepository,
                         UserService userService) {
        this.ticketDao = ticketDao;
        this.ticketBookedRepository = ticketBookedRepository;
        this.userService = userService;
    }

    @Transactional
    public TicketBooked bookTicket(Long ticketId, Long userId) {

        Ticket ticket = getTicketById(ticketId);

        UserAccount userAccount = userService.getUserAccountByUserId(userId);
        userAccount.withdraw(ticket.getPrice());


        TicketBooked ticketBooked = new TicketBooked();
        ticketBooked.setTicket(ticket);
        ticketBooked.setUser(userAccount.getUser());

        userService.saveUserAccount(userAccount);
        return ticketBookedRepository.save(ticketBooked);
    }

    public Ticket getTicketById(Long id) {
        log.debug("Get ticket by id {}", id);
        return ticketDao.findById(id).orElse(null);
    }

    public Optional<Ticket> getTicket(Long eventId, int place, String category) {
        TicketCategory ticketCategory = TicketCategory.valueOf(category);
        return ticketDao
                .findByEventIdAndPlaceAndCategory(eventId, place, ticketCategory);

    }

    public List<Ticket> getAllTickets() {
        log.debug("Get all tickets");
        return ticketDao.findAll();
    }

    public Ticket createTicket(Ticket ticket) {
        return ticketDao.save(ticket);
    }


    public Page<TicketBooked> searchTickets(Long userId, Pageable pageable) {
        return ticketBookedRepository.findAllByUserId(userId, pageable);
    }
}

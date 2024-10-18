package app.facade;

import app.domain.Event;
import app.domain.Ticket;
import app.domain.TicketCategory;
import app.domain.User;
import app.facade.dto.TicketDto;
import app.facade.dto.TicketsDto;
import app.service.EventService;
import app.service.TicketService;
import app.service.UserService;
import jakarta.xml.bind.Unmarshaller;
import java.io.FileInputStream;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import javax.xml.transform.stream.StreamSource;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.TransactionDefinition;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.DefaultTransactionDefinition;

@Slf4j
@Service
public class BookingFacadeImpl implements BookingFacade {
    private final UserService userService;
    private final EventService eventService;
    private final TicketService ticketService;

    private final Unmarshaller unmarshaller;
    private final PlatformTransactionManager transactionManager;

    public BookingFacadeImpl(UserService userService, EventService eventService, TicketService ticketService, Unmarshaller unmarshaller, PlatformTransactionManager transactionManager) {
        this.userService = userService;
        this.eventService = eventService;
        this.ticketService = ticketService;
        this.unmarshaller = unmarshaller;
        this.transactionManager = transactionManager;
    }

    @Override
    public User createUser(String name, String email) {
        log.info("Creating user {} {}", name, email);
        User user = new User();
        user.setName(name);
        user.setEmail(email);
        return userService.createUser(user);
    }

    @Override
    public Event createEvent(String title, LocalDateTime date) {
        Event event = new Event();
        event.setTitle(title);
        event.setDateTime(date);
        return eventService.createEvent(event);
    }

    @Override
    public Ticket bookTicket(Long eventId, Long userId, Integer place, String category) {
        TicketCategory ticketCategory = TicketCategory.valueOf(category);

        Ticket ticket = new Ticket();
        ticket.setUserId(userId);
        ticket.setEventId(eventId);
        ticket.setPlace(place);
        ticket.setCategory(ticketCategory);

        ticketService.bookTicket(ticket);
        return ticket;
    }

    @Override
    public Optional<Ticket> getTicketById(Long ticketId) {
        return Optional.ofNullable(ticketService.getTicketById(ticketId));
    }

    @Override
    public Optional<Event> getEventById(Long eventId) {
        return Optional.ofNullable(eventService.getEventById(eventId));
    }

    @Override
    public Optional<User> getUserById(Long userId) {
        return Optional.ofNullable(userService.getUserById(userId));
    }

    @Override
    public List<Ticket> getAllTickets() {
        return ticketService.getAllTickets();
    }

    @Override
    public List<User> getAllUsers() {
        return userService.getAllUsers();
    }

    @Override
    public Page<Ticket> getBookedTickets(User user, int pageSize, int pageNum) {

        Pageable pageable = PageRequest.of(pageNum, pageSize);
        return ticketService.searchTickets(pageable);

    }

    @SneakyThrows
    public void preloadTickets(String filePath) {
        FileInputStream inputStream = new FileInputStream(filePath);
        StreamSource streamSource = new StreamSource(inputStream);

        TicketsDto ticketsDto = (TicketsDto) unmarshaller.unmarshal(streamSource);

        DefaultTransactionDefinition def = new DefaultTransactionDefinition();
        def.setName("PreloadTicketsTransaction");
        def.setPropagationBehavior(TransactionDefinition.PROPAGATION_REQUIRED);

        TransactionStatus status = transactionManager.getTransaction(def);

        try {
            List<TicketDto> ticketList = ticketsDto.getTickets();
            for (TicketDto ticketDto : ticketList) {
                Ticket ticket = new Ticket();
                ticket.setUserId(ticketDto.getUserId());
                ticket.setEventId(ticketDto.getEventId());
                ticket.setPlace(ticketDto.getPlace());
                ticket.setCategory(TicketCategory.valueOf(ticketDto.getCategory()));

                ticketService.bookTicket(ticket);  // Saving each ticket to the repository
            }
            transactionManager.commit(status);  // Commit the transaction if everything goes well
        } catch (Exception e) {
            transactionManager.rollback(status);  // Rollback if something goes wrong
            throw e;
        } finally {
            inputStream.close();
        }
    }
}
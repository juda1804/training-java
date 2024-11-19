package org.booking.facade;

import jakarta.annotation.PostConstruct;
import org.booking.model.*;
import org.booking.service.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

import static org.booking.util.IdentifierGenerator.generateId;

@Component
public class BookingFacadeImpl implements BookingFacade {
    private final Logger LOGGER = LoggerFactory.getLogger(BookingFacadeImpl.class);

    @Value("${file.path.tickets}")
    private String ticketsFilePath;

    private final EventService eventService;
    private final TicketService ticketService;
    private final UserService userService;
    private final UserAccountService userAccountService;
    private final ReadDataService readDataService;

    public BookingFacadeImpl(
            EventService eventService,
            TicketService ticketService,
            UserService userService,
            UserAccountService userAccountService,
            ReadDataService readDataService
    ) {
        this.eventService = eventService;
        this.ticketService = ticketService;
        this.userService = userService;
        this.userAccountService = userAccountService;
        this.readDataService = readDataService;
    }

    /**
     * Preload all configured tickets when the BookingFacadeImpl is initialized
     */
    @PostConstruct
    private void postConstruct() {
        preloadTickets();
    }

    @Override
    public Event getEventById(long eventId) {
        return eventService.getEventById(eventId);
    }

    @Override
    public List<Event> getEventsByTitle(String title, int pageSize, int pageNum) {
        return eventService.getEventsByTitle(title, pageSize, pageNum);
    }

    @Override
    public List<Event> getEventsForDay(Date day, int pageSize, int pageNum) {
        return eventService.getEventsForDay(day, pageSize, pageNum);
    }

    @Override
    public Event createEvent(Event event) {
        return eventService.create(event);
    }

    @Override
    public Event updateEvent(Event event) {
        return eventService.update(event);
    }

    @Override
    public boolean deleteEvent(long eventId) {
        return eventService.delete(eventId);
    }

    @Override
    public User getUserById(long userId) {
        return userService.getUserById(userId);
    }

    @Override
    public User getUserByEmail(String email) {
        return userService.getUserByEmail(email);
    }

    @Override
    public List<User> getUsersByName(String name, int pageSize, int pageNum) {
        LOGGER.debug("find user by name: {}, pageSize: {}, pageNum: {}", name, pageSize, pageNum);
        return userService.getUsersByName(name, pageSize, pageNum);
    }

    @Override
    public User createUser(User user) {
        return userService.create(user);
    }

    @Override
    public User updateUser(User user) {
        return userService.update(user);
    }

    @Override
    public boolean deleteUser(long userId) {
        return userService.delete(userId);
    }

    @Transactional()
    @Override
    public Ticket bookTicket(long userId, long eventId, int place, Ticket.Category category) {
        return buyTicket(userId, eventId, place, category)
                .map(ticketService::book)
                .orElseThrow(() -> {
                    LOGGER.error("Balance for user id: {} is not sufficient to book the ticket", userId);
                    return new NoSuchElementException(
                            String.format("Balance for user id: %s is not sufficient to book the ticket", userId)
                    );
                });
    }

    @Override
    public List<Ticket> getBookedTickets(User user, int pageSize, int pageNum) {
        return ticketService
                .getBookedTicketsByUserId(user.getId(), pageSize, pageNum).stream()
                .map(ticket -> {
                    var event = eventService.getEventById(ticket.getEventId());
                    return new Book(ticket, event, user);
                })
                .sorted(Comparator.<Book, Date>comparing(book -> book.event().getDate()).reversed())
                .map(Book::ticket)
                .toList();
    }

    @Override
    public List<Ticket> getBookedTickets(Event event, int pageSize, int pageNum) {
        return ticketService
                .getBookedTicketsByEventId(event.getId(), pageSize, pageNum).stream()
                .map(ticket -> {
                    var user = userService.getUserById(ticket.getUserId());
                    return new Book(ticket, event, user);
                })
                .sorted(Comparator.comparing(book -> book.user().getEmail()))
                .map(Book::ticket)
                .toList();
    }

    @Override
    public boolean cancelTicket(long ticketId) {
        return ticketService.cancel(ticketId);
    }

    @Transactional()
    @Override
    public UserAccount createUserAccount(UserAccount userAccount) {
        if (userService.existsUser(userAccount.getUserId())) {
            return userAccountService.createUserAccount(userAccount);
        } else {
            throw new NoSuchElementException("User not found");
        }
    }

    @Override
    public boolean refillUserAccount(long id, double amount) {
        return userAccountService.refillUserAccount(id, amount);
    }

    @Override
    public boolean deleteAccount(long accountId) {
        return userAccountService.delete(accountId);
    }

    @Transactional()
    @Override
    public void preloadTickets() {
        readDataService
                .readFile(ticketsFilePath, Tickets::getTicket)
                .forEach(ticketService::book);
    }

    private Optional<Ticket> buyTicket(long userId, long eventId, int place, Ticket.Category category) {
        var event = eventService.getEventById(eventId);
        var userAccounts = findUserAccountByUserIdWithDebit(userId);
        var totalAmount = getTotalAmount(userAccounts);

        if (hadPlaceAlreadyBeenBooked(eventId, place)) {
            throw new IllegalStateException("This place had already been booked");
        }

        if (event.getTicketPrice() <= totalAmount) {
            var sortedAccounts = userAccounts.stream()
                    .sorted(Comparator.comparingDouble(UserAccount::getAmount).reversed())
                    .toList();
            var modifierUserAccounts = debit(event.getTicketPrice(), sortedAccounts, new ArrayList<>());
            userAccountService.update(modifierUserAccounts);

            return Optional.of(defineTicket(userId, eventId, place, category));
        } else {
            return Optional.empty();
        }
    }

    private boolean hadPlaceAlreadyBeenBooked(long eventId, int place) {
        var bookedTickets = ticketService.getBookedTicketsByEventId(eventId);

        return bookedTickets.stream().anyMatch(ticket -> ticket.getPlace() == place);
    }

    private List<UserAccount> debit(double price, List<UserAccount> accounts, List<UserAccount> newAccounts) {
        if (!accounts.isEmpty() && price > 0) {
            var userAccount = accounts.get(0);
            var headAmount = userAccount.getAmount();

            var isPriceGreaterThanAmount = price >= headAmount;
            var newPrice = isPriceGreaterThanAmount ? price - headAmount : 0;
            var newAccountAmount = isPriceGreaterThanAmount ? 0 : headAmount - price;

            userAccount.setAmount(newAccountAmount);
            newAccounts.add(userAccount);
            return debit(newPrice, accounts.subList(1, accounts.size()), newAccounts);
        } else {
            return newAccounts;
        }
    }

    private List<UserAccount> findUserAccountByUserIdWithDebit(long userId) {
        return userAccountService.findUserAccountByUserId(userId)
                .stream()
                .filter(userAccount -> userAccount.getAmount() > 0)
                .toList();
    }

    private double getTotalAmount(List<UserAccount> userAccounts) {
        return userAccounts
                .stream()
                .map(UserAccount::getAmount)
                .reduce( 0.0, Double::sum);
    }

    private Ticket defineTicket(long userId, long eventId, int place, Ticket.Category category) {
        Ticket ticket = new Ticket();
        ticket.setId(generateId());
        ticket.setUserId(userId);
        ticket.setEventId(eventId);
        ticket.setPlace(place);
        ticket.setCategory(category);

        return ticket;
    }
}

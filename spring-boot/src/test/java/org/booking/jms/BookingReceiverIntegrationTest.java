package org.booking.jms;

import org.booking.TestWebApplication;
import org.booking.data.repository.EventRepository;
import org.booking.data.repository.TicketRepository;
import org.booking.data.repository.UserAccountRepository;
import org.booking.data.repository.UserRepository;
import org.booking.model.Event;
import org.booking.model.Ticket;
import org.booking.model.User;
import org.booking.model.UserAccount;
import org.junit.Test;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Disabled;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jms.annotation.EnableJms;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.test.context.junit4.SpringRunner;

import java.time.LocalDate;

import static org.booking.util.DateConverter.convertToDate;
import static org.booking.util.IdentifierGenerator.generateId;

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT, classes = TestWebApplication.class)
@EnableJms
public class BookingReceiverIntegrationTest {

    @Autowired
    private JmsTemplate jmsTemplate;

    @Autowired
    private UserRepository userDao;

    @Autowired
    private UserAccountRepository accountDao;

    @Autowired
    private EventRepository eventDao;

    @Autowired
    private TicketRepository ticketDao;

    @Value("${jms.message.destination}")
    private String bookingMessage;

    @Test
    public void testTicket() {
        var tickets = ticketDao.findAll();
        Assertions.assertEquals(2, tickets.size());
    }

    @Disabled("fix Ticket deserialization error")
    public void testBookTicket() throws InterruptedException {
        var user = new User();
        user.setId(generateId());
        user.setName("Maria");
        user.setEmail("maria@email.com");

        var account = new UserAccount();
        account.setId(generateId());
        account.setUserId(user.getId());
        account.setAmount(50);

        var event = new Event();
        event.setId(generateId());
        event.setTitle("Romolus");
        event.setDate(convertToDate(LocalDate.now()));
        event.setTicketPrice(20.0);

        var ticket = new Ticket();
        ticket.setUserId(user.getId());
        ticket.setEventId(event.getId());
        ticket.setPlace(10);
        ticket.setCategory(Ticket.Category.STANDARD);

        userDao.save(user);
        accountDao.save(account);
        eventDao.save(event);

        jmsTemplate.convertAndSend(bookingMessage, ticket);

        Thread.sleep(300);

        var filteredTickets = ticketDao.findAll().stream()
                .filter(t -> t.getUserId() == user.getId())
                .toList();

        Assertions.assertEquals(1, filteredTickets.size());
        Assertions.assertTrue(filteredTickets.stream().findFirst().isPresent());

        var bookedTicket = filteredTickets.stream().findFirst().get();

        Assertions.assertEquals(ticket.getUserId(), bookedTicket.getUserId());
        Assertions.assertEquals(ticket.getEventId(), bookedTicket.getEventId());
        Assertions.assertEquals(ticket.getCategory(), bookedTicket.getCategory());
        Assertions.assertEquals(ticket.getPlace(), bookedTicket.getPlace());
    }
}

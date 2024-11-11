package org.booking.config;

import org.booking.data.repository.EventRepository;
import org.booking.data.repository.TicketRepository;
import org.booking.data.repository.UserRepository;
import org.junit.Test;
import org.junit.jupiter.api.Assertions;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations={"/applicationContext.xml"})
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.NONE)
public class DbInitializationTest {

    @Autowired
    private EventRepository eventRepository;

    @Autowired
    private TicketRepository ticketRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private DbInitialization dbInit;

    @Test
    public void testEventDataInitialization() {

        var event = eventRepository.findById(56566505L);

        Assertions.assertTrue(event.isPresent());
    }

    @Test
    public void testUSerDataInitialization() {
        var event = userRepository.findById(1565650L);

        Assertions.assertTrue(event.isPresent());
    }

}

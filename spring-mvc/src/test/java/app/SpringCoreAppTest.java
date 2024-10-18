package app;

import static app.domain.TicketCategory.STANDARD;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mockStatic;

import app.domain.Event;
import app.domain.Ticket;
import app.domain.User;
import app.facade.BookingFacade;
import java.time.LocalDateTime;
import java.util.Optional;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
class SpringCoreAppTest {
    @Autowired
    private BookingFacade bookingFacade;

    @Test
    void testMain() {
        try (var mockedSpringApplication = mockStatic(SpringApplication.class)) {
            mockedSpringApplication.when(() -> SpringApplication.run(SpringCoreApp.class, new String[]{})).thenReturn(null);
            SpringCoreApp.main(new String[]{});
            mockedSpringApplication.verify(() -> SpringApplication.run(SpringCoreApp.class, new String[]{}));
        }
    }

    @Test
    void testCreateEvent() {
        Event eventById = bookingFacade.createEvent("demo", LocalDateTime.now());
        assertEquals("demo", eventById.getTitle());
    }

    @Test
    void testCreateTicket() {
        bookingFacade.bookTicket( 1L, 1L, 100, STANDARD.name());
        Optional<Ticket> ticket = bookingFacade.getTicketById(101L);

        assertTrue(ticket.isPresent());
        assertEquals(STANDARD, ticket.get().getCategory());
    }

    @Test
    void testCreateUser() {
        bookingFacade.createUser("Juan", "juan@epam.com");
        Optional<User> user = bookingFacade.getUserById(9999L);

        assertTrue(user.isPresent());
        assertEquals("Juan", user.get().getName());
    }

}
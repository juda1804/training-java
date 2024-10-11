package app.config;

import app.domain.Event;
import app.domain.Ticket;
import app.domain.User;
import app.facade.BookingFacade;
import app.facade.BookingFacadeImpl;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import java.util.HashMap;
import java.util.Map;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import app.service.EventService;
import app.service.TicketService;
import app.service.UserService;

@Configuration
public class AppConfig {

    @Value("${data.file.path}")
    private String filePath;

    @Bean
    public BookingFacade bookingFacade(UserService userService, EventService eventService, TicketService ticketService) {
        return new BookingFacadeImpl(userService, eventService, ticketService);
    }

    @Bean
    public Map<String, Event> createEventInitDataStore() {
        return new HashMap<>();
    }
    @Bean
    public Map<String, User> createUserInitDataStore() {
        return new HashMap<>();
    }
    @Bean
    public Map<String, Ticket> createTicketInitDataStore() {
        return new HashMap<>();
    }

    @Bean
    public ObjectMapper objectMapper() {
        ObjectMapper mapper = new ObjectMapper();
        mapper.registerModule(new JavaTimeModule());
        return mapper;
    }

    /*
    @Bean
    public StorageInitializer storageInitializer(
            Map<String, Event> initialEvents,
            Map<String, User> initialUsers,
            Map<String, Ticket> initialTickets
            ) {
        StorageInitializer initializer = new StorageInitializer(filePath, initialUsers,initialEvents, initialTickets);
        initializer.setFilePath(filePath);
        return initializer;
    }

     */
}

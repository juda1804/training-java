package org.booking.config;

import jakarta.annotation.PostConstruct;
import org.booking.data.repository.EventRepository;
import org.booking.data.repository.TicketRepository;
import org.booking.data.repository.UserRepository;
import org.booking.model.*;
import org.booking.service.ReadDataService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.repository.ListCrudRepository;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.List;
import java.util.function.Function;

@Component
public class DbInitialization {
    @Value("${file.path.events}")
    private String eventsFilePath;

    @Value("${file.path.users}")
    private String usersFilePath;

    @Autowired
    private ReadDataService readDataService;

    @Autowired
    private EventRepository eventRepository;

    @Autowired
    private TicketRepository ticketRepository;

    @Autowired
    private UserRepository userRepository;

    @PostConstruct
    private void postConstruct() {
        initializeTables(
                () -> initializeData(eventsFilePath, Events::getEvent, eventRepository),
                () -> initializeData(usersFilePath, Users::getUser, userRepository)
        );
    }

    private <T, C, ID> void initializeData(
            String filePath,
            Function<C, List<T>> getValue,
            ListCrudRepository<T, ID> repository) {
        var data = readDataService.readFile(filePath, getValue);
        repository.saveAll(data);
    }

    private void initializeTables(Runnable... runnableList) {
        Arrays.stream(runnableList).forEach(Runnable::run);
    }
}

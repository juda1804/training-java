package org.booking.data.repository;

import org.booking.TestWebApplication;
import org.booking.model.Event;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.time.Instant;
import java.time.LocalDate;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.function.Function;

import static org.booking.CommonUtilTest.convertToDate;
import static org.booking.util.IdentifierGenerator.generateId;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.MOCK, classes = TestWebApplication.class)
public class EventRepositoryTest {

    @Autowired
    private EventRepository eventRepository;

    @BeforeEach
    public void resetDb() {
        eventRepository.deleteAll();
    }

    @Test
    public void testSaveEvent() {
        var event = new Event();
        event.setId(generateId());
        event.setDate(Date.from(Instant.now()));
        event.setTitle("Event1");
        event.setTicketPrice(10);

        var storedEvent = eventRepository.save(event);
        Assertions.assertEquals(event.getId(), storedEvent.getId());
        Assertions.assertEquals(event.getDate().getTime(), storedEvent.getDate().getTime());
        Assertions.assertEquals(event.getTitle(), storedEvent.getTitle());
        Assertions.assertEquals(event.getTicketPrice(), storedEvent.getTicketPrice());
    }

    @Test
    public void testSaveEventIfItWasAlreadyStored() {
        var id = generateId();

        var event1 = new Event();
        event1.setId(id);
        event1.setDate(Date.from(Instant.now()));
        event1.setTitle("Event1");
        event1.setTicketPrice(10);

        var storedEvent1 = eventRepository.save(event1);
        Assertions.assertEquals(event1.getId(), storedEvent1.getId());
        Assertions.assertEquals(event1.getDate().getTime(), storedEvent1.getDate().getTime());
        Assertions.assertEquals(event1.getTitle(), storedEvent1.getTitle());

        var event2 = new Event();
        event2.setId(id);
        event2.setDate(Date.from(Instant.now()));
        event2.setTitle("Event2");
        event2.setTicketPrice(20);

        var storedEvent2 = eventRepository.save(event2);
        Assertions.assertEquals(event2.getId(), storedEvent2.getId());
        Assertions.assertEquals(event2.getDate().getTime(), storedEvent2.getDate().getTime());
        Assertions.assertEquals(event2.getTitle(), storedEvent2.getTitle());
        Assertions.assertEquals(event2.getTicketPrice(), storedEvent2.getTicketPrice());
    }

    @Test
    public void testFindById() {
        var id = generateId();
        var event = new Event();
        event.setId(id);
        event.setDate(Date.from(Instant.now()));
        event.setTitle("Event1");
        event.setTicketPrice(10);

        eventRepository.save(event);
        var eventResult = eventRepository.findById(id);

        Assertions.assertEquals(Optional.of(id), eventResult.map(Event::getId));
    }

    @Test
    public void testFindByIdIfEventHasNotBeenStored() {
        var id = generateId();

        var eventResult = eventRepository.findById(id);

        Assertions.assertEquals(Optional.empty(), eventResult);
    }


    @Test
    public void testFindByTitle() {
        var event1 = new Event();
        event1.setId(generateId());
        event1.setDate(Date.from(Instant.now()));
        event1.setTitle("Movie1");
        event1.setTicketPrice(10);

        var event2 = new Event();
        event2.setId(generateId());
        event2.setDate(Date.from(Instant.now()));
        event2.setTitle("Movie2");
        event2.setTicketPrice(12);

        var event3 = new Event();
        event3.setId(generateId());
        event3.setDate(Date.from(Instant.now()));
        event3.setTitle("Movie3");
        event3.setTicketPrice(10);

        var event4 = new Event();
        event4.setId(generateId());
        event4.setDate(Date.from(Instant.now()));
        event4.setTitle("Concert");
        event4.setTicketPrice(11);

        List.of(event1, event2, event3, event4)
                .forEach(eventRepository::save);

        var size = 2;
        Function<Integer, Pageable> createPage = (page) -> PageRequest.of(page, size);

        var eventsPage1 = eventRepository.findByTitleContaining("Movie", createPage.apply(0));
        var eventsPage2 = eventRepository.findByTitleContaining("Movie", createPage.apply(1));
        var eventsPage3 = eventRepository.findByTitleContaining("Movie", createPage.apply(2));

        Assertions.assertEquals(2, eventsPage1.size());
        Assertions.assertEquals(1, eventsPage2.size());
        Assertions.assertEquals(0, eventsPage3.size());
    }

    @Test
    public void testFindEventByDay() {

        var event1 = new Event();
        event1.setId(generateId());
        event1.setDate(convertToDate(LocalDate.now()));
        event1.setTitle("Event1");
        event1.setTicketPrice(10);

        var event2 = new Event();
        event2.setId(generateId());
        event2.setDate(convertToDate(LocalDate.now().plusDays(1)));
        event2.setTitle("Event2");
        event2.setTicketPrice(11);

        var event3 = new Event();
        event3.setId(generateId());
        event3.setDate(convertToDate(LocalDate.now().minusDays(1)));
        event3.setTitle("Event3");
        event3.setTicketPrice(10);

        var event4 = new Event();
        event4.setId(generateId());
        event4.setDate(convertToDate(LocalDate.now().plusDays(1)));
        event4.setTitle("Concert1");
        event4.setTicketPrice(11);

        var event5 = new Event();
        event5.setId(generateId());
        event5.setDate(convertToDate(LocalDate.now().plusDays(1)));
        event5.setTitle("Concert2");
        event5.setTicketPrice(50);

        List.of(event1, event2, event3, event4, event5)
                .forEach(eventRepository::save);

        var expectedDay = LocalDate.now().plusDays(1);

        var size = 2;
        Function<Integer, Pageable> createPage = (page) -> PageRequest.of(page, size);

        var eventsPage1 = eventRepository.findByDate(convertToDate(expectedDay), createPage.apply(0));
        var eventsPage2 = eventRepository.findByDate(convertToDate(expectedDay), createPage.apply(1));
        var eventsPage3 = eventRepository.findByDate(convertToDate(expectedDay), createPage.apply(2));

        Assertions.assertEquals(2, eventsPage1.size());
        Assertions.assertEquals(1, eventsPage2.size());
        Assertions.assertEquals(0, eventsPage3.size());
    }

    @Test
    public void testUpdateEvent() {
        var event = new Event();
        event.setId(generateId());
        event.setDate(Date.from(Instant.now()));
        event.setTitle("Event1");
        event.setTicketPrice(10);

        var storedEvent = eventRepository.save(event);
        Assertions.assertEquals(event.getId(), storedEvent.getId());
        Assertions.assertEquals(event.getDate().getTime(), storedEvent.getDate().getTime());
        Assertions.assertEquals(event.getTitle(), storedEvent.getTitle());

        event.setTitle("Concert1");

        var updatedEvent = eventRepository.save(event);
        Assertions.assertEquals(event.getId(), updatedEvent.getId());
        Assertions.assertEquals(event.getDate().getTime(), updatedEvent.getDate().getTime());
        Assertions.assertEquals(event.getTitle(), updatedEvent.getTitle());

    }

    @Test
    public void testUpdateEventIfItHasNotBeenStored() {
        var id = generateId();

        var event1 = new Event();
        event1.setId(id);
        event1.setDate(Date.from(Instant.now()));
        event1.setTitle("Event1");
        event1.setTicketPrice(10);

        var storedEvent1 = eventRepository.save(event1);
        Assertions.assertEquals(event1.getId(), storedEvent1.getId());
        Assertions.assertEquals(event1.getDate().getTime(), storedEvent1.getDate().getTime());
        Assertions.assertEquals(event1.getTitle(), storedEvent1.getTitle());
    }

    @Test
    public void testDeleteEvent() {
        var event = new Event();
        event.setId(generateId());
        event.setDate(Date.from(Instant.now()));
        event.setTitle("Event1");
        event.setTicketPrice(10);

        var storedEvent = eventRepository.save(event);
        Assertions.assertEquals(event.getId(), storedEvent.getId());
        Assertions.assertEquals(event.getDate().getTime(), storedEvent.getDate().getTime());
        Assertions.assertEquals(event.getTitle(), storedEvent.getTitle());

        var existsEventBeforeDeleting = eventRepository.existsById(event.getId());
        Assertions.assertTrue(existsEventBeforeDeleting);

        eventRepository.deleteById(event.getId());

        var existsEventAfterDeleting = eventRepository.existsById(event.getId());
        Assertions.assertFalse(existsEventAfterDeleting);
    }

}

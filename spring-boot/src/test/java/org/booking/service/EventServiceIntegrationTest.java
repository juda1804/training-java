package org.booking.service;

import org.booking.data.repository.EventRepository;
import org.booking.model.Event;
import org.junit.Before;
import org.junit.jupiter.api.Assertions;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.time.Instant;
import java.time.LocalDate;
import java.util.Date;
import java.util.List;
import java.util.NoSuchElementException;

import static org.booking.CommonUtilTest.convertToDate;
import static org.booking.util.IdentifierGenerator.generateId;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations={"/applicationContext.xml"})
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.NONE)
public class EventServiceIntegrationTest {

    @Autowired
    private EventRepository eventDao;
    @Autowired
    private EventService eventService;

    @Before
    public void resetDb() {
        eventDao.deleteAll();
    }

    @Test
    public void testSaveEvent() {
        var event = new Event();
        event.setId(generateId());
        event.setDate(Date.from(Instant.now()));
        event.setTitle("Event1");

        var storedEvent = eventService.create(event);
        Assertions.assertEquals(event.getId(), storedEvent.getId());
        Assertions.assertEquals(event.getDate().getTime(), storedEvent.getDate().getTime());
        Assertions.assertEquals(event.getTitle(), storedEvent.getTitle());
    }

    @Test
    public void testSaveEventIfItWasAlreadyStored() {
        var id = generateId();

        var event1 = new Event();
        event1.setId(id);
        event1.setDate(Date.from(Instant.now()));
        event1.setTitle("Event1");

        var storedEvent1 = eventDao.save(event1);
        Assertions.assertEquals(event1.getId(), storedEvent1.getId());
        Assertions.assertEquals(event1.getDate().getTime(), storedEvent1.getDate().getTime());
        Assertions.assertEquals(event1.getTitle(), storedEvent1.getTitle());

        var event2 = new Event();
        event2.setId(id);
        event2.setDate(Date.from(Instant.now()));
        event2.setTitle("Event2");

        var storedEvent2 = eventService.create(event2);
        Assertions.assertEquals(event2.getId(), storedEvent2.getId());
        Assertions.assertEquals(event2.getDate().getTime(), storedEvent2.getDate().getTime());
        Assertions.assertEquals(event2.getTitle(), storedEvent2.getTitle());
    }

    @Test
    public void testFindById() {
        var id = generateId();
        var event = new Event();
        event.setId(id);
        event.setDate(Date.from(Instant.now()));
        event.setTitle("Event1");

        eventDao.save(event);
        var eventResult = eventService.getEventById(id);

        Assertions.assertEquals(id, eventResult.getId());
    }

    @Test
    public void testFindByIdIfEventHasNotBeenStored() {
        var id = generateId();

        Assertions.assertThrows(NoSuchElementException.class, () -> eventService.getEventById(id));
    }


    @Test
    public void testFindEventByTitle() {
        var event1 = new Event();
        event1.setId(generateId());
        event1.setDate(Date.from(Instant.now()));
        event1.setTitle("Event1");

        var event2 = new Event();
        event2.setId(generateId());
        event2.setDate(Date.from(Instant.now()));
        event2.setTitle("Event2");

        var event3 = new Event();
        event3.setId(generateId());
        event3.setDate(Date.from(Instant.now()));
        event3.setTitle("Event3");

        var event4 = new Event();
        event4.setId(generateId());
        event4.setDate(Date.from(Instant.now()));
        event4.setTitle("Concert");

        List.of(event1, event2, event3, event4)
                .forEach(eventDao::save);

        var eventsPage1 = eventService.getEventsByTitle("Event", 2, 0);
        var eventsPage2 = eventService.getEventsByTitle("Event", 2, 1);
        var eventsPage3 = eventService.getEventsByTitle("Event", 2, 2);

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

        var event2 = new Event();
        event2.setId(generateId());
        event2.setDate(convertToDate(LocalDate.now().plusDays(1)));
        event2.setTitle("Event2");

        var event3 = new Event();
        event3.setId(generateId());
        event3.setDate(convertToDate(LocalDate.now().minusDays(1)));
        event3.setTitle("Event3");

        var event4 = new Event();
        event4.setId(generateId());
        event4.setDate(convertToDate(LocalDate.now().plusDays(1)));
        event4.setTitle("Concert1");

        var event5 = new Event();
        event5.setId(generateId());
        event5.setDate(convertToDate(LocalDate.now().plusDays(1)));
        event5.setTitle("Concert2");

        List.of(event1, event2, event3, event4, event5)
                .forEach(eventDao::save);

        var expectedDay = LocalDate.now().plusDays(1);

        var eventsPage1 = eventService.getEventsForDay(convertToDate(expectedDay), 2, 0);
        var eventsPage2 = eventService.getEventsForDay(convertToDate(expectedDay), 2, 1);
        var eventsPage3 = eventService.getEventsForDay(convertToDate(expectedDay), 2, 2);

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

        var storedEvent = eventDao.save(event);
        Assertions.assertEquals(event.getId(), storedEvent.getId());
        Assertions.assertEquals(event.getDate().getTime(), storedEvent.getDate().getTime());
        Assertions.assertEquals(event.getTitle(), storedEvent.getTitle());

        event.setTitle("Concert1");

        var updatedEvent = eventService.update(event);
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

        var storedEvent1 = eventService.update(event1);
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

        var storedEvent = eventDao.save(event);
        Assertions.assertEquals(event.getId(), storedEvent.getId());
        Assertions.assertEquals(event.getDate().getTime(), storedEvent.getDate().getTime());
        Assertions.assertEquals(event.getTitle(), storedEvent.getTitle());

        var wasDeleted = eventService.delete(event.getId());
        Assertions.assertTrue(wasDeleted);
    }
}

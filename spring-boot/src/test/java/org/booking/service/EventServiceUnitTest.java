package org.booking.service;

import org.booking.data.repository.EventRepository;
import org.booking.model.Event;

import org.junit.Test;
import org.junit.jupiter.api.Assertions;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.time.Instant;
import java.time.LocalDate;
import java.util.Date;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.function.Function;

import static org.booking.CommonUtilTest.convertToDate;
import static org.booking.util.IdentifierGenerator.generateId;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
@SpringBootTest(webEnvironment = WebEnvironment.NONE)
public class EventServiceUnitTest {

    @Mock
    private EventRepository eventDao;

    @Mock
    private Page<Event> eventPage;

    @InjectMocks
    private EventService eventService;

    @Test
    public void testCreateEvent() {
        var event = new Event();
        event.setId(generateId());
        event.setDate(Date.from(Instant.now()));
        event.setTitle("Event1");

        when(eventDao.save(event)).thenReturn(event);

        var storedEvent = eventService.create(event);
        Assertions.assertEquals(event.getId(), storedEvent.getId());
        Assertions.assertEquals(event.getDate().getTime(), storedEvent.getDate().getTime());
        Assertions.assertEquals(event.getTitle(), storedEvent.getTitle());
    }

    @Test
    public void testGetById() {
        var id = generateId();
        var event = new Event();
        event.setId(id);
        event.setDate(Date.from(Instant.now()));
        event.setTitle("Event1");

        when(eventDao.findById(id)).thenReturn(Optional.of(event));
        var eventResult = eventService.getEventById(id);

        Assertions.assertEquals(id, eventResult.getId());
    }

    @Test
    public void testGetByIdIfEventHasNotBeenStored() {
        var id = generateId();

        when(eventDao.findById(id)).thenReturn(Optional.empty());

        Assertions.assertThrows(NoSuchElementException.class, () -> eventService.getEventById(id));
    }


    @Test
    public void testGetEventByTitle() {
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

        var size = 2;
        Function<Integer, Pageable> createPageable = (pageNum) -> PageRequest.of(pageNum, size);

        when(eventDao.findByTitleContaining("Event", createPageable.apply(0)))
                .thenReturn(List.of(event1, event2));
        when(eventDao.findByTitleContaining("Event", createPageable.apply(1)))
                .thenReturn(List.of(event3));
        when(eventDao.findByTitleContaining("Event", createPageable.apply(2)))
                .thenReturn(List.of());

        var eventsPage1 = eventService.getEventsByTitle("Event", 2, 0);
        var eventsPage2 = eventService.getEventsByTitle("Event", 2, 1);
        var eventsPage3 = eventService.getEventsByTitle("Event", 2, 2);

        Assertions.assertEquals(2, eventsPage1.size());
        Assertions.assertEquals(1, eventsPage2.size());
        Assertions.assertEquals(0, eventsPage3.size());
    }

    @Test
    public void testFilterEventForDay() {

        var event1 = new Event();
        event1.setId(generateId());
        event1.setDate(convertToDate(LocalDate.now().plusDays(1)));
        event1.setTitle("Event2");

        var event2 = new Event();
        event2.setId(generateId());
        event2.setDate(convertToDate(LocalDate.now().plusDays(1)));
        event2.setTitle("Concert1");

        var event3 = new Event();
        event3.setId(generateId());
        event3.setDate(convertToDate(LocalDate.now().plusDays(1)));
        event3.setTitle("Concert2");

        var expectedDay = LocalDate.now().plusDays(1);

        var size = 2;
        Function<Integer, Pageable> createPageable = (pageNum) -> PageRequest.of(pageNum, size);

        when(eventDao.findByDate(convertToDate(expectedDay), createPageable.apply(0)))
                .thenReturn(List.of(event1, event2));
        when(eventDao.findByDate(convertToDate(expectedDay), createPageable.apply(1)))
                .thenReturn(List.of(event3));
        when(eventDao.findByDate(convertToDate(expectedDay), createPageable.apply(2)))
                .thenReturn(List.of());

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

        when(eventDao.save(event)).thenReturn(event);

        var updatedEvent = eventService.update(event);
        Assertions.assertEquals(event.getId(), updatedEvent.getId());
        Assertions.assertEquals(event.getDate().getTime(), updatedEvent.getDate().getTime());
        Assertions.assertEquals(event.getTitle(), updatedEvent.getTitle());

    }

    @Test
    public void testDeleteEvent() {
        var id = generateId();

        var wasDeleted = eventService.delete(id);
        Assertions.assertTrue(wasDeleted);
    }
}

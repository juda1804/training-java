package org.booking.service;

import org.booking.data.repository.EventRepository;
import org.booking.model.Event;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.function.BiFunction;

@Service
public class EventService {
    private final Logger LOGGER = LoggerFactory.getLogger(EventService.class);

    private final EventRepository eventRepository;

    private final BiFunction<Integer, Integer, Pageable> createPageable = PageRequest::of;

    @Autowired
    public EventService(EventRepository eventRepository) {
        this.eventRepository = eventRepository;
    }

    /**
     * Gets event by its id.
     * @return Event.
     */
    public Event getEventById(long eventId) {
        return eventRepository.findById(eventId)
                .orElseThrow(() -> {
                    LOGGER.error("User with id: {} cannot be found", eventId);
                    return new NoSuchElementException(String.format("Event with id %s not found", eventId));
                });
    }

    /**
     * Get list of events by matching title. Title is matched using 'contains' approach.
     * In case nothing was found, empty list is returned.
     * @param title Event title or it's part.
     * @param pageSize Pagination param. Number of events to return on a page.
     * @param pageNum Pagination param. Number of the page to return. Starts from 1.
     * @return List of events.
     */
    public List<Event> getEventsByTitle(String title, int pageSize, int pageNum) {
        Pageable pageable = createPageable.apply(pageNum, pageSize);
        return eventRepository.findByTitleContaining(title, pageable);
    }

    /**
     * Get list of events for specified day.
     * In case nothing was found, empty list is returned.
     * @param day Date object from which day information is extracted.
     * @param pageSize Pagination param. Number of events to return on a page.
     * @param pageNum Pagination param. Number of the page to return. Starts from 1.
     * @return List of events.
     */
    public List<Event> getEventsForDay(Date day, int pageSize, int pageNum) {
        Pageable pageable = createPageable.apply(pageNum, pageSize);
        return eventRepository.findByDate(day, pageable).stream().toList();
    }

    /**
     * Creates new event. Event id should be auto-generated.
     * @param event Event data.
     * @return Created Event object.
     */
    public Event create(Event event) {
        LOGGER.info("Saving event with id: {}", event.getId());
        return eventRepository.save(event);
    }

    /**
     * Updates event using given data.
     * @param event Event data for update. Should have id set.
     * @return Updated Event object.
     */
    public Event update(Event event) {
        LOGGER.info("Updating ticket with id: {}", event.getId());
        return eventRepository.save(event);
    }

    /**
     * Deletes event by its id.
     * @param eventId Event id.
     * @return Flag that shows whether event has been deleted.
     */
    public boolean delete(long eventId) {
        LOGGER.info("Deleting ticket with id: {}", eventId);

        try {
            eventRepository.deleteById(eventId);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

}

package app.service;

import app.dao.EventRepository;
import app.domain.Event;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class EventService {
    private final EventRepository eventRepository;

    public EventService(EventRepository eventRepository) {
        this.eventRepository = eventRepository;
    }

    public Event createEvent(Event event) {
        log.info("Creating new event {}", event);
        eventRepository.save(event);
        return event;
    }

    public Event getEventById(Long id) {
        log.info("Retrieving event by id {}", id);
        return eventRepository.findEventById(id);
    }

}


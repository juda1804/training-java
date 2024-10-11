package app.service;

import app.dao.EventDao;
import app.domain.Event;
import java.util.List;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class EventService {
    private final EventDao eventDao;

    public EventService(EventDao eventDao) {
        this.eventDao = eventDao;
    }

    public Event createEvent(Event event) {
        log.info("Creating new event {}", event);
        eventDao.saveEvent(event);
        return event;
    }

    public Event getEventById(Long id) {
        log.info("Retrieving event by id {}", id);
        return eventDao.findEventById(id);
    }


}


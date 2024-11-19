package org.booking.data.repository;

import org.booking.model.Event;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.*;

@Repository
public interface EventRepository extends JpaRepository<Event, Long> {
    /**
     * Get list of events by matching title. Title is matched using 'contains' approach.
     * In case nothing was found, empty list is returned.
     * @param title Event title or it's part.
     * @param pageable Pagination param.
     * @return List of events.
     */
    List<Event> findByTitleContaining(String title, Pageable pageable);

    /**
     * Get list of events for specified day.
     * In case nothing was found, empty list is returned.
     * @param date Date object from which day information is extracted.
     * @param pageable Pagination param.
     * @return List of events.
     */
    List<Event> findByDate(Date date, Pageable pageable);
}

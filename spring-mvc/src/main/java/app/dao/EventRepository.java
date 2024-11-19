package app.dao;

import app.domain.Event;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface EventRepository extends PagingAndSortingRepository<Event, Long> {
    Event findEventById(Long id);

    Event save(Event event);

    Page<Event> findAll(Pageable pageable);
}
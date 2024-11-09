package app.dao;

import app.domain.Ticket;
import app.domain.TicketCategory;
import java.util.List;
import java.util.Optional;

import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.stereotype.Repository;


@Repository
public interface TicketRepository extends PagingAndSortingRepository<Ticket, Long> {
    List<Ticket> findAll();

    Ticket save(Ticket ticket);
    Optional<Ticket> findById(Long id);

    Optional<Ticket> findByEventIdAndPlaceAndCategory(Long eventId, int place, TicketCategory category);
}

package app.dao;

import app.domain.Ticket;
import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.stereotype.Repository;


@Repository
public interface TicketRepository extends PagingAndSortingRepository<Ticket, Long> {
    List<Ticket> findAll();

    Page<Ticket> findAllByUserId(Long userId, Pageable pageable);

    Ticket save(Ticket ticket);
    Optional<Ticket> findById(Long id);
}

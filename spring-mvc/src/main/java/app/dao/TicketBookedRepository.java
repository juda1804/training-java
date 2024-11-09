package app.dao;

import app.domain.TicketBooked;
import java.util.List;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.stereotype.Repository;


@Repository
public interface TicketBookedRepository extends PagingAndSortingRepository<TicketBooked, Long> {
    List<TicketBooked> findAll();

    Page<TicketBooked> findAllByUserId(Long userId, Pageable pageable);

    TicketBooked save(TicketBooked ticketBooked);
    Optional<TicketBooked> findById(Long id);
}

package org.booking.data.repository;

import org.booking.model.Ticket;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.*;

@Repository
public interface TicketRepository extends JpaRepository<Ticket, Long> {

    /**
     * Get all booked tickets for specified user.
     * @param userId User id
     * @param pageable Pagination param.
     * @return List of Ticket objects.
     */
    List<Ticket> findByUserId(long userId, Pageable pageable);

    /**
     * Get all booked tickets for specified event. Tickets should be sorted in by user email in ascending order.
     * @param eventId Event ID
     * @param pageable Pagination param.
     * @return List of Ticket objects.
     */
    List<Ticket> findByEventId(long eventId, Pageable pageable);

    /**
     * Get all booked tickets for specified event. Tickets should be sorted in by user email in ascending order.
     * @param eventId Event ID
     * @return List of Ticket objects.
     */
    List<Ticket> findByEventId(long eventId);

}

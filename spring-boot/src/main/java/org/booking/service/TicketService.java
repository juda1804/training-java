package org.booking.service;

import org.booking.data.repository.TicketRepository;
import org.booking.model.Ticket;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.function.BiFunction;

@Service
public class TicketService {
    private final Logger LOGGER = LoggerFactory.getLogger(TicketService.class);

    private final TicketRepository ticketRepository;

    private final BiFunction<Integer, Integer, Pageable> createPageable = PageRequest::of;

    @Autowired
    public TicketService(TicketRepository ticketRepository) {
        this.ticketRepository = ticketRepository;
    }

    /**
     * Book ticket for a specified event on behalf of specified user.
     * @param ticket Ticket object.
     * @return Booked ticket object.
     * @throws java.lang.IllegalStateException if this place has already been booked.
     */
    public Ticket book(Ticket ticket) {
        LOGGER.info("Saving ticket with id: {}", ticket.getId());
        return ticketRepository.save(ticket);
    }

    /**
     * Get all booked tickets for specified user.
     * @param userId User
     * @param pageSize Pagination param. Number of tickets to return on a page.
     * @param pageNum Pagination param. Number of the page to return. Starts from 0.
     * @return List of Ticket objects.
     */
    public List<Ticket> getBookedTicketsByUserId(long userId, int pageSize, int pageNum) {
        Pageable pageable = createPageable.apply(pageNum, pageSize);
        return ticketRepository.findByUserId(userId, pageable);
    }

    /**
     * Get all booked tickets for specified event.
     * @param eventId Event
     * @param pageSize Pagination param. Number of tickets to return on a page.
     * @param pageNum Pagination param. Number of the page to return. Starts from 0.
     * @return List of Ticket objects.
     */
    public List<Ticket> getBookedTicketsByEventId(long eventId, int pageSize, int pageNum) {
        Pageable pageable = createPageable.apply(pageNum, pageSize);
        return ticketRepository.findByEventId(eventId, pageable);
    }

    /**
     * Get all booked tickets for specified event.
     * @param eventId Event
     * @return List of Ticket objects.
     */
    public List<Ticket> getBookedTicketsByEventId(long eventId) {
        return ticketRepository.findByEventId(eventId);
    }

    /**
     * Cancel ticket with a specified id.
     * @param ticketId Ticket id.
     * @return Flag whether anything has been canceled.
     */
    public boolean cancel(long ticketId) {
        LOGGER.info("Deleting ticket with id: {}", ticketId);
        try {
            ticketRepository.deleteById(ticketId);
            return true;
        } catch (Exception e) {
            return false;
        }
    }
}

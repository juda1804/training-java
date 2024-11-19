package org.booking.jms;

import org.booking.facade.BookingFacade;
import org.booking.model.Ticket;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jms.annotation.JmsListener;
import org.springframework.stereotype.Component;

@Component
public class BookingReceiver {
    private final Logger LOGGER = LoggerFactory.getLogger(BookingReceiver.class);

    @Autowired
    private BookingFacade bookingFacade;

    @JmsListener(destination = "${jms.message.destination}")
    public void bookTicket(Ticket ticket) {
        LOGGER.info("Booking ticket for event with id: {} and user id: {} in place: {} with category: {}",
                ticket.getEventId(), ticket.getUserId(), ticket.getPlace(), ticket.getCategory());

        try {
            bookingFacade.bookTicket(ticket.getUserId(), ticket.getEventId(), ticket.getPlace(), ticket.getCategory());
        } catch (Exception ex) {
            LOGGER.error(String.format("Ticket has not been booked due to exception: %s", ex.getMessage()), ex);
            throw ex;
        }
    }
}

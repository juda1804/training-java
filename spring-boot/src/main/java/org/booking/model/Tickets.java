package org.booking.model;

import com.thoughtworks.xstream.annotations.XStreamImplicit;

import java.util.List;

public class Tickets {

    //this is for serialize and deserialize XML
    @XStreamImplicit
    private List<Ticket> ticket;

    public List<Ticket> getTicket() {
        return ticket;
    }

    public void setTicket(List<Ticket> ticket) {
        this.ticket = ticket;
    }
}

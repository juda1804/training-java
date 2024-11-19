package org.booking.model;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.thoughtworks.xstream.annotations.XStreamAsAttribute;
import jakarta.persistence.*;

import java.util.Date;

@Entity(name = "events")
public class Event {

    @Id
    @XStreamAsAttribute
    private long id;

    @Column(name = "title")
    @XStreamAsAttribute
    private String title;

    @Column(name = "dt")
    @XStreamAsAttribute
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
    private Date date;

    @Column(name = "ticket_price")
    @XStreamAsAttribute
    private double ticketPrice;

    public long getId() {
        return this.id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getTitle() {
        return this.title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public Date getDate() {
        return this.date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public double getTicketPrice() {
        return ticketPrice;
    }

    public void setTicketPrice(double ticketPrice) {
        this.ticketPrice = ticketPrice;
    }
}

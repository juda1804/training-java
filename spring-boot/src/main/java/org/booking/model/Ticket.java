package org.booking.model;

import com.thoughtworks.xstream.annotations.XStreamAsAttribute;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import java.io.Serializable;

@Entity(name = "tickets")
public class Ticket implements Serializable {
    public enum Category {STANDARD, PREMIUM, BAR}

    @Id
    @XStreamAsAttribute
    private long id;

    @Column(name = "event_id")
    @XStreamAsAttribute
    private long eventId;

    @Column(name = "user_id")
    @XStreamAsAttribute
    private long userId;

    @Column(name = "category")
    @XStreamAsAttribute
    private Category category;

    @Column(name = "place")
    @XStreamAsAttribute
    private int place;

    public long getId() {
        return this.id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public long getEventId() {
        return this.eventId;
    }

    public void setEventId(long eventId) {
        this.eventId = eventId;
    }

    public long getUserId() {
        return this.userId;
    }

    public void setUserId(long userId) {
        this.userId = userId;
    }

    public Category getCategory() {
        return this.category;
    }

    public void setCategory(Category category) {
        this.category = category;
    }

    public int getPlace() {
        return this.place;
    }

    public void setPlace(int place) {
        this.place = place;
    }
}

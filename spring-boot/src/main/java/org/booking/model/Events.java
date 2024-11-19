package org.booking.model;

import com.thoughtworks.xstream.annotations.XStreamImplicit;

import java.util.List;

public class Events {
    @XStreamImplicit
    private List<Event> event;

    public List<Event> getEvent() {
        return event;
    }

    public void setEvent(List<Event> event) {
        this.event = event;
    }
}

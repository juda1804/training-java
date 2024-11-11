package org.booking.model;

import com.thoughtworks.xstream.annotations.XStreamImplicit;

import java.util.List;

public class Users {

    @XStreamImplicit
    private List<User> user;

    public List<User> getUser() {
        return user;
    }

    public void setUser(List<User> user) {
        this.user = user;
    }
}

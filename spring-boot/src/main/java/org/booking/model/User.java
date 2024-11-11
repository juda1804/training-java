package org.booking.model;

import com.thoughtworks.xstream.annotations.XStreamAsAttribute;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;

@Entity(name = "users")
public class User {

    @Id
    @XStreamAsAttribute
    private long id;

    @XStreamAsAttribute
    private String name;

    @XStreamAsAttribute
    private String email;

    public long getId() {
        return this.id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getName() {
        return this.name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return this.email;
    }

    public void setEmail(String email) {
        this.email = email;
    }
}

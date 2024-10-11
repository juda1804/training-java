package app.dao;

import app.domain.Event;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Repository
public class EventDao {
    private final StorageBean storageBean;

    public EventDao(StorageBean storageBean) {
        this.storageBean = storageBean;
    }

    public Event findEventById(Long id) {
        return (Event) storageBean.getStorage("event").get(id);
    }

    public void saveEvent(Event event) {
        storageBean.addToStorage("event", event.getId(), event);
    }
}
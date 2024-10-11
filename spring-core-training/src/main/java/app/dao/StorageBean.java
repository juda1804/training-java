package app.dao;

import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Repository;

import java.util.HashMap;
import java.util.Map;

@Repository
@Order(1)
public class StorageBean {
    private final Map<String, Map<Long, Object>> storage;

    public StorageBean() {
        storage = Map.of(
                "user", new HashMap<>(),
                "event", new HashMap<>(),
                "ticket", new HashMap<>()
        );
    }

    public Map<Long, Object> getStorage(String namespace) {
        return storage.get(namespace);
    }

    public void addToStorage(String namespace, Long id, Object entity) {
        storage.get(namespace).put(id, entity);
    }
}
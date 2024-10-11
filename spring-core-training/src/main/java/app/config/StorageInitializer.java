package app.config;

import app.dao.StorageBean;
import app.domain.Event;
import app.domain.Ticket;
import app.domain.User;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;
import java.util.Map;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.beans.factory.annotation.Value;

@Configuration
public class StorageInitializer implements InitializingBean {
    private final Map<String, User> userStorage;
    private final Map<String, Event> eventStorage;
    private final Map<String, Ticket> ticketStorage;

    @Value("${data.file.path}")
    private String filePath;

    @Autowired
    private ObjectMapper  mapper;

    @Autowired
    private StorageBean storageBean;

    public StorageInitializer(
            Map<String, User> userStorage,
            Map<String, Event> eventStorage,
            Map<String, Ticket> ticketStorage) {
        this.userStorage = userStorage;
        this.eventStorage = eventStorage;
        this.ticketStorage = ticketStorage;
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        List<String> strings = Files.readAllLines(Paths.get(filePath));

        for (String stringObjects : strings) {
            JsonNode rootNode = mapper.readTree(stringObjects);
            if (rootNode.has("event")) {
                JsonNode eventNode = rootNode.get("event");
                Event event = mapper.treeToValue(eventNode, Event.class);
                storageBean.addToStorage("event", event.getId(), event);
            } else if (rootNode.has("ticket")) {
                JsonNode ticketNode = rootNode.get("ticket");
                Ticket ticket = mapper.treeToValue(ticketNode, Ticket.class);
                storageBean.addToStorage("ticket", ticket.getId(), ticket);
            } else if (rootNode.has("user")) {
                JsonNode userNode = rootNode.get("user");
                User user = mapper.treeToValue(userNode, User.class);
                storageBean.addToStorage("user", user.getId(), user);
            }
        }
    }
}




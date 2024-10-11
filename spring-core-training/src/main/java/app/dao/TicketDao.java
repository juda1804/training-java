package app.dao;

import app.domain.Ticket;
import org.springframework.stereotype.Repository;

import java.util.*;


@Repository
public class TicketDao {
    private final StorageBean storageBean;

    public TicketDao(StorageBean storageBean) {
        this.storageBean = storageBean;
    }

    public Ticket findTicketById(Long id) {
        return (Ticket) storageBean.getStorage("ticket").get(id);
    }

    public void saveTicket(Ticket ticket) {
        storageBean.addToStorage("ticket", ticket.getId(), ticket);
    }
}

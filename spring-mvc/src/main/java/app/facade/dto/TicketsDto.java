package app.facade.dto;

import app.domain.Ticket;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlRootElement;
import java.util.List;

@XmlRootElement(name = "tickets")
public class TicketsDto {

    private List<TicketDto> tickets;

    @XmlElement(name = "ticket")
    public List<TicketDto> getTickets() {
        return tickets;
    }

    public void setTickets(List<TicketDto> tickets) {
        this.tickets = tickets;
    }

    @Override
    public String toString() {
        return "TicketsDto{" +
                "tickets=" + tickets +
                '}';
    }
}

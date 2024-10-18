package app.domain;

import java.io.Serializable;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Table;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Table("TICKETS")
public class Ticket implements Serializable {
    @Id
    private Long id;
    private Long userId;
    private Long eventId;
    private int place;
    private TicketCategory category;
}
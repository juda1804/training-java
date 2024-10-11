package app.domain;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public class Ticket {
    private Long id;
    private Long userId;
    private Long eventId;
    private int place;
    private TicketCategory category;
}
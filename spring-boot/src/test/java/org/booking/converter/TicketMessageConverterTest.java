package org.booking.converter;

import jakarta.jms.JMSException;
import jakarta.jms.Session;
import jakarta.jms.TextMessage;
import org.booking.model.Ticket;
import org.junit.jupiter.api.Assertions;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.boot.test.context.SpringBootTest;

import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.NONE)
public class TicketMessageConverterTest {

    @Mock
    private Session session;

    @Mock
    private TextMessage textMessage;

    private final TicketMessageConverter ticketMessageConverter = new TicketMessageConverter();

    @Test
    public void testToMessage() throws JMSException {
        var ticket = new Ticket();
        ticket.setUserId(123456L);
        ticket.setEventId(5755421L);
        ticket.setPlace(10);
        ticket.setCategory(Ticket.Category.STANDARD);

        var expectedJson = """
                {"id":0,"eventId":5755421,"userId":123456,"category":"STANDARD","place":10}""";

        when(session.createTextMessage(expectedJson))
                .thenReturn(textMessage);

        var message = ticketMessageConverter.toMessage(ticket, session);

        Assertions.assertEquals(textMessage, message);
    }

    @Test
    public void testFromMessage() throws JMSException {
        var expectedTicket = new Ticket();
        expectedTicket.setUserId(123456L);
        expectedTicket.setEventId(5755421L);
        expectedTicket.setPlace(10);
        expectedTicket.setCategory(Ticket.Category.STANDARD);

        var ticketJson = """
                {"id":0,"eventId":5755421,"userId":123456,"category":"STANDARD","place":10}""";

        when(textMessage.getText())
                .thenReturn(ticketJson);

        var message = ticketMessageConverter.fromMessage(textMessage);

        Assertions.assertInstanceOf(Ticket.class, message);

        var ticket = (Ticket) message;

        Assertions.assertEquals(expectedTicket.getUserId(), ticket.getUserId());
        Assertions.assertEquals(expectedTicket.getEventId(), ticket.getEventId());
        Assertions.assertEquals(expectedTicket.getCategory(), ticket.getCategory());
        Assertions.assertEquals(expectedTicket.getPlace(), ticket.getPlace());
    }

}

package org.booking.converter;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.jms.JMSException;
import jakarta.jms.Message;
import jakarta.jms.Session;
import jakarta.jms.TextMessage;
import org.booking.model.Ticket;
import org.springframework.jms.support.converter.MessageConversionException;
import org.springframework.jms.support.converter.MessageConverter;

public class TicketMessageConverter implements MessageConverter {

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    public Message toMessage(Object object, Session session) throws JMSException, MessageConversionException {
        try {
            var ticket = (Ticket) object;
            var ticketJson = objectMapper.writeValueAsString(ticket);
            return session.createTextMessage(ticketJson);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public Object fromMessage(Message message) throws JMSException, MessageConversionException {
        try {
            var textMessage = (TextMessage) message;
            return objectMapper.readValue(textMessage.getText(), Ticket.class);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }
}

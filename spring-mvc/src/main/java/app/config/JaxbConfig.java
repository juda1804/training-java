package app.config;

import app.facade.dto.TicketsDto;
import jakarta.xml.bind.JAXBContext;
import jakarta.xml.bind.JAXBException;
import jakarta.xml.bind.Unmarshaller;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class JaxbConfig {

    @Bean
    public Unmarshaller jaxbUnmarshaller() throws JAXBException {
        JAXBContext jaxbContext = JAXBContext.newInstance(TicketsDto.class);
        return jaxbContext.createUnmarshaller();
    }
}

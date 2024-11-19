package org.booking.service;

import com.thoughtworks.xstream.converters.basic.DateConverter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.oxm.xstream.XStreamMarshaller;
import org.springframework.stereotype.Service;
import javax.xml.transform.stream.StreamSource;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.function.Function;

@Service
public class ReadDataService {
    private static final Logger LOGGER = LoggerFactory.getLogger(ReadDataService.class);

    private final XStreamMarshaller xstreamMarshaller;

    public ReadDataService(XStreamMarshaller xstreamMarshaller) {
        this.xstreamMarshaller = xstreamMarshaller;
    }

    /**
     * Refill existing user account for a existing user.
     * @param filePath classpath of the xml file.
     * @param extractValue convert collection of objects into an object.
     * @return List of objects that were extracted of the XML.
     */
    public <T, C> List<T> readFile(String filePath, Function<C, List<T>> extractValue) {
        try(InputStream inputStream = ReadDataService.class.getClassLoader().getResourceAsStream(filePath)) {
            xstreamMarshaller.setConverters(new DateConverter("yyyy-MM-dd", new String[]{ "HHmmss" }));

            return extractValue.apply((C) xstreamMarshaller.unmarshal(new StreamSource(inputStream)));
        } catch (IOException | ClassCastException ex){
            LOGGER.error("Objects cannot be loaded", ex);
            return List.of();
        }
    }
}
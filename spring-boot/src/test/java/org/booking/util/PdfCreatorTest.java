package org.booking.util;

import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfReader;
import com.itextpdf.kernel.pdf.canvas.parser.PdfTextExtractor;
import org.booking.model.Book;
import org.booking.model.Event;
import org.booking.model.Ticket;
import org.booking.model.User;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.io.ByteArrayInputStream;
import java.time.LocalDate;
import java.util.List;

import static org.booking.CommonUtilTest.convertToDate;
import static org.booking.util.IdentifierGenerator.generateId;

public class PdfCreatorTest {
    @Test
    public void testCreatePdf() {
        var user1 = new User();
        user1.setId(generateId());
        user1.setName("Maria");
        user1.setEmail("maria@email.com");

        var event1 = new Event();
        event1.setId(generateId());
        event1.setTitle("Romulus");
        event1.setDate(convertToDate(LocalDate.now()));
        event1.setTicketPrice(20.0);

        var event2 = new Event();
        event2.setId(generateId());
        event2.setTitle("Resident");
        event2.setDate(convertToDate(LocalDate.now()));
        event2.setTicketPrice(10.0);

        var ticket1 = new Ticket();
        ticket1.setId(generateId());
        ticket1.setUserId(user1.getId());
        ticket1.setEventId(event1.getId());
        ticket1.setCategory(Ticket.Category.STANDARD);
        ticket1.setPlace(1);

        var ticket2 = new Ticket();
        ticket2.setId(generateId());
        ticket2.setUserId(user1.getId());
        ticket2.setEventId(event2.getId());
        ticket2.setCategory(Ticket.Category.STANDARD);
        ticket2.setPlace(2);

        var books = List.of(
                new Book(ticket1, event1, user1),
                new Book(ticket2, event2, user1)
        );

        var pdf = PdfCreator.createPdf(books);

        try (var is = new ByteArrayInputStream(pdf);
             var reader = new PdfReader(is)) {
            var document = new PdfDocument(reader);
            document.getNumberOfPages();
            var page = PdfTextExtractor.getTextFromPage(document.getFirstPage());

            System.out.println(page.contains(user1.getName()));

            Assertions.assertTrue(pdf.length > 0);
            Assertions.assertEquals(1, document.getNumberOfPages());

            Assertions.assertTrue(page.contains(String.valueOf(ticket1.getId())));
            Assertions.assertTrue(page.contains(event1.getTitle()));
            Assertions.assertTrue(page.contains(String.valueOf(ticket1.getPlace())));
            Assertions.assertTrue(page.contains(String.valueOf(ticket1.getCategory())));

            Assertions.assertTrue(page.contains(String.valueOf(ticket2.getId())));
            Assertions.assertTrue(page.contains(event2.getTitle()));
            Assertions.assertTrue(page.contains(String.valueOf(ticket2.getPlace())));
            Assertions.assertTrue(page.contains(String.valueOf(ticket2.getCategory())));
        } catch (Exception e) {
            Assertions.fail(e);
        }
    }

    @Test
    public void testCreatePdfIfBookListIsEmpty() {
        var books = List.<Book>of();

        var pdf = PdfCreator.createPdf(books);

        try (var is = new ByteArrayInputStream(pdf);
             var reader = new PdfReader(is)) {
            var document = new PdfDocument(reader);
            document.getNumberOfPages();
            var page = PdfTextExtractor.getTextFromPage(document.getFirstPage());

            Assertions.assertTrue(pdf.length > 0);
            Assertions.assertEquals(1, document.getNumberOfPages());

            Assertions.assertTrue(page.contains("Tickets have not been booked for the user"));
        } catch (Exception e) {
            Assertions.fail(e);
        }
    }
}

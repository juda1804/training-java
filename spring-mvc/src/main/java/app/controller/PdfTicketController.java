package app.controller;

import app.domain.Ticket;
import app.facade.BookingFacade;
import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.pdf.PdfWriter;
import java.util.List;
import org.apache.tomcat.util.http.fileupload.ByteArrayOutputStream;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;

@Controller
public class PdfTicketController {
    private final BookingFacade bookingFacade;

    public PdfTicketController(BookingFacade bookingFacade) {
        this.bookingFacade = bookingFacade;
    }

    @GetMapping("/tickets/pdf")
    public ResponseEntity<byte[]> getTicketsPdf(@RequestHeader("Accept") String acceptHeader) {
        List<Ticket> allTickets = bookingFacade.getAllTickets();

        if (allTickets.isEmpty()) {
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        }

        if ("application/pdf".equals(acceptHeader)) {
            try {
                // Create a PDF document

                ByteArrayOutputStream baos = new ByteArrayOutputStream();
                writePdfOnByteArray(baos, allTickets);

                HttpHeaders headers = new HttpHeaders();
                headers.set(HttpHeaders.CONTENT_DISPOSITION, "inline; filename=tickets.pdf");
                headers.set(HttpHeaders.CONTENT_TYPE, "application/pdf");

                return new ResponseEntity<>(baos.toByteArray(), headers, HttpStatus.OK);
            } catch (Exception e) {
                return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
            }
        } else {
            return new ResponseEntity<>(HttpStatus.NOT_ACCEPTABLE);
        }
    }

    private static void writePdfOnByteArray(ByteArrayOutputStream baos, List<Ticket> tickets) throws DocumentException {
        Document document = new Document();
        PdfWriter.getInstance(document, baos);

        document.open();
        document.add(new Paragraph("Booked Tickets"));
        // Add dummy data or real data here

        for (Ticket ticket : tickets) {
            document.add(new Paragraph(ticket.toString()));
        }

        document.close();
    }
}

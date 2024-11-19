package org.booking.util;

import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.element.Cell;
import com.itextpdf.layout.element.Paragraph;
import com.itextpdf.layout.element.Table;
import org.booking.model.Book;

import java.io.ByteArrayOutputStream;
import java.util.List;

public class PdfCreator {
    public static byte[] createPdf(List<Book> books) {
        var byteArrayOutputStream = new ByteArrayOutputStream();
        var writer = new PdfWriter(byteArrayOutputStream);
        var pdfDocument = new PdfDocument(writer);
        var document = new Document(pdfDocument);

        document.add(new Paragraph("Ticket Details"));

        if (!books.isEmpty()) {
            var table = new Table(6);

            table.addHeaderCell(addCell("ID"));
            table.addHeaderCell(addCell("EVENT"));
            table.addHeaderCell(addCell("DATE"));
            table.addHeaderCell(addCell("USER"));
            table.addHeaderCell(addCell("PLACE"));
            table.addHeaderCell(addCell("CATEGORY"));

            books.forEach(book -> {
                table.addCell(addCell(book.ticket().getId()));
                table.addCell(addCell(book.event().getTitle()));
                table.addCell(addCell(book.event().getDate()));
                table.addCell(addCell(book.user().getName()));
                table.addCell(addCell(book.ticket().getPlace()));
                table.addCell(addCell(book.ticket().getCategory()));
            });

            document.add(table);
        } else {
            document.add(new Paragraph("Tickets have not been booked for the user"));
        }

        document.close();

        return byteArrayOutputStream.toByteArray();
    }

    private static  <T> Cell addCell(T text) {
        return new Cell().add(new Paragraph(String.valueOf(text)));
    }
}

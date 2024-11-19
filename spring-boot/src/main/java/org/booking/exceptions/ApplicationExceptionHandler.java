package org.booking.exceptions;

import org.booking.restcontroller.dto.BookingResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.util.NoSuchElementException;

@ControllerAdvice
public class ApplicationExceptionHandler {
    private final Logger LOGGER = LoggerFactory.getLogger(ApplicationExceptionHandler.class);

    @ExceptionHandler({NoSuchElementException.class, IllegalArgumentException.class, IllegalStateException.class})
    public ResponseEntity<BookingResponse> handleApplicationException(Exception ex) {
        var errorMessage = ex.getMessage();

        LOGGER.error(String.format("Application exception: %s", errorMessage), ex);

        return ResponseEntity.badRequest()
                .body(new BookingResponse(errorMessage));
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<BookingResponse> handleException(Exception ex) {
        LOGGER.error(String.format("Server error: %s", ex.getMessage()), ex);

        return ResponseEntity.internalServerError()
                .body(new BookingResponse("Server error"));
    }

}

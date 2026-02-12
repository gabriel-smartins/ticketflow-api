package com.ticketflow.api.config;

import com.ticketflow.api.event.exception.EventNotFoundException;
import com.ticketflow.api.event.exception.ExceedsTotalSpotsException;
import com.ticketflow.api.event.exception.NotEnoughSpotsException;
import com.ticketflow.api.ticket.exception.TicketAlreadyCanceledException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.net.URI;
import java.time.Instant;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(EventNotFoundException.class)
    public ProblemDetail handleNotFoundException(EventNotFoundException e) {
        ProblemDetail problemDetail = ProblemDetail.forStatusAndDetail(HttpStatus.NOT_FOUND, e.getMessage());
        problemDetail.setTitle("Event not found");
        problemDetail.setType(URI.create("https://ticketflow.com/errors/event-not-found"));
        problemDetail.setProperty("timestamp", Instant.now());
        return problemDetail;
    }

    @ExceptionHandler(NotEnoughSpotsException.class)
    public ProblemDetail handleNotEnoughSpots(NotEnoughSpotsException e) {
        ProblemDetail problemDetail = ProblemDetail.forStatusAndDetail(HttpStatus.CONFLICT, e.getMessage());
        problemDetail.setTitle("Business Rule Violation");
        problemDetail.setType(URI.create("https://ticketflow.com/errors/not-enough-spots"));
        problemDetail.setProperty("timestamp", Instant.now());
        return problemDetail;
    }

    @ExceptionHandler(ExceedsTotalSpotsException.class)
    public ProblemDetail handleExceedsTotalSpots(ExceedsTotalSpotsException e) {
        ProblemDetail problemDetail = ProblemDetail.forStatusAndDetail(HttpStatus.UNPROCESSABLE_ENTITY, e.getMessage());
        problemDetail.setTitle("Inventory Limit Exceeded");
        problemDetail.setType(URI.create("https://ticketflow.com/errors/exceeds-total-spots"));
        problemDetail.setProperty("timestamp", Instant.now());

        return problemDetail;
    }

    @ExceptionHandler(TicketAlreadyCanceledException.class)
    public ProblemDetail handleTicketAlreadyCanceled(TicketAlreadyCanceledException e) {
        ProblemDetail problemDetail = ProblemDetail.forStatusAndDetail(HttpStatus.CONFLICT, e.getMessage());
        problemDetail.setTitle("Ticket already canceled.");
        problemDetail.setType(URI.create("https://ticketflow.com/errors/ticket-already-canceled"));
        problemDetail.setProperty("timestamp", Instant.now());

        return problemDetail;
    }
}

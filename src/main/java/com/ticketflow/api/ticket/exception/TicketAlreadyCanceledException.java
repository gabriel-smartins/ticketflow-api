package com.ticketflow.api.ticket.exception;

public class TicketAlreadyCanceledException extends RuntimeException {
    public TicketAlreadyCanceledException(String message) {
        super(message);
    }
}

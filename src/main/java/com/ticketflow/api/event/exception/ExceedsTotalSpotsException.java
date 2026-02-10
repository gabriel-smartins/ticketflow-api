package com.ticketflow.api.event.exception;

public class ExceedsTotalSpotsException extends RuntimeException {
    public ExceedsTotalSpotsException(String message) {
        super(message);
    }
}

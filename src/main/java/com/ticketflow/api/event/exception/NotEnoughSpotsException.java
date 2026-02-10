package com.ticketflow.api.event.exception;

public class NotEnoughSpotsException extends RuntimeException {
    public NotEnoughSpotsException(String message) {
        super(message);
    }
}

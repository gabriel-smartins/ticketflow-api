package com.ticketflow.api.event;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
public class EventService {
    private final EventRepository eventRepository;

    public EventService(EventRepository eventRepository, Event event) {
        this.eventRepository = eventRepository;
    }

    @Transactional
    public Event buyTicket(int quantity, UUID eventId) {

        var event = eventRepository.findByIdWithLock(eventId)
                .orElseThrow(() -> new IllegalArgumentException("Event not found with ID: " + eventId));

        event.decreaseSpots(quantity);

        return eventRepository.save(event);

    }
}

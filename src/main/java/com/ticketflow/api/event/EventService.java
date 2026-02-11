package com.ticketflow.api.event;

import com.ticketflow.api.event.dto.CreateEventRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
public class EventService {
    private final EventRepository eventRepository;

    public EventService(EventRepository eventRepository) {
        this.eventRepository = eventRepository;
    }

    @Transactional
    public Event createEvent(CreateEventRequest data) {
        Event newEvent = Event.builder()
                .title(data.title())
                .description(data.description())
                .date(data.date())
                .location(data.location())
                .totalSpots(data.totalSpots())
                .availableSpots(data.totalSpots())
                .price(data.price())
                .build();

        return eventRepository.save(newEvent);
    }

    @Transactional
    public Event buyTicket(int quantity, UUID eventId) {

        var event = eventRepository.findByIdWithLock(eventId)
                .orElseThrow(() -> new IllegalArgumentException("Event not found with ID: " + eventId));

        event.decreaseSpots(quantity);

        return eventRepository.save(event);

    }
}

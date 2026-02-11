package com.ticketflow.api.event;

import com.ticketflow.api.event.dto.CreateEventRequestDTO;
import com.ticketflow.api.event.exception.EventNotFoundException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
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
    public Event createEvent(CreateEventRequestDTO data) {
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

    @Transactional(readOnly = true)
    public Page<Event> fetchEvents(Pageable pageable) {
        return eventRepository.findAll(pageable);
    }

    @Transactional(readOnly = true)
    public Event getEventDetails(UUID eventId) {
        return eventRepository.findById(eventId)
                .orElseThrow(() -> new EventNotFoundException("Event not Found with ID: " + eventId));
    }

    @Transactional
    public Event buyTicket(int quantity, UUID eventId) {

        var event = eventRepository.findByIdWithLock(eventId)
                .orElseThrow(() -> new EventNotFoundException("Event not found with ID: " + eventId));

        event.decreaseSpots(quantity);

        return eventRepository.save(event);

    }

    @Transactional
    public void refundTicket(UUID eventId) {

        var event = eventRepository.findById(eventId)
                .orElseThrow(() -> new EventNotFoundException("Event not found with ID: " + eventId));

        event.increaseSpots(1);

        eventRepository.save(event);
    }
}

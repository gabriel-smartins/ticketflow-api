package com.ticketflow.api.event;

import com.ticketflow.api.config.CustomPage;
import com.ticketflow.api.event.dto.CreateEventRequestDTO;
import com.ticketflow.api.event.exception.EventNotFoundException;
import com.ticketflow.api.ticket.Ticket;
import com.ticketflow.api.ticket.TicketRepository;
import com.ticketflow.api.ticket.exception.TicketNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class EventService {
    private final EventRepository eventRepository;
    private final TicketRepository ticketRepository;

    @Transactional
    @CacheEvict(value = "eventsCache", allEntries = true)
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
    @Cacheable(value = "eventsCache")
    public Page<Event> fetchEvents(Pageable pageable) {
        Page<Event> page = eventRepository.findAll(pageable);

        return new CustomPage<>(page.getContent(), pageable, page.getTotalElements());
    }

    @Transactional(readOnly = true)
    public Event getEventDetails(UUID eventId) {
        return eventRepository.findById(eventId)
                .orElseThrow(() -> new EventNotFoundException("Event not Found with ID: " + eventId));
    }

    @Transactional
    @CacheEvict(value = "eventsCache", allEntries = true)
    public Ticket buyTicket(int quantity, UUID eventId, String customerName, String customerEmail) {

        var event = eventRepository.findByIdWithLock(eventId)
                .orElseThrow(() -> new EventNotFoundException("Event not found with ID: " + eventId));

        event.decreaseSpots(quantity);

        eventRepository.save(event);

        var ticket = Ticket.builder()
                .event(event)
                .customerName(customerName)
                .customerEmail(customerEmail)
                .price(event.getPrice())
                .build();

        return ticketRepository.save(ticket);
    }

    @Transactional
    @CacheEvict(value = "eventsCache", allEntries = true)
    public void refundTicket(UUID ticketId) {

        var ticket = ticketRepository.findById(ticketId)
                .orElseThrow(() -> new TicketNotFoundException("Ticket not found"));

        ticket.cancel();

        var event = eventRepository.findByIdWithLock(ticket.getEvent().getId())
                .orElseThrow(() -> new EventNotFoundException("Event associated with ticket not found"));

        event.increaseSpots(1);
        eventRepository.save(event);
    }
}

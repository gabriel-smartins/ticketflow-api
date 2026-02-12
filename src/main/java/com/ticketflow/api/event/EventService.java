package com.ticketflow.api.event;

import com.ticketflow.api.event.dto.CreateEventRequestDTO;
import com.ticketflow.api.event.exception.EventNotFoundException;
import com.ticketflow.api.ticket.Ticket;
import com.ticketflow.api.ticket.TicketRepository;
import com.ticketflow.api.ticket.exception.TicketNotFoundException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
public class EventService {
    private final EventRepository eventRepository;
    private final TicketRepository ticketRepository;

    public EventService(EventRepository eventRepository, TicketRepository ticketRepository) {
        this.eventRepository = eventRepository;
        this.ticketRepository = ticketRepository;
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

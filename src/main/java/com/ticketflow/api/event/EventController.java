package com.ticketflow.api.event;

import com.ticketflow.api.event.dto.CreateEventRequestDTO;
import com.ticketflow.api.event.dto.EventResponseDTO;
import com.ticketflow.api.event.dto.PurchaseRequestDTO;
import com.ticketflow.api.ticket.Ticket;
import com.ticketflow.api.ticket.dto.TicketResponseDTO;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URI;
import java.util.UUID;

@RestController
@RequestMapping("/events")
public class EventController {

    private final EventService eventService;

    public EventController(EventService eventService) {
        this.eventService = eventService;
    }

    @GetMapping
    public ResponseEntity<Page<EventResponseDTO>> fetchEvents(
            @PageableDefault(size = 10, sort = "date") Pageable pageable) {
        var events = eventService.fetchEvents(pageable);

        var response = events.map(EventResponseDTO::fromEntity);

        return ResponseEntity.ok(response);
    }

    @GetMapping("{eventId}")
    public ResponseEntity<EventResponseDTO> getEventDetails(@PathVariable UUID eventId) {

        var event = eventService.getEventDetails(eventId);

        return ResponseEntity.ok(EventResponseDTO.fromEntity(event));

    }

    @PostMapping
    public ResponseEntity<Event> createEvent(@RequestBody CreateEventRequestDTO request, UriComponentsBuilder uriBuilder) {
        var createdEvent = eventService.createEvent(request);

        URI uri = uriBuilder.path("/events/{id}").buildAndExpand(createdEvent.getId()).toUri();

        return ResponseEntity.created(uri).body(createdEvent);
    }

    @PostMapping("/{eventId}/purchase")
    public ResponseEntity<TicketResponseDTO> buyTicket(@PathVariable UUID eventId, @RequestBody PurchaseRequestDTO request, UriComponentsBuilder uriBuilder) {
        var boughtTicket = eventService.buyTicket(request.quantity(), eventId, request.customerName(), request.customerEmail());

        URI uri = uriBuilder.path("/tickets/{id}").buildAndExpand(boughtTicket.getId()).toUri();

        return ResponseEntity.created(uri).body(TicketResponseDTO.fromEntity(boughtTicket));
    }

    @PostMapping("/{eventId}/refund")
    public ResponseEntity<Void> refundTicket(@PathVariable UUID eventId) {
        eventService.refundTicket(eventId);

        return ResponseEntity.noContent().build();
    }
}

package com.ticketflow.api.event;

import com.ticketflow.api.event.dto.CreateEventRequestDTO;
import com.ticketflow.api.event.dto.EventResponseDTO;
import com.ticketflow.api.event.dto.PurchaseRequestDTO;
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

    @PostMapping
    public ResponseEntity<Event> createEvent(@RequestBody CreateEventRequestDTO request, UriComponentsBuilder uriBuilder) {
        var createdEvent = eventService.createEvent(request);

        URI uri = uriBuilder.path("/events/{id}").buildAndExpand(createdEvent.getId()).toUri();

        return ResponseEntity.created(uri).body(createdEvent);
    }

    @PostMapping("/{eventId}/purchase")
    public ResponseEntity<Event> buyTicket(@PathVariable UUID eventId, @RequestBody PurchaseRequestDTO request) {
        Event updatedEvent = eventService.buyTicket(request.quantity(), eventId);

        return ResponseEntity.ok(updatedEvent);
    }
}

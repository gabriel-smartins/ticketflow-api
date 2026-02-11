package com.ticketflow.api.event;

import com.ticketflow.api.event.dto.CreateEventRequest;
import com.ticketflow.api.event.dto.PurchaseRequest;
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

    @PostMapping
    public ResponseEntity<Event> createEvent(@RequestBody CreateEventRequest request, UriComponentsBuilder uriBuilder){
        var createdEvent = eventService.createEvent(request);

        URI uri = uriBuilder.path("/events/{id}").buildAndExpand(createdEvent.getId()).toUri();

        return ResponseEntity.created(uri).body(createdEvent);
    }

    @PostMapping("/{eventId}/purchase")
    public ResponseEntity<Event> buyTicket(@PathVariable UUID eventId, @RequestBody PurchaseRequest request) {
        Event updatedEvent = eventService.buyTicket(request.quantity(), eventId);

        return ResponseEntity.ok(updatedEvent);
    }
}

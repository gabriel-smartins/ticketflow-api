package com.ticketflow.api.event;

import com.ticketflow.api.event.dto.PurchaseRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/events")
public class EventController {

    private final EventService eventService;

    public EventController(EventService eventService) {
        this.eventService = eventService;
    }

    @PostMapping("/{eventId}/purchase")
    public ResponseEntity<Event> buyTicket(@PathVariable UUID eventId, @RequestBody PurchaseRequest request) {
        Event updatedEvent = eventService.buyTicket(request.quantity(), eventId);

        return ResponseEntity.ok(updatedEvent);
    }
}

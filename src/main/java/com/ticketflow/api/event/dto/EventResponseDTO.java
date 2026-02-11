package com.ticketflow.api.event.dto;

import com.ticketflow.api.event.Event;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

public record EventResponseDTO(
        UUID id,
        String title,
        String description,
        LocalDateTime date,
        String location,
        BigDecimal price,
        int availableSpots
) {
    public static EventResponseDTO fromEntity(Event event) {
        return new EventResponseDTO(
                event.getId(),
                event.getTitle(),
                event.getDescription(),
                event.getDate(),
                event.getLocation(),
                event.getPrice(),
                event.getAvailableSpots()
        );
    }
}

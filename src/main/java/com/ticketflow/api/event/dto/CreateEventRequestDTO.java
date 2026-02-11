package com.ticketflow.api.event.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record CreateEventRequestDTO(
        String title,
        String description,
        LocalDateTime date,
        String location,
        Integer totalSpots,
        BigDecimal price
) {
}

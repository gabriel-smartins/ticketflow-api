package com.ticketflow.api.ticket.dto;

import com.ticketflow.api.ticket.Ticket;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

public record TicketResponseDTO(
        UUID id,
        UUID eventId,
        String customerName,
        BigDecimal price,
        LocalDateTime soldAt,
        String status
) {
    public static TicketResponseDTO fromEntity(Ticket ticket) {
        return new TicketResponseDTO(
                ticket.getId(),
                ticket.getEvent().getId(),
                ticket.getCustomerName(),
                ticket.getPrice(),
                ticket.getSoldAt(),
                ticket.getStatus().name()
        );
    }
}

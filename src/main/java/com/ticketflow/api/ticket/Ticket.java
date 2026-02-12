package com.ticketflow.api.ticket;

import com.ticketflow.api.event.Event;
import com.ticketflow.api.ticket.enums.TicketStatus;
import com.ticketflow.api.ticket.exception.TicketAlreadyCanceledException;
import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "tickets")
@Getter
@Builder(toBuilder = true)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Ticket {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "eventId", nullable = false)
    private Event event;

    private String customerName;
    private String customerEmail;

    @Column(name = "price", nullable = false)
    private BigDecimal price;

    @Column(name = "sold_at")
    private LocalDateTime soldAt;

    @Enumerated(EnumType.STRING)
    private TicketStatus status;

    @PrePersist
    protected void onCreate() {
        this.soldAt = LocalDateTime.now();
        if (this.status == null) this.status = TicketStatus.ACTIVE;
    }

    public void cancel() {
        if (TicketStatus.CANCELED.equals(this.status)) {
            throw new TicketAlreadyCanceledException("Ticket is already canceled");
        }
        this.status = TicketStatus.CANCELED;
    }


}

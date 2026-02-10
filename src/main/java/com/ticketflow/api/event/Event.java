package com.ticketflow.api.event;

import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "events")
@Getter
@Builder(toBuilder = true)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Event {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(nullable = false)
    private String title;

    @Column(columnDefinition = "TEXT")
    private String description;

    @Column(nullable = false)
    private LocalDateTime date;

    @Column(nullable = false)
    private String location;

    @Column(nullable = false)
    private Integer totalSpots;

    @Column(nullable = false)
    private Integer availableSpots;

    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal price;

    @Column(updatable = false)
    private LocalDateTime createdAt;

    @PrePersist
    protected void onCreate() {
        this.createdAt = LocalDateTime.now();
    }

    public void decreaseSpots(int quantity) {
        if (availableSpots < quantity) {
            throw new IllegalArgumentException("Not enough spots available");
        }
        this.availableSpots -= quantity;
    }

    public void increaseSpots(int quantity) {
        if (availableSpots + quantity > totalSpots) {
            throw new IllegalArgumentException("Cannot exceeds total spots");
        }
        this.availableSpots += quantity;
    }

}

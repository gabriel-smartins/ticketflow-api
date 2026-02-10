package com.ticketflow.api.event.integration;

import com.ticketflow.api.config.BaseIntegrationTest;
import com.ticketflow.api.event.Event;
import com.ticketflow.api.event.EventRepository;
import com.ticketflow.api.event.EventService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.concurrent.*;
import java.util.stream.IntStream;

import static org.assertj.core.api.Assertions.assertThat;

public class EventIntegrationTest extends BaseIntegrationTest {

    @Autowired
    private EventService eventService;

    @Autowired
    private EventRepository eventRepository;

    @BeforeEach
    void setup() {
        eventRepository.deleteAll();
    }

    @Test
    @DisplayName("Should prevent overbooking")
    void shouldPreventOverBooking() throws InterruptedException {

        Event event = Event
                .builder()
                .title("High Concurrency Event")
                .description("Testing locks")
                .date(LocalDateTime.now().plusDays(1))
                .location("Stadium")
                .totalSpots(10)
                .availableSpots(10)
                .price(new BigDecimal("100.00"))
                .build();

        eventRepository.save(event);

        List<CompletableFuture<Void>> futures = IntStream.range(0, 100)
                .mapToObj(i -> CompletableFuture.runAsync(() -> {
                    try {
                        eventService.buyTicket(1, event.getId());
                    } catch (Exception e) {
                    }
                }))
                .toList();

        CompletableFuture.allOf(futures.toArray(new CompletableFuture[0])).join();

        Event updatedEvent = eventRepository.findById(event.getId()).orElseThrow();

        assertThat(updatedEvent.getAvailableSpots()).isEqualTo(0);

    }
}

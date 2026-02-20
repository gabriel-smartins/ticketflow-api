package com.ticketflow.api.event.integration;

import com.ticketflow.api.config.BaseIntegrationTest;
import com.ticketflow.api.event.Event;
import com.ticketflow.api.event.EventRepository;
import com.ticketflow.api.event.EventService;
import com.ticketflow.api.ticket.TicketRepository;
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

    @Autowired
    private TicketRepository ticketRepository;

    @BeforeEach
    void setup() {
        ticketRepository.deleteAll();
        eventRepository.deleteAll();
    }

    @Test
    @DisplayName("Should prevent overbooking")
    void shouldPreventOverBooking() throws InterruptedException {

        Event event = Event.builder()
                .title("High Concurrency Event")
                .description("Testing locks")
                .date(LocalDateTime.now().plusDays(1))
                .location("Stadium")
                .totalSpots(10)
                .availableSpots(10)
                .price(new BigDecimal("100.00"))
                .build();

        eventRepository.save(event);

        int numberOfThreads = 100;
        ExecutorService executorService = Executors.newFixedThreadPool(numberOfThreads);
        CountDownLatch latch = new CountDownLatch(1);

        List<? extends Future<?>> futures = IntStream.range(0, numberOfThreads)
                .mapToObj(i -> executorService.submit(() -> {
                    try {
                        latch.await();

                        eventService.buyTicket(1, event.getId(), "User " + i, "user" + i + "@test.com");

                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }))
                .toList();

        latch.countDown();
        for (Future<?> future : futures) {
            try {
                future.get();
            } catch (ExecutionException e) {
            }
        }

        executorService.shutdown();

        Event updatedEvent = eventRepository.findById(event.getId()).orElseThrow();
        long ticketsSold = ticketRepository.count();

        assertThat(updatedEvent.getAvailableSpots()).isEqualTo(0);

        assertThat(ticketsSold).isEqualTo(10);
    }
}
package com.ticketflow.api.event.unit;

import com.ticketflow.api.event.Event;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;

public class EventUnitTest {

    @Test
    @DisplayName("Should decrease spots correctly when available")
    void shouldDecreaseSpots(){
        Event event = Event.builder()
                .totalSpots(10)
                .availableSpots(10)
                .title("Show Teste")
                .date(LocalDateTime.now())
                .location("Local")
                .price(BigDecimal.TEN)
                .build();
        event.decreaseSpots(2);

        assertThat(event.getAvailableSpots()).isEqualTo(8);
    }
}

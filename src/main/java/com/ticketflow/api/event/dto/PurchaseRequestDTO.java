package com.ticketflow.api.event.dto;


import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Positive;

public record PurchaseRequestDTO(
        @Positive(message = "Quantity must be positive")
        @Min(value = 1, message = "Minimun 1 ticket")
        int quantity,

        @NotBlank
        String customerName,

        @Email
        String customerEmail
) {
}

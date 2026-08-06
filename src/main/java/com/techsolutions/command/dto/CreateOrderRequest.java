package com.techsolutions.command.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;

import java.util.List;

/**
 * DTO para crear un nuevo pedido.
 */
public record CreateOrderRequest(

        @NotBlank(message = "El nombre del cliente es obligatorio.")
        String customerName,

        @NotEmpty(message = "El pedido debe tener al menos un ítem.")
        List<String> items,

        @NotNull(message = "El monto total es obligatorio.")
        @Min(value = 1, message = "El total debe ser mayor a cero.")
        Double totalAmount
) {}

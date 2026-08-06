package com.techsolutions.adapter.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

/**
 * DTO de solicitud para procesar un pago mediante un adaptador específico.
 */
public record PaymentRequest(

        @NotBlank(message = "El nombre del adaptador no puede estar vacío.")
        String adapterName,

        @NotNull(message = "El monto es obligatorio.")
        @Min(value = 1, message = "El monto debe ser mayor a cero.")
        Double amount,

        @NotBlank(message = "La moneda no puede estar vacía.")
        String currency
) {}

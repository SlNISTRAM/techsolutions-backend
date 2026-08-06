package com.techsolutions.strategy.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

/**
 * DTO de solicitud para calcular el precio con una estrategia específica.
 */
public record PriceCalculationRequest(

        @NotNull(message = "El precio base es obligatorio.")
        @Min(value = 0, message = "El precio base no puede ser negativo.")
        Double basePrice,

        @NotNull(message = "La cantidad es obligatoria.")
        @Min(value = 1, message = "La cantidad debe ser al menos 1.")
        Integer quantity,

        @NotBlank(message = "El nombre de la estrategia es obligatorio.")
        String strategyName,

        Double discountPercentage,
        Double demandMultiplier,
        Integer bulkThreshold,
        Double taxRate
) {}

package com.techsolutions.observer.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

/**
 * DTO para registrar un nuevo producto en el inventario.
 */
public record ProductRequest(

        @NotBlank(message = "El nombre del producto es obligatorio.")
        String name,

        @NotBlank(message = "La categoría es obligatoria.")
        String category,

        @NotNull(message = "El precio base es obligatorio.")
        @Min(value = 0, message = "El precio base no puede ser negativo.")
        Double basePrice,

        @NotNull(message = "El stock inicial es obligatorio.")
        @Min(value = 0, message = "El stock inicial no puede ser negativo.")
        Integer stock,

        @NotNull(message = "El stock mínimo es obligatorio.")
        @Min(value = 1, message = "El stock mínimo debe ser al menos 1.")
        Integer minimumStock
) {}

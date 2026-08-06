package com.techsolutions.strategy.dto;

/**
 * DTO de respuesta con el resultado del cálculo de precio.
 */
public record PriceCalculationResponse(
        double basePrice,
        int    quantity,
        String strategyUsed,
        double unitPrice,
        double totalPrice,
        String strategyDescription
) {}

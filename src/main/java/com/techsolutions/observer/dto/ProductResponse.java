package com.techsolutions.observer.dto;

import com.techsolutions.observer.Product;

/**
 * DTO de respuesta que representa el estado de un producto en el inventario.
 */
public record ProductResponse(
        String  id,
        String  name,
        String  category,
        double  basePrice,
        int     stock,
        int     minimumStock,
        boolean belowMinimum,
        int     observersCount
) {
    public static ProductResponse from(Product product) {
        return new ProductResponse(
                product.getId(),
                product.getName(),
                product.getCategory(),
                product.getBasePrice(),
                product.getStock(),
                product.getMinimumStock(),
                product.isBelowMinimum(),
                product.getObservers().size()
        );
    }
}

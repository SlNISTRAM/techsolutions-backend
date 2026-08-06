package com.techsolutions.command;

/**
 * Enum que representa todos los estados posibles de un pedido en TechSolutions S.A.
 */
public enum OrderStatus {
    PENDING,      // Creado, pendiente de procesamiento
    PROCESSING,   // En proceso de preparación
    CONFIRMED,    // Confirmado y listo para despacho
    DISCOUNTED,   // Descuento aplicado sobre el total
    CANCELLED     // Cancelado, no puede avanzar
}

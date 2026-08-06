package com.techsolutions.strategy;

/**
 * Interfaz del patrón Strategy para el cálculo de precios.
 *
 * <p>Cada implementación encapsula un algoritmo de cálculo de precio distinto.
 * El contexto {@link PriceCalculatorContext} delega el cálculo a la estrategia
 * activa sin conocer el algoritmo concreto.</p>
 */
public interface PricingStrategy {

    /**
     * Calcula el precio final de venta.
     *
     * @param basePrice precio base del producto en soles (PEN)
     * @param quantity  cantidad de unidades solicitadas
     * @param params    parámetros adicionales opcionales dependientes de la estrategia
     *                  (e.g. porcentaje de descuento, nivel de demanda, etc.)
     * @return precio final calculado (por unidad)
     */
    double calculatePrice(double basePrice, int quantity, StrategyParams params);

    /**
     * Nombre identificador de la estrategia (usado en el contexto).
     */
    String getStrategyName();

    /**
     * Descripción legible de cómo funciona la estrategia.
     */
    String getDescription();
}

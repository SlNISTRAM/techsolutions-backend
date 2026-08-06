package com.techsolutions.strategy;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Clase contexto del patrón Strategy para el cálculo de precios.
 *
 * <p>Mantiene una referencia a la estrategia de precios activa y delega
 * el cálculo a ella. Puede cambiar de estrategia en tiempo de ejecución
 * mediante {@link #setStrategy(String)}.</p>
 *
 * <p>No conoce el algoritmo concreto de ninguna estrategia; solo conoce
 * la interfaz {@link PricingStrategy}.</p>
 */
@Component
public class PriceCalculatorContext {

    private static final Logger log = LoggerFactory.getLogger(PriceCalculatorContext.class);

    private final Map<String, PricingStrategy> strategyRegistry = new HashMap<>();
    private PricingStrategy currentStrategy;

    public PriceCalculatorContext(List<PricingStrategy> strategies) {
        for (PricingStrategy strategy : strategies) {
            strategyRegistry.put(strategy.getStrategyName().toUpperCase(), strategy);
            log.info("Estrategia de precio registrada: {}", strategy.getStrategyName());
        }
        // Estrategia por defecto: STANDARD
        currentStrategy = strategyRegistry.get("STANDARD");
        log.info("Estrategia de precio inicial: STANDARD");
    }

    /**
     * Cambia la estrategia activa en tiempo de ejecución.
     *
     * @param strategyName nombre de la estrategia (insensible a mayúsculas)
     * @throws IllegalArgumentException si la estrategia no existe
     */
    public void setStrategy(String strategyName) {
        PricingStrategy strategy = strategyRegistry.get(strategyName.toUpperCase());
        if (strategy == null) {
            throw new IllegalArgumentException(
                "Estrategia no encontrada: '" + strategyName +
                "'. Disponibles: " + strategyRegistry.keySet()
            );
        }
        this.currentStrategy = strategy;
        log.info("Estrategia de precio cambiada a: {}", strategy.getStrategyName());
    }

    /**
     * Calcula el precio final usando la estrategia activa.
     *
     * @param basePrice precio base del producto
     * @param quantity  cantidad de unidades
     * @param params    parámetros adicionales de la estrategia
     * @return precio final calculado
     */
    public double calculatePrice(double basePrice, int quantity, StrategyParams params) {
        log.debug("Calculando precio con estrategia '{}': base={}, qty={}",
                currentStrategy.getStrategyName(), basePrice, quantity);
        return currentStrategy.calculatePrice(basePrice, quantity, params);
    }

    /**
     * Devuelve el nombre de la estrategia activa.
     */
    public String getCurrentStrategyName() {
        return currentStrategy.getStrategyName();
    }

    /**
     * Devuelve la descripción de la estrategia activa.
     */
    public String getCurrentStrategyDescription() {
        return currentStrategy.getDescription();
    }

    /**
     * Devuelve los nombres de todas las estrategias disponibles.
     */
    public Map<String, String> getAllStrategies() {
        Map<String, String> result = new HashMap<>();
        strategyRegistry.forEach((name, strategy) -> result.put(name, strategy.getDescription()));
        return result;
    }
}

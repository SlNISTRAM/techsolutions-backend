package com.techsolutions.strategy;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

/**
 * Estrategia estándar de precio.
 *
 * <p>Aplica únicamente el IGV (18%) al precio base, sin descuentos ni ajustes
 * por demanda. Es la estrategia por defecto para ventas regulares.</p>
 *
 * <p>Fórmula: precioFinal = precioBase × (1 + IGV)</p>
 */
@Component
public class StandardPricingStrategy implements PricingStrategy {

    private static final Logger log = LoggerFactory.getLogger(StandardPricingStrategy.class);
    private static final String STRATEGY_NAME = "STANDARD";

    @Override
    public double calculatePrice(double basePrice, int quantity, StrategyParams params) {
        double tax        = params.getTaxRate();
        double finalPrice = basePrice * (1 + tax);

        log.debug("[Standard] Base={}, IGV={}%, Final={}", basePrice, tax * 100, finalPrice);

        return Math.round(finalPrice * 100.0) / 100.0;
    }

    @Override
    public String getStrategyName() {
        return STRATEGY_NAME;
    }

    @Override
    public String getDescription() {
        return "Precio estándar con IGV incluido (18%). Sin descuentos adicionales.";
    }
}

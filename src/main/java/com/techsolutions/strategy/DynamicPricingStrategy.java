package com.techsolutions.strategy;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

/**
 * Estrategia de precio dinámico basado en demanda y temporada.
 *
 * <p>Ajusta el precio usando un multiplicador de demanda: si la demanda es alta
 * (multiplier > 1) el precio sube; si es baja (multiplier < 1) el precio baja.
 * También aplica una reducción adicional si la cantidad es alta (compra por lotes),
 * incentivando pedidos grandes incluso en temporada alta.</p>
 *
 * <p>Fórmula:<br/>
 * precioAjustado = precioBase × demandMultiplier<br/>
 * si qty >= bulkThreshold → precioAjustado × (1 - bulkDiscountRate)<br/>
 * precioFinal = precioAjustado × (1 + IGV)</p>
 *
 * <p>Casos de uso típicos: temporada navideña (multiplier=1.2),
 * liquidación de fin de temporada (multiplier=0.75).</p>
 */
@Component
public class DynamicPricingStrategy implements PricingStrategy {

    private static final Logger log = LoggerFactory.getLogger(DynamicPricingStrategy.class);
    private static final String STRATEGY_NAME = "DYNAMIC";

    @Override
    public double calculatePrice(double basePrice, int quantity, StrategyParams params) {
        double demandMultiplier = params.getDemandMultiplier();
        double tax              = params.getTaxRate();

        double adjustedPrice = basePrice * demandMultiplier;

        String priceDirection = demandMultiplier > 1.0 ? "↑ ALTA demanda"
                              : demandMultiplier < 1.0 ? "↓ BAJA demanda"
                              : "→ demanda NORMAL";
        log.debug("[Dynamic] Multiplicador={} ({})", demandMultiplier, priceDirection);

        if (quantity >= params.getBulkThreshold()) {
            adjustedPrice = adjustedPrice * (1 - params.getBulkDiscountRate());
            log.debug("[Dynamic] Descuento por volumen aplicado: {}%", params.getBulkDiscountRate() * 100);
        }

        double finalPrice = adjustedPrice * (1 + tax);

        log.debug("[Dynamic] Base={}, Multiplicador={}, IGV={}%, Final={}",
                basePrice, demandMultiplier, tax * 100, finalPrice);

        return Math.round(finalPrice * 100.0) / 100.0;
    }

    @Override
    public String getStrategyName() {
        return STRATEGY_NAME;
    }

    @Override
    public String getDescription() {
        return "Precio dinámico ajustado por multiplicador de demanda. " +
               "demandMultiplier > 1 sube el precio (alta demanda); " +
               "demandMultiplier < 1 lo baja (baja demanda / liquidación). " +
               "Descuento adicional por volumen disponible. IGV incluido.";
    }
}

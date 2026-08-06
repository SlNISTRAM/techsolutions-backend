package com.techsolutions.strategy;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

/**
 * Estrategia de precio con descuento porcentual.
 *
 * <p>Aplica el descuento configurado al precio base y luego añade el IGV.
 * Adicionalmente, si la cantidad supera el umbral de compra por volumen
 * ({@code bulkThreshold}), se aplica un descuento adicional de volumen.</p>
 *
 * <p>Fórmula:<br/>
 * precioConDescuento  = precioBase × (1 - descuento%)<br/>
 * descuentoVolumen    = si qty >= bulkThreshold → precioConDescuento × (1 - bulkDiscountRate)<br/>
 * precioFinal         = precioConDescuento × (1 + IGV)</p>
 */
@Component
public class PercentageDiscountStrategy implements PricingStrategy {

    private static final Logger log = LoggerFactory.getLogger(PercentageDiscountStrategy.class);
    private static final String STRATEGY_NAME = "PERCENTAGE_DISCOUNT";

    @Override
    public double calculatePrice(double basePrice, int quantity, StrategyParams params) {
        double discountRate  = params.getDiscountPercentage() / 100.0;
        double tax           = params.getTaxRate();

        double priceAfterDiscount = basePrice * (1 - discountRate);

        if (quantity >= params.getBulkThreshold()) {
            double bulkRate = params.getBulkDiscountRate();
            log.debug("[PercentageDiscount] Descuento volumen aplicado: {}%", bulkRate * 100);
            priceAfterDiscount = priceAfterDiscount * (1 - bulkRate);
        }

        double finalPrice = priceAfterDiscount * (1 + tax);

        log.debug("[PercentageDiscount] Base={}, Descuento={}%, Volumen={}, IGV={}%, Final={}",
                basePrice, params.getDiscountPercentage(),
                quantity >= params.getBulkThreshold() ? "SI" : "NO",
                tax * 100, finalPrice);

        return Math.round(finalPrice * 100.0) / 100.0;
    }

    @Override
    public String getStrategyName() {
        return STRATEGY_NAME;
    }

    @Override
    public String getDescription() {
        return "Aplica descuento porcentual configurable al precio base. " +
               "Descuento adicional por volumen si la cantidad supera el umbral definido. " +
               "IGV incluido al final.";
    }
}

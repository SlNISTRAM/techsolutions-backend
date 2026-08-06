package com.techsolutions.strategy;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("Pruebas Unitarias — Patrón Strategy")
class StrategyPatternTest {

    private StandardPricingStrategy standardStrategy;
    private PercentageDiscountStrategy discountStrategy;
    private DynamicPricingStrategy dynamicStrategy;
    private PriceCalculatorContext context;

    @BeforeEach
    void setUp() {
        standardStrategy = new StandardPricingStrategy();
        discountStrategy = new PercentageDiscountStrategy();
        dynamicStrategy = new DynamicPricingStrategy();
        context = new PriceCalculatorContext(List.of(standardStrategy, discountStrategy, dynamicStrategy));
    }

    @Test
    @DisplayName("StandardPricingStrategy calcula precio base + IGV 18%")
    void testStandardPricingStrategy() {
        StrategyParams params = StrategyParams.defaults();
        double price = standardStrategy.calculatePrice(100.0, 1, params);

        assertEquals(118.0, price);
    }

    @Test
    @DisplayName("PercentageDiscountStrategy aplica descuento % y descuento por volumen")
    void testPercentageDiscountStrategyWithBulk() {
        StrategyParams params = StrategyParams.builder()
                .discountPercentage(10.0)
                .bulkThreshold(5)
                .bulkDiscountRate(0.05)
                .build();

        // Base: 100 -> 10% desc = 90 -> Bulk (qty 10 >= 5): 90 * 0.95 = 85.5 -> IGV 18%: 85.5 * 1.18 = 100.89
        double price = discountStrategy.calculatePrice(100.0, 10, params);

        assertEquals(100.89, price);
    }

    @Test
    @DisplayName("DynamicPricingStrategy ajusta precio por multiplicador de demanda")
    void testDynamicPricingStrategyHighDemand() {
        StrategyParams params = StrategyParams.builder()
                .demandMultiplier(1.5) // Alta demanda
                .build();

        // Base 100 * 1.5 = 150 -> +18% IGV = 177.0
        double price = dynamicStrategy.calculatePrice(100.0, 1, params);

        assertEquals(177.0, price);
    }

    @Test
    @DisplayName("PriceCalculatorContext permite cambiar la estrategia dinámicamente")
    void testContextStrategySwitching() {
        assertEquals("STANDARD", context.getCurrentStrategyName());

        context.setStrategy("PERCENTAGE_DISCOUNT");
        assertEquals("PERCENTAGE_DISCOUNT", context.getCurrentStrategyName());

        StrategyParams params = StrategyParams.builder().discountPercentage(20.0).build();
        double price = context.calculatePrice(100.0, 1, params);

        // 100 * 0.8 = 80 -> * 1.18 = 94.4
        assertEquals(94.4, price);
    }
}

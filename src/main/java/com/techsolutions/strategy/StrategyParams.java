package com.techsolutions.strategy;

/**
 * Objeto de parámetros opcionales pasados a cada estrategia de precios.
 * Usa el patrón Builder para facilitar la construcción parcial.
 */
public class StrategyParams {

    private double discountPercentage = 0.0;
    private double demandMultiplier   = 1.0;
    private int    bulkThreshold      = 10;
    private double bulkDiscountRate   = 0.05;
    private double taxRate            = 0.18;

    private StrategyParams() {}

    public double getDiscountPercentage() { return discountPercentage; }
    public double getDemandMultiplier()   { return demandMultiplier; }
    public int    getBulkThreshold()      { return bulkThreshold; }
    public double getBulkDiscountRate()   { return bulkDiscountRate; }
    public double getTaxRate()            { return taxRate; }

    public static Builder builder() { return new Builder(); }

    public static class Builder {
        private final StrategyParams params = new StrategyParams();

        public Builder discountPercentage(double pct) {
            if (pct < 0 || pct > 100) throw new IllegalArgumentException("Descuento debe estar entre 0 y 100.");
            params.discountPercentage = pct;
            return this;
        }

        public Builder demandMultiplier(double multiplier) {
            if (multiplier <= 0) throw new IllegalArgumentException("El multiplicador de demanda debe ser positivo.");
            params.demandMultiplier = multiplier;
            return this;
        }

        public Builder bulkThreshold(int threshold) {
            params.bulkThreshold = threshold;
            return this;
        }

        public Builder bulkDiscountRate(double rate) {
            params.bulkDiscountRate = rate;
            return this;
        }

        public Builder taxRate(double rate) {
            params.taxRate = rate;
            return this;
        }

        public StrategyParams build() { return params; }
    }

    public static StrategyParams defaults() {
        return new Builder().build();
    }
}

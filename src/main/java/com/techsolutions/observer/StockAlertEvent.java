package com.techsolutions.observer;

import java.time.LocalDateTime;

/**
 * Objeto de evento que transporta todos los datos relevantes cuando
 * el stock de un producto cae por debajo del mínimo configurado.
 * Es inmutable una vez creado.
 */
public class StockAlertEvent {

    private final String productId;
    private final String productName;
    private final String category;
    private final int previousStock;
    private final int currentStock;
    private final int minimumStock;
    private final LocalDateTime alertTime;

    public StockAlertEvent(String productId, String productName, String category,
                           int previousStock, int currentStock, int minimumStock) {
        this.productId     = productId;
        this.productName   = productName;
        this.category      = category;
        this.previousStock = previousStock;
        this.currentStock  = currentStock;
        this.minimumStock  = minimumStock;
        this.alertTime     = LocalDateTime.now();
    }

    public String getProductId()    { return productId; }
    public String getProductName()  { return productName; }
    public String getCategory()     { return category; }
    public int getPreviousStock()   { return previousStock; }
    public int getCurrentStock()    { return currentStock; }
    public int getMinimumStock()    { return minimumStock; }
    public LocalDateTime getAlertTime() { return alertTime; }

    /**
     * Indica cuántas unidades faltan para alcanzar el stock mínimo.
     */
    public int getUnitsMissing() {
        return Math.max(0, minimumStock - currentStock);
    }

    @Override
    public String toString() {
        return String.format(
            "StockAlertEvent{producto='%s', categoría='%s', stockAnterior=%d, stockActual=%d, mínimo=%d, faltantes=%d, hora=%s}",
            productName, category, previousStock, currentStock, minimumStock, getUnitsMissing(), alertTime
        );
    }
}

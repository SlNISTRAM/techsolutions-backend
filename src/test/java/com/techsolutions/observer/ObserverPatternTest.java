package com.techsolutions.observer;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("Pruebas Unitarias — Patrón Observer")
class ObserverPatternTest {

    private Product product;
    private GerenteNotificationObserver gerenteObserver;
    private ComprasNotificationObserver comprasObserver;

    @BeforeEach
    void setUp() {
        product = new Product("P001", "Monitor 24''", "MONITORES", 500.0, 10, 5);
        gerenteObserver = new GerenteNotificationObserver();
        comprasObserver = new ComprasNotificationObserver();

        gerenteObserver.clearLog();
        comprasObserver.clearLog();

        product.registerObserver(gerenteObserver);
        product.registerObserver(comprasObserver);
    }

    @Test
    @DisplayName("Product notifica a observadores cuando el stock cae por debajo del mínimo")
    void testObserversNotifiedWhenStockBelowMinimum() {
        assertEquals(0, gerenteObserver.getNotificationLog().size());
        assertEquals(0, comprasObserver.getPurchaseOrderLog().size());

        // Reducir stock de 10 a 3 (mínimo es 5)
        product.decreaseStock(7);

        assertTrue(product.isBelowMinimum());
        assertEquals(1, gerenteObserver.getNotificationLog().size());
        assertEquals(1, comprasObserver.getPurchaseOrderLog().size());

        String gerenteMsg = gerenteObserver.getNotificationLog().get(0);
        assertTrue(gerenteMsg.contains("Monitor 24''"));

        String comprasMsg = comprasObserver.getPurchaseOrderLog().get(0);
        assertTrue(comprasMsg.contains("ORDEN COMPRAS"));
    }

    @Test
    @DisplayName("Product NO notifica observadores si el stock se mantiene sobre el mínimo")
    void testObserversNotNotifiedWhenStockAboveMinimum() {
        // Reducir stock de 10 a 6 (mínimo es 5)
        product.decreaseStock(4);

        assertFalse(product.isBelowMinimum());
        assertEquals(0, gerenteObserver.getNotificationLog().size());
        assertEquals(0, comprasObserver.getPurchaseOrderLog().size());
    }

    @Test
    @DisplayName("Se pueden remover observadores del Subject")
    void testRemoveObserver() {
        product.removeObserver(gerenteObserver);
        product.decreaseStock(8); // Cae a stock 2

        assertEquals(0, gerenteObserver.getNotificationLog().size());
        assertEquals(1, comprasObserver.getPurchaseOrderLog().size());
    }
}

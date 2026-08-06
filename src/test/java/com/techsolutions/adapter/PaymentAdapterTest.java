package com.techsolutions.adapter;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("Pruebas Unitarias — Patrón Adapter")
class PaymentAdapterTest {

    private PayPalAdapter paypalAdapter;
    private YapeAdapter yapeAdapter;
    private PlinAdapter plinAdapter;
    private PaymentAdapterService adapterService;

    @BeforeEach
    void setUp() {
        paypalAdapter = new PayPalAdapter();
        yapeAdapter = new YapeAdapter();
        plinAdapter = new PlinAdapter();
        adapterService = new PaymentAdapterService(List.of(paypalAdapter, yapeAdapter, plinAdapter));
    }

    @Test
    @DisplayName("PayPalAdapter procesa pagos y realiza conversión de PEN a USD")
    void testPayPalAdapterProcessPayment() {
        String result = paypalAdapter.processPayment(375.0, "PEN");
        assertNotNull(result);
        assertTrue(result.contains("PayPal"));
        assertTrue(result.contains("100.00 USD"));
    }

    @Test
    @DisplayName("YapeAdapter permite transacciones en PEN dentro del límite de S/ 2000")
    void testYapeAdapterSuccess() {
        String result = yapeAdapter.processPayment(500.0, "PEN");
        assertNotNull(result);
        assertTrue(result.contains("Yape"));
    }

    @Test
    @DisplayName("YapeAdapter rechaza transacciones mayores a S/ 2000")
    void testYapeAdapterExceedsLimit() {
        IllegalArgumentException ex = assertThrows(
                IllegalArgumentException.class,
                () -> yapeAdapter.processPayment(2500.0, "PEN")
        );
        assertTrue(ex.getMessage().contains("2,000.00"));
    }

    @Test
    @DisplayName("PlinAdapter rechaza transacciones que no sean en soles (PEN)")
    void testPlinAdapterInvalidCurrency() {
        IllegalArgumentException ex = assertThrows(
                IllegalArgumentException.class,
                () -> plinAdapter.processPayment(100.0, "USD")
        );
        assertTrue(ex.getMessage().contains("solo acepta pagos en soles"));
    }

    @Test
    @DisplayName("PaymentAdapterService permite habilitar y deshabilitar adaptadores")
    void testEnableDisableAdapterService() {
        adapterService.disableAdapter("YAPE");
        assertFalse(yapeAdapter.isEnabled());

        IllegalStateException ex = assertThrows(
                IllegalStateException.class,
                () -> adapterService.processPayment("YAPE", 100.0, "PEN")
        );
        assertTrue(ex.getMessage().contains("deshabilitado"));

        adapterService.enableAdapter("YAPE");
        assertTrue(yapeAdapter.isEnabled());
        assertDoesNotThrow(() -> adapterService.processPayment("YAPE", 100.0, "PEN"));
    }
}

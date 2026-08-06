package com.techsolutions.adapter;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

/**
 * Adaptador para la pasarela de pago PayPal.
 * Traduce la interfaz interna de TechSolutions hacia la API de PayPal.
 */
@Component
public class PayPalAdapter implements PaymentAdapter {

    private static final Logger log = LoggerFactory.getLogger(PayPalAdapter.class);
    private static final String ADAPTER_NAME = "PAYPAL";

    private boolean enabled = true;

    @Override
    public String processPayment(double amount, String currency) {
        if (!enabled) {
            throw new IllegalStateException("El adaptador PayPal está deshabilitado.");
        }
        log.info("[PayPal] Iniciando pago de {} {}", amount, currency);

        // Simulación de conversión y envío a la API de PayPal
        String paypalCurrency = currency.equalsIgnoreCase("PEN") ? "USD" : currency;
        double convertedAmount = currency.equalsIgnoreCase("PEN") ? amount / 3.75 : amount;

        log.debug("[PayPal] Conversión: {} PEN -> {} {}", amount, convertedAmount, paypalCurrency);

        String transactionId = "PP-" + System.currentTimeMillis();
        log.info("[PayPal] Pago procesado exitosamente. TxID={}", transactionId);

        return String.format("PayPal: Pago de %.2f %s procesado. TxID=%s", convertedAmount, paypalCurrency, transactionId);
    }

    @Override
    public String getAdapterName() {
        return ADAPTER_NAME;
    }

    @Override
    public boolean isEnabled() {
        return enabled;
    }

    @Override
    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
        log.info("[PayPal] Estado cambiado a: {}", enabled ? "HABILITADO" : "DESHABILITADO");
    }
}

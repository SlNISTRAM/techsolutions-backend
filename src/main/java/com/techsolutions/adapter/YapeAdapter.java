package com.techsolutions.adapter;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

/**
 * Adaptador para la pasarela de pago Yape (BCP).
 * Traduce la interfaz interna de TechSolutions hacia la API de Yape.
 */
@Component
public class YapeAdapter implements PaymentAdapter {

    private static final Logger log = LoggerFactory.getLogger(YapeAdapter.class);
    private static final String ADAPTER_NAME = "YAPE";

    private boolean enabled = true;

    @Override
    public String processPayment(double amount, String currency) {
        if (!enabled) {
            throw new IllegalStateException("El adaptador Yape está deshabilitado.");
        }
        if (!currency.equalsIgnoreCase("PEN")) {
            throw new IllegalArgumentException("Yape solo acepta pagos en soles (PEN). Moneda recibida: " + currency);
        }

        log.info("[Yape] Iniciando pago de S/ {}", amount);

        if (amount > 2000.0) {
            throw new IllegalArgumentException("Yape no permite transacciones mayores a S/ 2,000.00 por operación.");
        }

        String qrCode = "YPQ-" + System.currentTimeMillis();
        log.info("[Yape] QR generado: {}", qrCode);

        String transactionId = "YP-" + System.currentTimeMillis();
        log.info("[Yape] Pago procesado exitosamente. TxID={}", transactionId);

        return String.format("Yape: Pago de S/ %.2f procesado mediante QR=%s. TxID=%s", amount, qrCode, transactionId);
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
        log.info("[Yape] Estado cambiado a: {}", enabled ? "HABILITADO" : "DESHABILITADO");
    }
}

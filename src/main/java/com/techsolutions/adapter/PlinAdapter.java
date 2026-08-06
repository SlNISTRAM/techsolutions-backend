package com.techsolutions.adapter;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

/**
 * Adaptador para la pasarela de pago Plin (BBVA / Scotiabank / Interbank / BanBif).
 * Traduce la interfaz interna de TechSolutions hacia la API de Plin.
 */
@Component
public class PlinAdapter implements PaymentAdapter {

    private static final Logger log = LoggerFactory.getLogger(PlinAdapter.class);
    private static final String ADAPTER_NAME = "PLIN";

    private boolean enabled = true;

    @Override
    public String processPayment(double amount, String currency) {
        if (!enabled) {
            throw new IllegalStateException("El adaptador Plin está deshabilitado.");
        }
        if (!currency.equalsIgnoreCase("PEN")) {
            throw new IllegalArgumentException("Plin solo acepta pagos en soles (PEN). Moneda recibida: " + currency);
        }

        log.info("[Plin] Iniciando pago de S/ {}", amount);

        if (amount > 5000.0) {
            throw new IllegalArgumentException("Plin no permite transacciones mayores a S/ 5,000.00 por operación.");
        }

        String authCode = "PLIN-AUTH-" + System.currentTimeMillis();
        log.debug("[Plin] Código de autorización generado: {}", authCode);

        String transactionId = "PL-" + System.currentTimeMillis();
        log.info("[Plin] Pago procesado exitosamente. TxID={}", transactionId);

        return String.format("Plin: Pago de S/ %.2f procesado. AuthCode=%s. TxID=%s", amount, authCode, transactionId);
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
        log.info("[Plin] Estado cambiado a: {}", enabled ? "HABILITADO" : "DESHABILITADO");
    }
}

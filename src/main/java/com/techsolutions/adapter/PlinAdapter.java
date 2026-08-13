package com.techsolutions.adapter;

import com.techsolutions.adapter.gateway.PlinGateway;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

/**
 * Adaptador para la pasarela de pago Plin (BBVA / Scotiabank / Interbank / BanBif).
 * Traduce la interfaz interna de TechSolutions ({@link PaymentAdapter}) hacia
 * la interfaz incompatible del SDK externo simulado {@link PlinGateway}.
 */
@Component
public class PlinAdapter implements PaymentAdapter {

    private static final Logger log = LoggerFactory.getLogger(PlinAdapter.class);
    private static final String ADAPTER_NAME = "PLIN";
    private static final String TELEFONO_COMERCIO = "988777666";

    private final PlinGateway gateway = new PlinGateway();
    private boolean enabled = true;

    @Override
    public String processPayment(double amount, String currency) {
        if (!enabled) {
            throw new IllegalStateException("El adaptador Plin está deshabilitado.");
        }
        if (!currency.equalsIgnoreCase("PEN")) {
            throw new IllegalArgumentException("Plin solo acepta pagos en soles (PEN). Moneda recibida: " + currency);
        }
        if (amount > 5000.0) {
            throw new IllegalArgumentException("Plin no permite transacciones mayores a S/ 5,000.00 por operación.");
        }

        log.info("[Plin] Iniciando pago de S/ {}", amount);

        // Adaptee: el SDK externo tiene su propia firma de metodo y objeto de respuesta
        PlinGateway.PlinAuthorizationResponse respuesta =
                gateway.solicitarPago(amount, TELEFONO_COMERCIO, "Pago TechSolutions");

        if (!respuesta.approved()) {
            throw new IllegalStateException("Plin rechazó la transacción.");
        }

        log.debug("[Plin] Código de autorización generado: {}", respuesta.authCode());
        log.info("[Plin] Pago procesado exitosamente. TxID={}", respuesta.transactionId());

        return String.format("Plin: Pago de S/ %.2f procesado. AuthCode=%s. TxID=%s",
                amount, respuesta.authCode(), respuesta.transactionId());
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

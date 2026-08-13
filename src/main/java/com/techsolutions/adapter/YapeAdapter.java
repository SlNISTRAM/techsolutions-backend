package com.techsolutions.adapter;

import com.techsolutions.adapter.gateway.YapeGateway;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.util.Map;

/**
 * Adaptador para la pasarela de pago Yape (BCP).
 * Traduce la interfaz interna de TechSolutions ({@link PaymentAdapter}) hacia
 * la interfaz incompatible del SDK externo simulado {@link YapeGateway}, que
 * responde con un {@code Map} crudo en vez de un objeto tipado.
 */
@Component
public class YapeAdapter implements PaymentAdapter {

    private static final Logger log = LoggerFactory.getLogger(YapeAdapter.class);
    private static final String ADAPTER_NAME = "YAPE";
    private static final String CELULAR_COMERCIO = "999888777";

    private final YapeGateway gateway = new YapeGateway();
    private boolean enabled = true;

    @Override
    public String processPayment(double amount, String currency) {
        if (!enabled) {
            throw new IllegalStateException("El adaptador Yape está deshabilitado.");
        }
        if (!currency.equalsIgnoreCase("PEN")) {
            throw new IllegalArgumentException("Yape solo acepta pagos en soles (PEN). Moneda recibida: " + currency);
        }
        if (amount > 2000.0) {
            throw new IllegalArgumentException("Yape no permite transacciones mayores a S/ 2,000.00 por operación.");
        }

        log.info("[Yape] Iniciando pago de S/ {}", amount);

        // Adaptee: el SDK externo solo entiende su propia firma (celular, monto) y responde con un Map
        Map<String, Object> respuesta = gateway.enviarCobro(CELULAR_COMERCIO, amount);

        boolean exitoso = "OK".equals(respuesta.get("estado"));
        if (!exitoso) {
            throw new IllegalStateException("Yape rechazó la transacción: " + respuesta.get("estado"));
        }

        String qrCode = String.valueOf(respuesta.get("codigoOperacion"));
        log.info("[Yape] Pago procesado exitosamente. TxID={}", qrCode);

        return String.format("Yape: Pago de S/ %.2f procesado mediante QR=%s. TxID=%s", amount, qrCode, qrCode);
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

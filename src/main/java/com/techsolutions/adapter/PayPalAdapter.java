package com.techsolutions.adapter;

import com.techsolutions.adapter.gateway.PayPalGateway;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

/**
 * Adaptador para la pasarela de pago PayPal.
 * Traduce la interfaz interna de TechSolutions ({@link PaymentAdapter}) hacia
 * la interfaz incompatible del SDK externo simulado {@link PayPalGateway}.
 */
@Component
public class PayPalAdapter implements PaymentAdapter {

    private static final Logger log = LoggerFactory.getLogger(PayPalAdapter.class);
    private static final String ADAPTER_NAME = "PAYPAL";

    private final PayPalGateway gateway = new PayPalGateway();
    private boolean enabled = true;

    @Override
    public String processPayment(double amount, String currency) {
        if (!enabled) {
            throw new IllegalStateException("El adaptador PayPal está deshabilitado.");
        }
        log.info("[PayPal] Iniciando pago de {} {}", amount, currency);

        // Traduccion: PayPal solo trabaja en USD, aqui se adapta el monto/moneda local
        String paypalCurrency = currency.equalsIgnoreCase("PEN") ? "USD" : currency;
        double convertedAmount = currency.equalsIgnoreCase("PEN") ? amount / 3.75 : amount;

        log.debug("[PayPal] Conversión: {} PEN -> {} {}", amount, convertedAmount, paypalCurrency);

        // Adaptee: el SDK externo solo entiende su propio request/response
        PayPalGateway.PayPalChargeRequest request =
                new PayPalGateway.PayPalChargeRequest(convertedAmount, "ORD-" + System.currentTimeMillis());
        PayPalGateway.PayPalChargeResponse response = gateway.charge(request);

        boolean exitoso = "COMPLETED".equals(response.status());
        if (!exitoso) {
            throw new IllegalStateException("PayPal rechazó la transacción: " + response.status());
        }

        log.info("[PayPal] Pago procesado exitosamente. TxID={}", response.chargeId());

        return String.format("PayPal: Pago de %.2f %s procesado. TxID=%s",
                response.amount(), paypalCurrency, response.chargeId());
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

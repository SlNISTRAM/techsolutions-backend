package com.techsolutions.adapter.gateway;

/**
 * Simula el SDK externo real de PayPal. Su interfaz (request/response propios,
 * método {@code charge}) es incompatible con {@link com.techsolutions.adapter.PaymentAdapter}
 * — es exactamente la clase que {@link com.techsolutions.adapter.PayPalAdapter} traduce.
 */
public class PayPalGateway {

    public PayPalChargeResponse charge(PayPalChargeRequest request) {
        String chargeId = "PP-" + System.currentTimeMillis();
        return new PayPalChargeResponse(chargeId, "COMPLETED", request.amountUsd());
    }

    public record PayPalChargeRequest(double amountUsd, String orderReference) {}

    public record PayPalChargeResponse(String chargeId, String status, double amount) {}
}

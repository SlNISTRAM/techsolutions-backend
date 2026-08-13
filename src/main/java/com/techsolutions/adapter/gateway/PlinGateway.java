package com.techsolutions.adapter.gateway;

/**
 * Simula el SDK externo real de Plin. Su firma de método y objeto de respuesta
 * son distintos a los de {@link PayPalGateway} y {@link YapeGateway} — es la
 * clase que {@link com.techsolutions.adapter.PlinAdapter} traduce.
 */
public class PlinGateway {

    public PlinAuthorizationResponse solicitarPago(double monto, String telefono, String glosa) {
        String authCode = "PLIN-AUTH-" + System.currentTimeMillis();
        String transactionId = "PL-" + System.currentTimeMillis();
        return new PlinAuthorizationResponse(transactionId, authCode, monto > 0);
    }

    public record PlinAuthorizationResponse(String transactionId, String authCode, boolean approved) {}
}

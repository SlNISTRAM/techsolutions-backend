package com.techsolutions.adapter.gateway;

import java.util.Map;

/**
 * Simula el SDK externo real de Yape. Responde con un {@code Map} crudo en vez
 * de un objeto tipado — una forma de incompatibilidad distinta a la de PayPal,
 * a propósito, para reforzar que cada pasarela llega con su propio "idioma".
 * Es la clase que {@link com.techsolutions.adapter.YapeAdapter} traduce.
 */
public class YapeGateway {

    public Map<String, Object> enviarCobro(String celular, double montoSoles) {
        String codigoOperacion = "YPQ-" + System.currentTimeMillis();
        return Map.of(
                "codigoOperacion", codigoOperacion,
                "estado", "OK",
                "monto", montoSoles
        );
    }
}

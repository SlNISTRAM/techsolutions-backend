package com.techsolutions.adapter;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Servicio que gestiona el registro de adaptadores de pago y orquesta
 * su activación, desactivación y ejecución de pagos.
 */
@Service
public class PaymentAdapterService {

    private static final Logger log = LoggerFactory.getLogger(PaymentAdapterService.class);

    private final Map<String, PaymentAdapter> adapters = new HashMap<>();

    public PaymentAdapterService(List<PaymentAdapter> adapterList) {
        for (PaymentAdapter adapter : adapterList) {
            adapters.put(adapter.getAdapterName().toUpperCase(), adapter);
            log.info("Adaptador registrado: {}", adapter.getAdapterName());
        }
    }

    /**
     * Habilita el adaptador identificado por {@code adapterName}.
     *
     * @param adapterName nombre del adaptador (insensible a mayúsculas)
     * @throws IllegalArgumentException si el adaptador no existe
     */
    public void enableAdapter(String adapterName) {
        PaymentAdapter adapter = resolveAdapter(adapterName);
        adapter.setEnabled(true);
        log.info("Adaptador '{}' habilitado.", adapterName.toUpperCase());
    }

    /**
     * Deshabilita el adaptador identificado por {@code adapterName}.
     *
     * @param adapterName nombre del adaptador (insensible a mayúsculas)
     * @throws IllegalArgumentException si el adaptador no existe
     */
    public void disableAdapter(String adapterName) {
        PaymentAdapter adapter = resolveAdapter(adapterName);
        adapter.setEnabled(false);
        log.info("Adaptador '{}' deshabilitado.", adapterName.toUpperCase());
    }

    /**
     * Procesa un pago usando el adaptador especificado.
     *
     * @param adapterName nombre del adaptador a usar
     * @param amount      monto del pago
     * @param currency    moneda del pago
     * @return resultado de la operación
     */
    public String processPayment(String adapterName, double amount, String currency) {
        PaymentAdapter adapter = resolveAdapter(adapterName);
        log.info("Procesando pago con adaptador '{}': {} {}", adapterName.toUpperCase(), amount, currency);
        return adapter.processPayment(amount, currency);
    }

    /**
     * Devuelve el estado actual de todos los adaptadores registrados.
     *
     * @return mapa con nombre del adaptador y su estado (true=habilitado)
     */
    public Map<String, Boolean> getAdaptersStatus() {
        Map<String, Boolean> status = new HashMap<>();
        adapters.forEach((name, adapter) -> status.put(name, adapter.isEnabled()));
        return status;
    }

    private PaymentAdapter resolveAdapter(String adapterName) {
        PaymentAdapter adapter = adapters.get(adapterName.toUpperCase());
        if (adapter == null) {
            throw new IllegalArgumentException(
                    "Adaptador no encontrado: '" + adapterName + "'. Disponibles: " + adapters.keySet()
            );
        }
        return adapter;
    }
}

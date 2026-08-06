package com.techsolutions.observer;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Observador concreto que representa al DEPARTAMENTO DE COMPRAS.
 *
 * <p>Al recibir una alerta de stock bajo, genera una orden de reabastecimiento
 * sugerida con la cantidad de unidades necesarias para cubrir el mínimo.
 * Almacena las alertas en su bandeja interna para consulta vía API.</p>
 *
 * <p>En un sistema real, este observador crearía una orden de compra en el ERP
 * o enviaría una solicitud de cotización a los proveedores.</p>
 */
@Component
public class ComprasNotificationObserver implements StockObserver {

    private static final Logger log = LoggerFactory.getLogger(ComprasNotificationObserver.class);
    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss");
    private static final String OBSERVER_NAME = "COMPRAS_OBSERVER";

    private final List<String> purchaseOrderLog = new ArrayList<>();

    @Override
    public void update(StockAlertEvent event) {
        int suggestedOrder = calculateSuggestedOrder(event);
        String message     = buildPurchaseOrderMessage(event, suggestedOrder);

        purchaseOrderLog.add(message);

        log.warn("🛒 [COMPRAS] SOLICITUD DE REABASTECIMIENTO — {}", message);

        // Simulación de creación de orden de compra
        simulatePurchaseOrder(event, suggestedOrder);
    }

    @Override
    public String getObserverName() {
        return OBSERVER_NAME;
    }

    /**
     * Devuelve el historial de órdenes de compra sugeridas.
     */
    public List<String> getPurchaseOrderLog() {
        return Collections.unmodifiableList(purchaseOrderLog);
    }

    /**
     * Limpia el historial de órdenes.
     */
    public void clearLog() {
        purchaseOrderLog.clear();
    }

    /**
     * Calcula la cantidad sugerida para el pedido.
     * Repone hasta el doble del stock mínimo (buffer de seguridad).
     */
    private int calculateSuggestedOrder(StockAlertEvent event) {
        int target = event.getMinimumStock() * 2;
        return Math.max(target - event.getCurrentStock(), event.getUnitsMissing());
    }

    private String buildPurchaseOrderMessage(StockAlertEvent event, int suggestedOrder) {
        return String.format(
            "[%s] 🛒 ORDEN COMPRAS — Producto: '%s' | Categoría: %s | " +
            "Stock: %d uds. (mínimo: %d) | Orden sugerida: %d uds.",
            event.getAlertTime().format(FORMATTER),
            event.getProductName(),
            event.getCategory(),
            event.getCurrentStock(),
            event.getMinimumStock(),
            suggestedOrder
        );
    }

    private void simulatePurchaseOrder(StockAlertEvent event, int suggestedOrder) {
        log.info("[COMPRAS] Generando orden de compra automática:");
        log.info("  Producto     : {}", event.getProductName());
        log.info("  Cantidad     : {} unidades", suggestedOrder);
        log.info("  Prioridad    : {}", event.getCurrentStock() == 0 ? "URGENTE" : "NORMAL");
        log.info("  Notificando a: compras@techsolutions.com");
    }
}

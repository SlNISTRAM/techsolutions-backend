package com.techsolutions.observer;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Observador concreto que representa al GERENTE DE OPERACIONES.
 *
 * <p>Al recibir una alerta de stock bajo, registra la notificación en el log
 * del sistema y la almacena en su bandeja interna para que pueda ser consultada
 * vía API.</p>
 *
 * <p>En un sistema real, este observador enviaría un correo o notificación push
 * al gerente. Aquí se simula ese comportamiento mediante logging estructurado.</p>
 */
@Component
public class GerenteNotificationObserver implements StockObserver {

    private static final Logger log = LoggerFactory.getLogger(GerenteNotificationObserver.class);
    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss");
    private static final String OBSERVER_NAME = "GERENTE_OBSERVER";

    private final List<String> notificationLog = new ArrayList<>();

    @Override
    public void update(StockAlertEvent event) {
        String message = buildMessage(event);
        notificationLog.add(message);

        log.warn("📊 [GERENTE] ALERTA DE STOCK — {}", message);

        // Simulación de envío de notificación ejecutiva
        simulateEmailNotification(event);
    }

    @Override
    public String getObserverName() {
        return OBSERVER_NAME;
    }

    /**
     * Devuelve el historial de notificaciones recibidas por el Gerente.
     */
    public List<String> getNotificationLog() {
        return Collections.unmodifiableList(notificationLog);
    }

    /**
     * Limpia el historial de notificaciones.
     */
    public void clearLog() {
        notificationLog.clear();
    }

    private String buildMessage(StockAlertEvent event) {
        return String.format(
            "[%s] ⚠️ ALERTA GERENCIA — Producto: '%s' | Categoría: %s | " +
            "Stock actual: %d unidades (mínimo: %d) | Faltan: %d unidades para reponer",
            event.getAlertTime().format(FORMATTER),
            event.getProductName(),
            event.getCategory(),
            event.getCurrentStock(),
            event.getMinimumStock(),
            event.getUnitsMissing()
        );
    }

    private void simulateEmailNotification(StockAlertEvent event) {
        log.info("[GERENTE] Simulando envío de email a gerencia@techsolutions.com:");
        log.info("  Asunto : ALERTA — Stock crítico en '{}'", event.getProductName());
        log.info("  Cuerpo : Stock actual {} uds. | Mínimo {} uds. | Déficit: {} uds.",
                event.getCurrentStock(), event.getMinimumStock(), event.getUnitsMissing());
    }
}

package com.techsolutions.observer;

/**
 * Interfaz del patrón Observer para alertas de stock.
 * Cualquier entidad interesada en cambios de stock de un producto
 * debe implementar esta interfaz y registrarse como observador.
 */
public interface StockObserver {

    /**
     * Método invocado por el Subject ({@link Product}) cuando el stock
     * cae por debajo del stock mínimo configurado.
     *
     * @param event objeto con el detalle completo del evento de alerta
     */
    void update(StockAlertEvent event);

    /**
     * Nombre identificador del observador.
     */
    String getObserverName();
}

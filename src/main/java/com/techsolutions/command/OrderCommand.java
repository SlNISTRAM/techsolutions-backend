package com.techsolutions.command;

/**
 * Interfaz del patrón Command para operaciones sobre pedidos.
 *
 * <p>Cada implementación encapsula una acción de negocio sobre un
 * {@link Order} y su correspondiente operación de deshacer.</p>
 *
 * <p><b>GRASP — Bajo Acoplamiento:</b> el {@link OrderInvoker} solo conoce
 * esta interfaz; nunca depende de las implementaciones concretas.</p>
 */
public interface OrderCommand {

    /**
     * Ejecuta la operación encapsulada sobre el pedido receptor.
     */
    void execute();

    /**
     * Deshace la operación ejecutada, restaurando el pedido al estado anterior.
     */
    void undo();

    /**
     * Descripción legible de la acción que representa este comando.
     */
    String getDescription();

    /**
     * ID del pedido sobre el que actúa este comando.
     */
    String getOrderId();
}

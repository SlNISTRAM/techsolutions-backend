package com.techsolutions.command.memento;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Deque;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * Caretaker del patrón Memento para pedidos.
 *
 * <p>Almacena y recupera snapshots ({@link OrderMemento}) de pedidos.
 * Mantiene una pila de mementos por cada pedido para soportar múltiples
 * niveles de deshacer (undo history).</p>
 *
 * <p><b>GRASP — Alta Cohesión:</b> esta clase solo gestiona mementos;
 * no ejecuta ni sabe qué operación generó cada snapshot.</p>
 *
 * <p><b>GRASP — Bajo Acoplamiento:</b> no conoce al {@link com.techsolutions.command.Order}
 * directamente; solo manipula {@link OrderMemento} como caja opaca.</p>
 */
@Component
public class OrderCaretaker {

    private static final Logger log = LoggerFactory.getLogger(OrderCaretaker.class);

    private final Map<String, Deque<OrderMemento>> history = new HashMap<>();

    /**
     * Guarda un memento para el pedido indicado.
     *
     * @param memento snapshot del estado a guardar
     */
    public void save(OrderMemento memento) {
        history.computeIfAbsent(memento.getOrderId(), k -> new ArrayDeque<>()).push(memento);
        log.debug("[Caretaker] Snapshot guardado para pedido '{}': estado={}, total={}",
                memento.getOrderId(), memento.getStatus(), memento.getTotalAmount());
    }

    /**
     * Recupera y elimina el snapshot más reciente del pedido (LIFO).
     *
     * @param orderId ID del pedido
     * @return el memento más reciente, o vacío si no hay historial
     */
    public Optional<OrderMemento> undo(String orderId) {
        Deque<OrderMemento> stack = history.get(orderId);
        if (stack == null || stack.isEmpty()) {
            log.warn("[Caretaker] No hay snapshots previos para el pedido '{}'.", orderId);
            return Optional.empty();
        }
        OrderMemento memento = stack.pop();
        log.info("[Caretaker] Restaurando pedido '{}' al estado: {}", orderId, memento.getStatus());
        return Optional.of(memento);
    }

    /**
     * Devuelve el historial completo de snapshots de un pedido (sin eliminarlo).
     */
    public List<OrderMemento> getHistory(String orderId) {
        Deque<OrderMemento> stack = history.getOrDefault(orderId, new ArrayDeque<>());
        return new ArrayList<>(stack);
    }

    /**
     * Indica cuántos snapshots hay almacenados para un pedido.
     */
    public int getSnapshotCount(String orderId) {
        Deque<OrderMemento> stack = history.get(orderId);
        return stack == null ? 0 : stack.size();
    }

    /**
     * Limpia todo el historial de un pedido.
     */
    public void clearHistory(String orderId) {
        history.remove(orderId);
    }
}

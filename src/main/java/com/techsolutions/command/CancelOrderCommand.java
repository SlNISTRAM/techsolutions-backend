package com.techsolutions.command;

import com.techsolutions.command.memento.OrderCaretaker;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Comando para cancelar un pedido.
 *
 * <p>Un pedido CONFIRMED no puede cancelarse (regla de negocio en {@link Order}).
 * El undo restaura el estado previo a la cancelación desde el Caretaker.</p>
 */
public class CancelOrderCommand implements OrderCommand {

    private static final Logger log = LoggerFactory.getLogger(CancelOrderCommand.class);

    private final Order order;
    private final OrderCaretaker caretaker;

    public CancelOrderCommand(Order order, OrderCaretaker caretaker) {
        this.order     = order;
        this.caretaker = caretaker;
    }

    @Override
    public void execute() {
        caretaker.save(order.createMemento());
        OrderStatus previousStatus = order.getStatus();
        order.cancel();
        log.info("[CancelOrder] Pedido '{}' cancelado. Estado anterior: {}", order.getId(), previousStatus);
    }

    @Override
    public void undo() {
        caretaker.undo(order.getId()).ifPresentOrElse(
            memento -> {
                order.restoreFromMemento(memento);
                log.info("[CancelOrder UNDO] Cancelación revertida en pedido '{}'. Estado restaurado: {}",
                        order.getId(), order.getStatus());
            },
            () -> log.warn("[CancelOrder UNDO] No hay snapshot previo para pedido '{}'.", order.getId())
        );
    }

    @Override
    public String getDescription() {
        return String.format("Cancelar pedido '%s' (estado actual: %s)", order.getId(), order.getStatus());
    }

    @Override
    public String getOrderId() { return order.getId(); }
}

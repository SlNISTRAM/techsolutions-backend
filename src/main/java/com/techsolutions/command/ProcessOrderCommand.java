package com.techsolutions.command;

import com.techsolutions.command.memento.OrderCaretaker;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Comando para pasar un pedido al estado {@code PROCESSING}.
 *
 * <p>Antes de ejecutar, guarda el estado actual como memento para que
 * la operación sea completamente reversible con {@link #undo()}.</p>
 */
public class ProcessOrderCommand implements OrderCommand {

    private static final Logger log = LoggerFactory.getLogger(ProcessOrderCommand.class);

    private final Order order;
    private final OrderCaretaker caretaker;

    public ProcessOrderCommand(Order order, OrderCaretaker caretaker) {
        this.order     = order;
        this.caretaker = caretaker;
    }

    @Override
    public void execute() {
        // Guardar estado previo antes de modificar
        caretaker.save(order.createMemento());
        order.process();
        log.info("[ProcessOrder] Pedido '{}' → estado PROCESSING.", order.getId());
    }

    @Override
    public void undo() {
        caretaker.undo(order.getId()).ifPresentOrElse(
            memento -> {
                order.restoreFromMemento(memento);
                log.info("[ProcessOrder UNDO] Pedido '{}' revertido a estado: {}", order.getId(), memento.getStatus());
            },
            () -> log.warn("[ProcessOrder UNDO] No hay snapshot previo para pedido '{}'.", order.getId())
        );
    }

    @Override
    public String getDescription() {
        return String.format("Procesar pedido '%s' → PROCESSING", order.getId());
    }

    @Override
    public String getOrderId() { return order.getId(); }
}

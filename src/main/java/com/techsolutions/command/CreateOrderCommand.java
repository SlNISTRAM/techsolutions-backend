package com.techsolutions.command;

import com.techsolutions.command.memento.OrderCaretaker;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

/**
 * Comando para crear un nuevo pedido y dejarlo en estado {@code PENDING}.
 *
 * <p>Guarda un snapshot inicial en el {@link OrderCaretaker} para que el
 * undo pueda marcarlo como inexistente (logically deleted).</p>
 */
public class CreateOrderCommand implements OrderCommand {

    private static final Logger log = LoggerFactory.getLogger(CreateOrderCommand.class);

    private final Order order;
    private final OrderCaretaker caretaker;

    public CreateOrderCommand(Order order, OrderCaretaker caretaker) {
        this.order     = order;
        this.caretaker = caretaker;
    }

    @Override
    public void execute() {
        // El pedido ya se construyó como PENDING; guardamos snapshot inicial
        caretaker.save(order.createMemento());
        log.info("[CreateOrder] Pedido '{}' creado para cliente '{}'. Total: S/ {}, Items: {}",
                order.getId(), order.getCustomerName(), order.getTotalAmount(), order.getItems());
    }

    @Override
    public void undo() {
        // Restaurar al estado capturado antes de la creación (lógicamente vacío)
        caretaker.undo(order.getId()).ifPresentOrElse(
            memento -> {
                order.restoreFromMemento(memento);
                log.info("[CreateOrder UNDO] Pedido '{}' revertido al estado: {}", order.getId(), memento.getStatus());
            },
            () -> log.warn("[CreateOrder UNDO] No hay estado previo para revertir el pedido '{}'.", order.getId())
        );
    }

    @Override
    public String getDescription() {
        return String.format("Crear pedido '%s' para cliente '%s' | Items: %s | Total: S/ %.2f",
                order.getId(), order.getCustomerName(), order.getItems(), order.getTotalAmount());
    }

    @Override
    public String getOrderId() { return order.getId(); }
}

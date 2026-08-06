package com.techsolutions.command;

import com.techsolutions.command.memento.OrderCaretaker;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Comando para aplicar un descuento porcentual sobre el total del pedido.
 *
 * <p>Registra el total original antes del descuento para que {@link #undo()}
 * pueda restaurarlo con exactitud sin depender solo del snapshot de Memento.</p>
 */
public class ApplyDiscountCommand implements OrderCommand {

    private static final Logger log = LoggerFactory.getLogger(ApplyDiscountCommand.class);

    private final Order order;
    private final double discountPercentage;
    private final OrderCaretaker caretaker;
    private double originalTotal;

    public ApplyDiscountCommand(Order order, double discountPercentage, OrderCaretaker caretaker) {
        this.order              = order;
        this.discountPercentage = discountPercentage;
        this.caretaker          = caretaker;
    }

    @Override
    public void execute() {
        originalTotal = order.getTotalAmount();
        caretaker.save(order.createMemento());

        order.applyDiscount(discountPercentage);

        log.info("[ApplyDiscount] Pedido '{}': descuento {}% aplicado. Total: S/ {} → S/ {}",
                order.getId(), discountPercentage, originalTotal, order.getTotalAmount());
    }

    @Override
    public void undo() {
        caretaker.undo(order.getId()).ifPresentOrElse(
            memento -> {
                order.restoreFromMemento(memento);
                log.info("[ApplyDiscount UNDO] Descuento revertido en pedido '{}'. Total restaurado: S/ {}",
                        order.getId(), order.getTotalAmount());
            },
            () -> {
                order.removeDiscount(originalTotal);
                log.warn("[ApplyDiscount UNDO] Sin memento previo; revertido manualmente. Total: S/ {}", originalTotal);
            }
        );
    }

    @Override
    public String getDescription() {
        return String.format("Aplicar descuento de %.1f%% al pedido '%s'", discountPercentage, order.getId());
    }

    @Override
    public String getOrderId() { return order.getId(); }
}

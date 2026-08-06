package com.techsolutions.command.memento;

import com.techsolutions.command.OrderStatus;

import java.time.LocalDateTime;

/**
 * Memento — snapshot inmutable del estado de un pedido en un momento dado.
 *
 * <p>Solo el {@link com.techsolutions.command.Order} (Originador) puede
 * crear instancias de este objeto. El {@link OrderCaretaker} las almacena
 * sin poder acceder ni modificar su contenido interno más allá de los
 * getters necesarios para la restauración.</p>
 *
 * <p><b>GRASP — Bajo Acoplamiento:</b> el Memento no conoce ni al Caretaker
 * ni al Invoker; solo sabe guardar el estado del Order.</p>
 */
public class OrderMemento {

    private final String      orderId;
    private final OrderStatus status;
    private final double      totalAmount;
    private final double      discount;
    private final LocalDateTime savedAt;

    public OrderMemento(String orderId, OrderStatus status,
                        double totalAmount, double discount, LocalDateTime savedAt) {
        this.orderId     = orderId;
        this.status      = status;
        this.totalAmount = totalAmount;
        this.discount    = discount;
        this.savedAt     = savedAt;
    }

    public String      getOrderId()    { return orderId; }
    public OrderStatus getStatus()     { return status; }
    public double      getTotalAmount(){ return totalAmount; }
    public double      getDiscount()   { return discount; }
    public LocalDateTime getSavedAt()  { return savedAt; }

    @Override
    public String toString() {
        return String.format("OrderMemento{orderId='%s', status=%s, total=%.2f, discount=%.1f%%, savedAt=%s}",
                orderId, status, totalAmount, discount, savedAt);
    }
}

package com.techsolutions.command;

import com.techsolutions.command.memento.OrderMemento;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Entidad Order — representa un pedido de cliente en TechSolutions S.A.
 *
 * <p>Cumple doble rol en el diseño:</p>
 * <ul>
 *   <li><b>Receptor (Command)</b>: recibe las operaciones de los comandos
 *       (process, applyDiscount, cancel) que modifican su estado.</li>
 *   <li><b>Originador (Memento)</b>: crea y restaura snapshots de su estado
 *       mediante {@link #createMemento()} y {@link #restoreFromMemento(OrderMemento)}.</li>
 * </ul>
 *
 * <p><b>GRASP — Alta Cohesión:</b> esta clase solo conoce su propio estado;
 * no sabe nada de comandos, historial ni de HTTP.</p>
 */
public class Order {

    private final String id;
    private final String customerName;
    private final List<String> items;
    private double totalAmount;
    private double discount;
    private OrderStatus status;
    private final LocalDateTime createdAt;

    public Order(String id, String customerName, List<String> items, double totalAmount) {
        if (id == null || id.isBlank())            throw new IllegalArgumentException("El ID del pedido es obligatorio.");
        if (customerName == null || customerName.isBlank()) throw new IllegalArgumentException("El nombre del cliente es obligatorio.");
        if (items == null || items.isEmpty())      throw new IllegalArgumentException("El pedido debe tener al menos un ítem.");
        if (totalAmount < 0)                       throw new IllegalArgumentException("El total no puede ser negativo.");

        this.id           = id;
        this.customerName = customerName;
        this.items        = new ArrayList<>(items);
        this.totalAmount  = totalAmount;
        this.discount     = 0.0;
        this.status       = OrderStatus.PENDING;
        this.createdAt    = LocalDateTime.now();
    }

    // ─── Operaciones de dominio (receptoras de comandos) ────────────────────

    public void process() {
        if (status == OrderStatus.CANCELLED) {
            throw new IllegalStateException("No se puede procesar un pedido cancelado.");
        }
        if (status == OrderStatus.CONFIRMED) {
            throw new IllegalStateException("El pedido ya está confirmado.");
        }
        this.status = OrderStatus.PROCESSING;
    }

    public void confirm() {
        if (status != OrderStatus.PROCESSING && status != OrderStatus.DISCOUNTED) {
            throw new IllegalStateException("Solo se puede confirmar un pedido en estado PROCESSING o DISCOUNTED.");
        }
        this.status = OrderStatus.CONFIRMED;
    }

    public void applyDiscount(double discountPercentage) {
        if (status == OrderStatus.CANCELLED || status == OrderStatus.CONFIRMED) {
            throw new IllegalStateException("No se puede aplicar descuento en estado " + status);
        }
        if (discountPercentage <= 0 || discountPercentage > 100) {
            throw new IllegalArgumentException("El descuento debe estar entre 1 y 100.");
        }
        this.discount     = discountPercentage;
        this.totalAmount  = Math.round(totalAmount * (1 - discountPercentage / 100.0) * 100.0) / 100.0;
        this.status       = OrderStatus.DISCOUNTED;
    }

    public void removeDiscount(double originalTotal) {
        this.discount    = 0.0;
        this.totalAmount = originalTotal;
        this.status      = OrderStatus.PROCESSING;
    }

    public void cancel() {
        if (status == OrderStatus.CONFIRMED) {
            throw new IllegalStateException("No se puede cancelar un pedido ya confirmado.");
        }
        this.status = OrderStatus.CANCELLED;
    }

    public void revertToPending() {
        this.status = OrderStatus.PENDING;
    }

    public void revertToProcessing() {
        this.status = OrderStatus.PROCESSING;
    }

    // ─── Memento — Originator methods ───────────────────────────────────────

    /**
     * Captura el estado actual del pedido en un {@link OrderMemento}.
     */
    public OrderMemento createMemento() {
        return new OrderMemento(id, status, totalAmount, discount, LocalDateTime.now());
    }

    /**
     * Restaura el estado del pedido desde un {@link OrderMemento} guardado.
     */
    public void restoreFromMemento(OrderMemento memento) {
        this.status      = memento.getStatus();
        this.totalAmount = memento.getTotalAmount();
        this.discount    = memento.getDiscount();
    }

    // ─── Getters ─────────────────────────────────────────────────────────────

    public String       getId()           { return id; }
    public String       getCustomerName() { return customerName; }
    public List<String> getItems()        { return Collections.unmodifiableList(items); }
    public double       getTotalAmount()  { return totalAmount; }
    public double       getDiscount()     { return discount; }
    public OrderStatus  getStatus()       { return status; }
    public LocalDateTime getCreatedAt()  { return createdAt; }

    @Override
    public String toString() {
        return String.format("Order{id='%s', customer='%s', total=%.2f, discount=%.1f%%, status=%s}",
                id, customerName, totalAmount, discount, status);
    }
}

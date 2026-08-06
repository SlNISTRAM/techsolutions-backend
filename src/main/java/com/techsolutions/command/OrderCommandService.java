package com.techsolutions.command;

import com.techsolutions.command.memento.OrderCaretaker;
import com.techsolutions.command.memento.OrderMemento;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

/**
 * Servicio de pedidos — actúa como <b>Controlador GRASP</b>.
 *
 * <p>El Controlador GRASP es el primer objeto más allá de la capa UI que
 * recibe y coordina una operación del sistema. Este servicio:</p>
 * <ul>
 *   <li>Recibe solicitudes del {@link com.techsolutions.command.controller.OrderController}.</li>
 *   <li>Crea los comandos concretos ({@link CreateOrderCommand}, etc.).</li>
 *   <li>Los delega al {@link OrderInvoker} para su ejecución.</li>
 *   <li>Consulta el {@link OrderCaretaker} para el historial de snapshots.</li>
 * </ul>
 *
 * <p><b>GRASP — Alta Cohesión:</b> cada método tiene una única responsabilidad
 * bien definida. No contiene lógica de HTTP ni de persistencia directa.</p>
 *
 * <p><b>GRASP — Bajo Acoplamiento:</b> depende de abstracciones ({@link OrderCommand},
 * {@link OrderInvoker}, {@link OrderCaretaker}), nunca de implementaciones concretas
 * de comandos en su firma pública.</p>
 */
@Service
public class OrderCommandService {

    private static final Logger log = LoggerFactory.getLogger(OrderCommandService.class);

    private final OrderInvoker   invoker;
    private final OrderCaretaker caretaker;
    private final Map<String, Order> orders = new HashMap<>();

    public OrderCommandService(OrderInvoker invoker, OrderCaretaker caretaker) {
        this.invoker   = invoker;
        this.caretaker = caretaker;
    }

    /**
     * Crea un nuevo pedido y lo registra (ejecuta {@link CreateOrderCommand}).
     */
    public Order createOrder(String customerName, List<String> items, double totalAmount) {
        String id    = "ORD-" + UUID.randomUUID().toString().substring(0, 6).toUpperCase();
        Order  order = new Order(id, customerName, items, totalAmount);
        orders.put(id, order);

        invoker.execute(new CreateOrderCommand(order, caretaker));
        return order;
    }

    /**
     * Mueve el pedido a estado PROCESSING (ejecuta {@link ProcessOrderCommand}).
     */
    public Order processOrder(String orderId) {
        Order order = findOrThrow(orderId);
        invoker.execute(new ProcessOrderCommand(order, caretaker));
        return order;
    }

    /**
     * Aplica un descuento al pedido (ejecuta {@link ApplyDiscountCommand}).
     */
    public Order applyDiscount(String orderId, double discountPercentage) {
        Order order = findOrThrow(orderId);
        invoker.execute(new ApplyDiscountCommand(order, discountPercentage, caretaker));
        return order;
    }

    /**
     * Cancela el pedido (ejecuta {@link CancelOrderCommand}).
     */
    public Order cancelOrder(String orderId) {
        Order order = findOrThrow(orderId);
        invoker.execute(new CancelOrderCommand(order, caretaker));
        return order;
    }

    /**
     * Deshace la última acción ejecutada sobre cualquier pedido.
     */
    public void undoLastCommand() {
        invoker.undoLast();
    }

    /**
     * Restaura un pedido a su estado guardado más reciente (Memento directo).
     */
    public Order restoreOrder(String orderId) {
        Order order = findOrThrow(orderId);
        caretaker.undo(orderId).ifPresentOrElse(
            order::restoreFromMemento,
            () -> { throw new IllegalStateException("No hay snapshots previos para el pedido: " + orderId); }
        );
        log.info("Pedido '{}' restaurado al estado: {}", orderId, order.getStatus());
        return order;
    }

    public Collection<Order> getAllOrders()  { return orders.values(); }

    public Order findById(String orderId)    { return findOrThrow(orderId); }

    public List<String> getCommandHistory()  { return invoker.getCommandHistory(); }

    public List<OrderMemento> getOrderSnapshots(String orderId) {
        return caretaker.getHistory(orderId);
    }

    private Order findOrThrow(String orderId) {
        Order order = orders.get(orderId.toUpperCase());
        if (order == null) throw new IllegalArgumentException("Pedido no encontrado: " + orderId);
        return order;
    }
}

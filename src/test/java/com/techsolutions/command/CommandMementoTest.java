package com.techsolutions.command;

import com.techsolutions.command.memento.OrderCaretaker;
import com.techsolutions.command.memento.OrderMemento;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("Pruebas Unitarias — Patrones Command y Memento")
class CommandMementoTest {

    private Order order;
    private OrderInvoker invoker;
    private OrderCaretaker caretaker;

    @BeforeEach
    void setUp() {
        order = new Order("ORD-001", "Ana Torres", List.of("Laptop Dell"), 2500.0);
        invoker = new OrderInvoker();
        caretaker = new OrderCaretaker();
    }

    @Test
    @DisplayName("CreateOrderCommand registra la orden en estado PENDING y guarda snapshot")
    void testCreateOrderCommand() {
        CreateOrderCommand cmd = new CreateOrderCommand(order, caretaker);
        invoker.execute(cmd);

        assertEquals(OrderStatus.PENDING, order.getStatus());
        assertEquals(1, caretaker.getSnapshotCount(order.getId()));
    }

    @Test
    @DisplayName("ProcessOrderCommand cambia estado a PROCESSING y permite undo")
    void testProcessOrderCommandAndUndo() {
        invoker.execute(new CreateOrderCommand(order, caretaker));
        invoker.execute(new ProcessOrderCommand(order, caretaker));

        assertEquals(OrderStatus.PROCESSING, order.getStatus());

        // Undo debe revertir a PENDING
        invoker.undoLast();
        assertEquals(OrderStatus.PENDING, order.getStatus());
    }

    @Test
    @DisplayName("ApplyDiscountCommand aplica descuento y undo restaura el total original")
    void testApplyDiscountCommandAndUndo() {
        invoker.execute(new CreateOrderCommand(order, caretaker));
        invoker.execute(new ProcessOrderCommand(order, caretaker));
        invoker.execute(new ApplyDiscountCommand(order, 10.0, caretaker)); // 10% de desc sobre 2500 = 2250

        assertEquals(OrderStatus.DISCOUNTED, order.getStatus());
        assertEquals(2250.0, order.getTotalAmount());

        // Undo descuenta el comando
        invoker.undoLast();
        assertEquals(2500.0, order.getTotalAmount());
        assertEquals(OrderStatus.PROCESSING, order.getStatus());
    }

    @Test
    @DisplayName("CancelOrderCommand cambia estado a CANCELLED y memento permite restauración")
    void testCancelOrderCommandAndMementoRestore() {
        invoker.execute(new CreateOrderCommand(order, caretaker));
        invoker.execute(new ProcessOrderCommand(order, caretaker));
        invoker.execute(new CancelOrderCommand(order, caretaker));

        assertEquals(OrderStatus.CANCELLED, order.getStatus());

        // Restaurar estado vía Caretaker
        OrderMemento memento = caretaker.undo(order.getId()).orElseThrow();
        order.restoreFromMemento(memento);

        assertEquals(OrderStatus.PROCESSING, order.getStatus());
    }
}

package com.techsolutions.command.controller;

import com.techsolutions.command.Order;
import com.techsolutions.command.OrderCommandService;
import com.techsolutions.command.dto.CreateOrderRequest;
import com.techsolutions.command.dto.OrderResponse;
import com.techsolutions.command.memento.OrderMemento;
import jakarta.validation.Valid;
import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Controlador REST para el módulo de pedidos (Command + Memento).
 *
 * <p><b>GRASP — Controlador:</b> esta clase es un <em>controlador de fachada</em>
 * puro: recibe peticiones HTTP y delega inmediatamente al
 * {@link OrderCommandService}. No contiene lógica de negocio.</p>
 *
 * <ul>
 *   <li>POST /api/orders                         → crear pedido</li>
 *   <li>GET  /api/orders                         → listar pedidos</li>
 *   <li>GET  /api/orders/{id}                    → obtener pedido</li>
 *   <li>PUT  /api/orders/{id}/process            → procesar pedido</li>
 *   <li>PUT  /api/orders/{id}/discount?pct=N     → aplicar descuento</li>
 *   <li>PUT  /api/orders/{id}/cancel             → cancelar pedido</li>
 *   <li>POST /api/orders/undo                    → deshacer último comando</li>
 *   <li>PUT  /api/orders/{id}/restore            → restaurar estado (Memento)</li>
 *   <li>GET  /api/orders/{id}/snapshots          → historial de snapshots</li>
 *   <li>GET  /api/orders/history                 → historial de comandos ejecutados</li>
 * </ul>
 */
@RestController
@RequestMapping("/api/orders")
public class OrderController {

    private final OrderCommandService orderCommandService;

    public OrderController(OrderCommandService orderCommandService) {
        this.orderCommandService = orderCommandService;
    }

    @PostMapping
    public ResponseEntity<OrderResponse> createOrder(@Valid @RequestBody CreateOrderRequest request) {
        Order order = orderCommandService.createOrder(
                request.customerName(), request.items(), request.totalAmount());
        return ResponseEntity.status(HttpStatus.CREATED).body(OrderResponse.from(order));
    }

    @GetMapping
    public ResponseEntity<List<OrderResponse>> getAllOrders() {
        List<OrderResponse> orders = orderCommandService.getAllOrders().stream()
                .map(OrderResponse::from)
                .collect(Collectors.toList());
        return ResponseEntity.ok(orders);
    }

    @GetMapping("/{id}")
    public ResponseEntity<OrderResponse> getOrder(@PathVariable String id) {
        return ResponseEntity.ok(OrderResponse.from(orderCommandService.findById(id)));
    }

    @PutMapping("/{id}/process")
    public ResponseEntity<OrderResponse> processOrder(@PathVariable String id) {
        return ResponseEntity.ok(OrderResponse.from(orderCommandService.processOrder(id)));
    }

    @PutMapping("/{id}/discount")
    public ResponseEntity<OrderResponse> applyDiscount(
            @PathVariable String id,
            @RequestParam @DecimalMin("1") @DecimalMax("100") double pct) {
        return ResponseEntity.ok(OrderResponse.from(orderCommandService.applyDiscount(id, pct)));
    }

    @PutMapping("/{id}/cancel")
    public ResponseEntity<OrderResponse> cancelOrder(@PathVariable String id) {
        return ResponseEntity.ok(OrderResponse.from(orderCommandService.cancelOrder(id)));
    }

    @PostMapping("/undo")
    public ResponseEntity<String> undoLastCommand() {
        orderCommandService.undoLastCommand();
        return ResponseEntity.ok("Último comando deshecho exitosamente.");
    }

    @PutMapping("/{id}/restore")
    public ResponseEntity<OrderResponse> restoreOrder(@PathVariable String id) {
        return ResponseEntity.ok(OrderResponse.from(orderCommandService.restoreOrder(id)));
    }

    @GetMapping("/{id}/snapshots")
    public ResponseEntity<List<OrderMemento>> getSnapshots(@PathVariable String id) {
        return ResponseEntity.ok(orderCommandService.getOrderSnapshots(id));
    }

    @GetMapping("/history")
    public ResponseEntity<List<String>> getCommandHistory() {
        return ResponseEntity.ok(orderCommandService.getCommandHistory());
    }

    @ExceptionHandler({IllegalArgumentException.class, IllegalStateException.class})
    public ResponseEntity<String> handleErrors(RuntimeException ex) {
        return ResponseEntity.badRequest().body("Error: " + ex.getMessage());
    }
}

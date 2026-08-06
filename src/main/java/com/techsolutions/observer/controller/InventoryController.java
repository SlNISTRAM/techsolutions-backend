package com.techsolutions.observer.controller;

import com.techsolutions.observer.Product;
import com.techsolutions.observer.ProductInventoryService;
import com.techsolutions.observer.dto.ProductRequest;
import com.techsolutions.observer.dto.ProductResponse;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Controlador REST para el módulo de inventario.
 * Demuestra el patrón Observer: las operaciones de stock disparan
 * automáticamente los observadores registrados en cada producto.
 *
 * <ul>
 *   <li>GET  /api/inventory                              → listar todos los productos</li>
 *   <li>GET  /api/inventory/{id}                        → obtener producto por ID</li>
 *   <li>GET  /api/inventory/alerts/stock                → productos bajo el mínimo</li>
 *   <li>POST /api/inventory                             → registrar nuevo producto</li>
 *   <li>PUT  /api/inventory/{id}/decrease?qty=N         → descontar stock (dispara Observer)</li>
 *   <li>PUT  /api/inventory/{id}/increase?qty=N         → reabastecer stock</li>
 *   <li>GET  /api/inventory/notifications/gerente       → alertas recibidas por Gerente</li>
 *   <li>GET  /api/inventory/notifications/compras       → órdenes generadas por Compras</li>
 * </ul>
 */
@RestController
@RequestMapping("/api/inventory")
public class InventoryController {

    private final ProductInventoryService inventoryService;

    public InventoryController(ProductInventoryService inventoryService) {
        this.inventoryService = inventoryService;
    }

    @GetMapping
    public ResponseEntity<List<ProductResponse>> getAllProducts() {
        List<ProductResponse> products = inventoryService.getAllProducts().stream()
                .map(ProductResponse::from)
                .collect(Collectors.toList());
        return ResponseEntity.ok(products);
    }

    @GetMapping("/{id}")
    public ResponseEntity<ProductResponse> getProduct(@PathVariable String id) {
        Product product = inventoryService.findById(id);
        return ResponseEntity.ok(ProductResponse.from(product));
    }

    @GetMapping("/alerts/stock")
    public ResponseEntity<List<ProductResponse>> getProductsBelowMinimum() {
        List<ProductResponse> critical = inventoryService.getProductsBelowMinimum().stream()
                .map(ProductResponse::from)
                .collect(Collectors.toList());
        return ResponseEntity.ok(critical);
    }

    @PostMapping
    public ResponseEntity<ProductResponse> registerProduct(@Valid @RequestBody ProductRequest request) {
        Product product = inventoryService.registerProduct(
                request.name(), request.category(), request.basePrice(),
                request.stock(), request.minimumStock()
        );
        return ResponseEntity.status(HttpStatus.CREATED).body(ProductResponse.from(product));
    }

    @PutMapping("/{id}/decrease")
    public ResponseEntity<ProductResponse> decreaseStock(
            @PathVariable String id,
            @RequestParam @Min(1) int qty) {
        Product product = inventoryService.decreaseStock(id, qty);
        return ResponseEntity.ok(ProductResponse.from(product));
    }

    @PutMapping("/{id}/increase")
    public ResponseEntity<ProductResponse> increaseStock(
            @PathVariable String id,
            @RequestParam @Min(1) int qty) {
        Product product = inventoryService.increaseStock(id, qty);
        return ResponseEntity.ok(ProductResponse.from(product));
    }

    @GetMapping("/notifications/gerente")
    public ResponseEntity<List<String>> getGerenteAlerts() {
        return ResponseEntity.ok(inventoryService.getGerenteAlerts());
    }

    @GetMapping("/notifications/compras")
    public ResponseEntity<List<String>> getComprasOrders() {
        return ResponseEntity.ok(inventoryService.getComprasOrders());
    }

    @ExceptionHandler({IllegalArgumentException.class})
    public ResponseEntity<String> handleBadRequest(IllegalArgumentException ex) {
        return ResponseEntity.badRequest().body("Error: " + ex.getMessage());
    }
}

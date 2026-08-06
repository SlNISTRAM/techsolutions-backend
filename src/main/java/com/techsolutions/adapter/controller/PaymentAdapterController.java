package com.techsolutions.adapter.controller;

import com.techsolutions.adapter.PaymentAdapterService;
import com.techsolutions.adapter.dto.PaymentRequest;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

/**
 * Controlador REST para gestionar los adaptadores de pago.
 * Expone endpoints para activar/desactivar adaptadores y procesar pagos.
 *
 * <ul>
 *   <li>GET  /api/payments/adapters               → estado de todos los adaptadores</li>
 *   <li>PUT  /api/payments/adapters/{name}/enable  → habilitar adaptador</li>
 *   <li>PUT  /api/payments/adapters/{name}/disable → deshabilitar adaptador</li>
 *   <li>POST /api/payments/process                 → procesar un pago</li>
 * </ul>
 */
@RestController
@RequestMapping("/api/payments")
public class PaymentAdapterController {

    private final PaymentAdapterService paymentAdapterService;

    public PaymentAdapterController(PaymentAdapterService paymentAdapterService) {
        this.paymentAdapterService = paymentAdapterService;
    }

    @GetMapping("/adapters")
    public ResponseEntity<Map<String, Boolean>> getAdaptersStatus() {
        Map<String, Boolean> status = paymentAdapterService.getAdaptersStatus();
        return ResponseEntity.ok(status);
    }

    @PutMapping("/adapters/{adapterName}/enable")
    public ResponseEntity<String> enableAdapter(@PathVariable String adapterName) {
        paymentAdapterService.enableAdapter(adapterName);
        return ResponseEntity.ok("Adaptador '" + adapterName.toUpperCase() + "' habilitado exitosamente.");
    }

    @PutMapping("/adapters/{adapterName}/disable")
    public ResponseEntity<String> disableAdapter(@PathVariable String adapterName) {
        paymentAdapterService.disableAdapter(adapterName);
        return ResponseEntity.ok("Adaptador '" + adapterName.toUpperCase() + "' deshabilitado exitosamente.");
    }

    @PostMapping("/process")
    public ResponseEntity<String> processPayment(@Valid @RequestBody PaymentRequest request) {
        String result = paymentAdapterService.processPayment(
                request.adapterName(),
                request.amount(),
                request.currency()
        );
        return ResponseEntity.ok(result);
    }

    @ExceptionHandler({IllegalArgumentException.class, IllegalStateException.class})
    public ResponseEntity<String> handleAdapterExceptions(RuntimeException ex) {
        return ResponseEntity.badRequest().body("Error: " + ex.getMessage());
    }
}

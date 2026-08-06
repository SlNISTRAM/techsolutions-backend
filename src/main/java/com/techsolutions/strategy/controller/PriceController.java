package com.techsolutions.strategy.controller;

import com.techsolutions.strategy.PriceCalculatorContext;
import com.techsolutions.strategy.StrategyParams;
import com.techsolutions.strategy.dto.PriceCalculationRequest;
import com.techsolutions.strategy.dto.PriceCalculationResponse;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

/**
 * Controlador REST para el módulo de estrategias de precios.
 * Demuestra el patrón Strategy: el contexto selecciona la estrategia
 * en tiempo de ejecución y delega el cálculo a ella.
 *
 * <ul>
 *   <li>GET  /api/pricing/strategies              → listar estrategias disponibles</li>
 *   <li>GET  /api/pricing/strategies/current      → estrategia activa</li>
 *   <li>PUT  /api/pricing/strategies/{name}       → cambiar estrategia activa</li>
 *   <li>POST /api/pricing/calculate               → calcular precio con estrategia específica</li>
 * </ul>
 */
@RestController
@RequestMapping("/api/pricing")
public class PriceController {

    private final PriceCalculatorContext context;

    public PriceController(PriceCalculatorContext context) {
        this.context = context;
    }

    @GetMapping("/strategies")
    public ResponseEntity<Map<String, String>> listStrategies() {
        return ResponseEntity.ok(context.getAllStrategies());
    }

    @GetMapping("/strategies/current")
    public ResponseEntity<Map<String, String>> getCurrentStrategy() {
        return ResponseEntity.ok(Map.of(
                "name",        context.getCurrentStrategyName(),
                "description", context.getCurrentStrategyDescription()
        ));
    }

    @PutMapping("/strategies/{name}")
    public ResponseEntity<String> setStrategy(@PathVariable String name) {
        context.setStrategy(name);
        return ResponseEntity.ok("Estrategia de precio cambiada a: " + name.toUpperCase());
    }

    @PostMapping("/calculate")
    public ResponseEntity<PriceCalculationResponse> calculatePrice(
            @Valid @RequestBody PriceCalculationRequest request) {

        // Seleccionar estrategia para esta operación
        context.setStrategy(request.strategyName());

        // Construir parámetros a partir del request
        StrategyParams.Builder paramsBuilder = StrategyParams.builder();

        if (request.discountPercentage() != null) {
            paramsBuilder.discountPercentage(request.discountPercentage());
        }
        if (request.demandMultiplier() != null) {
            paramsBuilder.demandMultiplier(request.demandMultiplier());
        }
        if (request.bulkThreshold() != null) {
            paramsBuilder.bulkThreshold(request.bulkThreshold());
        }
        if (request.taxRate() != null) {
            paramsBuilder.taxRate(request.taxRate());
        }
        StrategyParams params = paramsBuilder.build();

        double unitPrice  = context.calculatePrice(request.basePrice(), request.quantity(), params);
        double totalPrice = Math.round(unitPrice * request.quantity() * 100.0) / 100.0;

        PriceCalculationResponse response = new PriceCalculationResponse(
                request.basePrice(),
                request.quantity(),
                context.getCurrentStrategyName(),
                unitPrice,
                totalPrice,
                context.getCurrentStrategyDescription()
        );

        return ResponseEntity.ok(response);
    }

    @ExceptionHandler({IllegalArgumentException.class})
    public ResponseEntity<String> handleBadRequest(IllegalArgumentException ex) {
        return ResponseEntity.badRequest().body("Error: " + ex.getMessage());
    }
}

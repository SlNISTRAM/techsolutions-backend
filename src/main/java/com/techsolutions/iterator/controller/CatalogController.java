package com.techsolutions.iterator.controller;

import com.techsolutions.iterator.ProductCatalog;
import com.techsolutions.iterator.ProductFilter;
import com.techsolutions.iterator.ProductIterator;
import com.techsolutions.observer.Product;
import com.techsolutions.observer.dto.ProductResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * Controlador REST para el catálogo de productos con soporte de Iterator.
 *
 * <p>Demuestra el patrón Iterator: el controlador llama a
 * {@link ProductCatalog#createIterator(ProductFilter)} y recorre la
 * colección mediante la interfaz {@link ProductIterator}, sin conocer
 * la implementación concreta ni la estructura interna.</p>
 *
 * <ul>
 *   <li>GET /api/catalog                           → listar todos (sin filtro)</li>
 *   <li>GET /api/catalog/search                    → buscar con filtros y paginación</li>
 *   <li>GET /api/catalog/categories                → categorías únicas disponibles</li>
 *   <li>GET /api/catalog/stats                     → estadísticas del catálogo</li>
 * </ul>
 */
@RestController
@RequestMapping("/api/catalog")
public class CatalogController {

    private final ProductCatalog catalog;

    public CatalogController(ProductCatalog catalog) {
        this.catalog = catalog;
    }

    @GetMapping
    public ResponseEntity<List<ProductResponse>> listAll() {
        ProductIterator iterator = catalog.createIterator();
        List<ProductResponse> products = new ArrayList<>();
        while (iterator.hasNext()) {
            products.add(ProductResponse.from(iterator.next()));
        }
        return ResponseEntity.ok(products);
    }

    @GetMapping("/search")
    public ResponseEntity<Map<String, Object>> search(
            @RequestParam(required = false) String category,
            @RequestParam(required = false, defaultValue = "0")    double minPrice,
            @RequestParam(required = false, defaultValue = "999999") double maxPrice,
            @RequestParam(required = false, defaultValue = "false") boolean onlyAvailable,
            @RequestParam(required = false, defaultValue = "0")    int page,
            @RequestParam(required = false, defaultValue = "10")   int pageSize) {

        ProductFilter filter = ProductFilter.builder()
                .category(category)
                .minPrice(minPrice)
                .maxPrice(maxPrice)
                .onlyAvailable(onlyAvailable)
                .page(page)
                .pageSize(pageSize)
                .build();

        ProductIterator iterator = catalog.createIterator(filter);

        List<ProductResponse> results = new ArrayList<>();
        while (iterator.hasNext()) {
            results.add(ProductResponse.from(iterator.next()));
        }

        Map<String, Object> response = new LinkedHashMap<>();
        response.put("page",          page);
        response.put("pageSize",      pageSize);
        response.put("totalInPage",   results.size());
        response.put("totalCatalog",  catalog.totalProducts());
        response.put("products",      results);

        return ResponseEntity.ok(response);
    }

    @GetMapping("/categories")
    public ResponseEntity<List<String>> getCategories() {
        ProductIterator iterator = catalog.createIterator();
        List<String> categories = new ArrayList<>();
        while (iterator.hasNext()) {
            String cat = iterator.next().getCategory();
            if (!categories.contains(cat)) {
                categories.add(cat);
            }
        }
        return ResponseEntity.ok(categories);
    }

    @GetMapping("/stats")
    public ResponseEntity<Map<String, Object>> getStats() {
        ProductIterator allIterator   = catalog.createIterator();
        ProductIterator availIterator = catalog.createIterator(
                ProductFilter.builder().onlyAvailable(true).build());
        ProductIterator criticalIter  = catalog.createIterator();

        int total     = allIterator.totalElements();
        int available = availIterator.totalElements();

        double maxPrice = 0, minPrice = Double.MAX_VALUE, sumPrice = 0;
        int countCritical = 0;
        allIterator.reset();
        while (allIterator.hasNext()) {
            Product p = allIterator.next();
            sumPrice += p.getBasePrice();
            if (p.getBasePrice() > maxPrice) maxPrice = p.getBasePrice();
            if (p.getBasePrice() < minPrice) minPrice = p.getBasePrice();
        }
        criticalIter.reset();
        while (criticalIter.hasNext()) {
            if (criticalIter.next().isBelowMinimum()) countCritical++;
        }

        Map<String, Object> stats = new LinkedHashMap<>();
        stats.put("totalProducts",       total);
        stats.put("availableProducts",   available);
        stats.put("criticalStock",       countCritical);
        stats.put("averagePrice",        total > 0 ? Math.round((sumPrice / total) * 100.0) / 100.0 : 0);
        stats.put("maxPrice",            maxPrice == 0 ? 0 : maxPrice);
        stats.put("minPrice",            minPrice == Double.MAX_VALUE ? 0 : minPrice);

        return ResponseEntity.ok(stats);
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<String> handleBadRequest(IllegalArgumentException ex) {
        return ResponseEntity.badRequest().body("Error: " + ex.getMessage());
    }
}

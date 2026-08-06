package com.techsolutions.observer;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

/**
 * Servicio que gestiona el inventario de productos.
 *
 * <p>Es el punto de entrada para todas las operaciones sobre productos.
 * Al crear cada producto, registra automáticamente los observadores
 * {@link GerenteNotificationObserver} y {@link ComprasNotificationObserver}.</p>
 *
 * <p>El inventario se mantiene en memoria para esta entrega. En producción
 * se reemplazaría por un repositorio JPA.</p>
 */
@Service
public class ProductInventoryService {

    private static final Logger log = LoggerFactory.getLogger(ProductInventoryService.class);

    private final Map<String, Product> inventory = new HashMap<>();
    private final GerenteNotificationObserver gerenteObserver;
    private final ComprasNotificationObserver comprasObserver;

    public ProductInventoryService(GerenteNotificationObserver gerenteObserver,
                                   ComprasNotificationObserver comprasObserver) {
        this.gerenteObserver = gerenteObserver;
        this.comprasObserver = comprasObserver;
        loadSampleData();
    }

    /**
     * Registra un nuevo producto en el inventario y le asigna los observadores.
     */
    public Product registerProduct(String name, String category, double basePrice,
                                   int stock, int minimumStock) {
        String id      = UUID.randomUUID().toString().substring(0, 8).toUpperCase();
        Product product = new Product(id, name, category, basePrice, stock, minimumStock);

        product.registerObserver(gerenteObserver);
        product.registerObserver(comprasObserver);

        inventory.put(id, product);
        log.info("Producto registrado: {} (ID={}). Observadores: GERENTE, COMPRAS", name, id);
        return product;
    }

    /**
     * Descuenta unidades del stock de un producto.
     * Si el stock resultante cae bajo el mínimo, los observadores son notificados.
     */
    public Product decreaseStock(String productId, int quantity) {
        Product product = findOrThrow(productId);
        product.decreaseStock(quantity);
        log.info("Stock de '{}' reducido en {} uds. Stock actual: {}", product.getName(), quantity, product.getStock());
        return product;
    }

    /**
     * Aumenta el stock de un producto (reabastecimiento).
     */
    public Product increaseStock(String productId, int quantity) {
        Product product = findOrThrow(productId);
        product.increaseStock(quantity);
        log.info("Stock de '{}' incrementado en {} uds. Stock actual: {}", product.getName(), quantity, product.getStock());
        return product;
    }

    /**
     * Devuelve todos los productos del inventario.
     */
    public Collection<Product> getAllProducts() {
        return inventory.values();
    }

    /**
     * Busca un producto por ID.
     */
    public Product findById(String productId) {
        return findOrThrow(productId);
    }

    /**
     * Devuelve los productos cuyo stock está por debajo del mínimo.
     */
    public List<Product> getProductsBelowMinimum() {
        List<Product> result = new ArrayList<>();
        for (Product p : inventory.values()) {
            if (p.isBelowMinimum()) result.add(p);
        }
        return result;
    }

    /**
     * Historial de alertas recibidas por el observador GERENTE.
     */
    public List<String> getGerenteAlerts() {
        return gerenteObserver.getNotificationLog();
    }

    /**
     * Historial de órdenes de compra generadas por el observador COMPRAS.
     */
    public List<String> getComprasOrders() {
        return comprasObserver.getPurchaseOrderLog();
    }

    private Product findOrThrow(String productId) {
        Product product = inventory.get(productId.toUpperCase());
        if (product == null) {
            throw new IllegalArgumentException("Producto no encontrado con ID: " + productId);
        }
        return product;
    }

    /**
     * Carga datos de ejemplo al iniciar el servicio.
     */
    private void loadSampleData() {
        registerProduct("Laptop Dell Inspiron 15",   "COMPUTADORAS",  2899.00, 12, 5);
        registerProduct("Monitor LG 27'' 4K",        "MONITORES",     1450.00, 8,  3);
        registerProduct("Teclado Mecánico Logitech", "PERIFÉRICOS",    350.00, 3,  5);
        registerProduct("Disco SSD Samsung 1TB",     "ALMACENAMIENTO", 480.00, 2,  4);
        log.info("Inventario inicial cargado: {} productos.", inventory.size());
    }
}

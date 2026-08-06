package com.techsolutions.iterator;

import com.techsolutions.observer.Product;
import com.techsolutions.observer.ProductInventoryService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

/**
 * Agregado del patrón Iterator — representa el catálogo de productos.
 *
 * <p>Proporciona un método de fábrica {@link #createIterator(ProductFilter)}
 * que devuelve un {@link ProductIterator} sin exponer la lista interna de
 * productos. Los clientes nunca tienen acceso directo a la colección.</p>
 *
 * <p>Obtiene los productos del {@link ProductInventoryService} (reutiliza los
 * datos del módulo Observer), cumpliendo con el principio DRY y manteniendo
 * un único punto de verdad para el inventario.</p>
 *
 * <p><b>GRASP — Bajo Acoplamiento:</b> depende de {@link ProductInventoryService}
 * solo para leer productos; no modifica el inventario.</p>
 *
 * <p><b>GRASP — Alta Cohesión:</b> solo crea iteradores; no filtra ni pagina
 * directamente (eso es responsabilidad del iterador concreto).</p>
 */
@Service
public class ProductCatalog {

    private static final Logger log = LoggerFactory.getLogger(ProductCatalog.class);

    private final ProductInventoryService inventoryService;

    public ProductCatalog(ProductInventoryService inventoryService) {
        this.inventoryService = inventoryService;
    }

    /**
     * Crea y devuelve un iterador sobre los productos del catálogo
     * aplicando los criterios del {@link ProductFilter} dado.
     *
     * @param filter criterios de filtrado y paginación
     * @return iterador listo para su uso
     */
    public ProductIterator createIterator(ProductFilter filter) {
        List<Product> products = new ArrayList<>(inventoryService.getAllProducts());
        log.debug("[Catalog] Creando iterador sobre {} productos con filtro: cat={}, precio=[{}-{}], page={}/{}",
                products.size(),
                filter.getCategory() == null ? "*" : filter.getCategory(),
                filter.getMinPrice(), filter.getMaxPrice(),
                filter.getPage(), filter.getPageSize());
        return new ProductCatalogIterator(products, filter);
    }

    /**
     * Crea un iterador sin filtros (recorre todos los productos del catálogo).
     */
    public ProductIterator createIterator() {
        return createIterator(ProductFilter.noFilter());
    }

    /**
     * Devuelve la cantidad total de productos en el catálogo (sin filtrar).
     */
    public int totalProducts() {
        return inventoryService.getAllProducts().size();
    }
}

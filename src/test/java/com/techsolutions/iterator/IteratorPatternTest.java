package com.techsolutions.iterator;

import com.techsolutions.observer.ComprasNotificationObserver;
import com.techsolutions.observer.GerenteNotificationObserver;
import com.techsolutions.observer.Product;
import com.techsolutions.observer.ProductInventoryService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("Pruebas Unitarias — Patrón Iterator")
class IteratorPatternTest {

    private ProductCatalog catalog;

    @BeforeEach
    void setUp() {
        ProductInventoryService inventoryService = new ProductInventoryService(
                new GerenteNotificationObserver(),
                new ComprasNotificationObserver()
        );
        catalog = new ProductCatalog(inventoryService);
    }

    @Test
    @DisplayName("ProductIterator recorre todos los productos del catálogo")
    void testIterateAllProducts() {
        ProductIterator iterator = catalog.createIterator();

        assertNotNull(iterator);
        List<Product> products = new ArrayList<>();
        while (iterator.hasNext()) {
            products.add(iterator.next());
        }

        assertEquals(4, products.size());
        assertEquals(4, iterator.totalElements());
    }

    @Test
    @DisplayName("ProductIterator filtra por categoría correctamente")
    void testFilterByCategory() {
        ProductFilter filter = ProductFilter.builder()
                .category("MONITORES")
                .build();

        ProductIterator iterator = catalog.createIterator(filter);

        List<Product> list = new ArrayList<>();
        while (iterator.hasNext()) {
            list.add(iterator.next());
        }

        assertEquals(1, list.size());
        assertEquals("Monitor LG 27'' 4K", list.get(0).getName());
    }

    @Test
    @DisplayName("ProductIterator aplica paginación correctamente")
    void testPagination() {
        ProductFilter filter = ProductFilter.builder()
                .page(0)
                .pageSize(2)
                .build();

        ProductIterator iterator = catalog.createIterator(filter);

        List<Product> list = new ArrayList<>();
        while (iterator.hasNext()) {
            list.add(iterator.next());
        }

        assertEquals(2, list.size());
    }

    @Test
    @DisplayName("ProductIterator permite resetear la posición del cursor")
    void testResetCursor() {
        ProductIterator iterator = catalog.createIterator();
        assertTrue(iterator.hasNext());
        iterator.next();
        assertEquals(1, iterator.currentIndex());

        iterator.reset();
        assertEquals(0, iterator.currentIndex());
    }
}

package com.techsolutions.iterator;

import com.techsolutions.observer.Product;

/**
 * Interfaz del patrón Iterator para recorrer productos del catálogo.
 *
 * <p>Permite traversal secuencial sin exponer la estructura interna
 * de la colección ({@link ProductCatalog}).</p>
 *
 * <p><b>GRASP — Bajo Acoplamiento:</b> los clientes reciben esta interfaz,
 * no el tipo concreto {@link ProductCatalogIterator}.</p>
 */
public interface ProductIterator {

    /**
     * Indica si hay más elementos disponibles en la iteración actual.
     */
    boolean hasNext();

    /**
     * Devuelve el siguiente producto y avanza el cursor.
     *
     * @throws java.util.NoSuchElementException si no hay más elementos
     */
    Product next();

    /**
     * Reinicia el cursor al primer elemento de la colección filtrada.
     */
    void reset();

    /**
     * Devuelve la posición actual del cursor (0-indexed).
     */
    int currentIndex();

    /**
     * Devuelve el número total de elementos en la vista filtrada/paginada.
     */
    int totalElements();
}

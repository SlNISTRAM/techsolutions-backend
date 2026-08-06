package com.techsolutions.iterator;

import com.techsolutions.observer.Product;

import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;

/**
 * Iterador concreto del catálogo de productos.
 *
 * <p>Aplica los filtros del {@link ProductFilter} sobre la colección interna del
 * {@link ProductCatalog} y expone solo la vista filtrada y paginada.
 * Los clientes nunca acceden a la lista original.</p>
 *
 * <p><b>GRASP — Alta Cohesión:</b> esta clase solo se encarga de recorrer
 * y filtrar; no sabe nada de HTTP ni de lógica de negocio.</p>
 *
 * <p><b>GRASP — Bajo Acoplamiento:</b> expone la interfaz {@link ProductIterator}
 * al mundo exterior; el tipo concreto es invisible para los clientes.</p>
 */
public class ProductCatalogIterator implements ProductIterator {

    private final List<Product> filteredProducts;
    private int cursor;

    public ProductCatalogIterator(List<Product> allProducts, ProductFilter filter) {
        this.filteredProducts = applyFilter(allProducts, filter);
        this.cursor           = 0;
    }

    @Override
    public boolean hasNext() {
        return cursor < filteredProducts.size();
    }

    @Override
    public Product next() {
        if (!hasNext()) {
            throw new NoSuchElementException("No hay más productos en el iterador.");
        }
        return filteredProducts.get(cursor++);
    }

    @Override
    public void reset() {
        cursor = 0;
    }

    @Override
    public int currentIndex() {
        return cursor;
    }

    @Override
    public int totalElements() {
        return filteredProducts.size();
    }

    /**
     * Drena todos los elementos restantes como lista (no modifica el cursor del caller).
     */
    public List<Product> drainAll() {
        List<Product> result = new ArrayList<>();
        while (hasNext()) {
            result.add(next());
        }
        return result;
    }

    // ─── Aplicación de filtros y paginación ─────────────────────────────────

    private List<Product> applyFilter(List<Product> source, ProductFilter filter) {
        List<Product> filtered = new ArrayList<>();

        for (Product product : source) {
            if (!matchesCategory(product, filter))    continue;
            if (!matchesPriceRange(product, filter))  continue;
            if (!matchesAvailability(product, filter)) continue;
            filtered.add(product);
        }

        return paginate(filtered, filter);
    }

    private boolean matchesCategory(Product product, ProductFilter filter) {
        if (filter.getCategory() == null || filter.getCategory().isBlank()) return true;
        return product.getCategory().equalsIgnoreCase(filter.getCategory());
    }

    private boolean matchesPriceRange(Product product, ProductFilter filter) {
        return product.getBasePrice() >= filter.getMinPrice()
            && product.getBasePrice() <= filter.getMaxPrice();
    }

    private boolean matchesAvailability(Product product, ProductFilter filter) {
        if (!filter.isOnlyAvailable()) return true;
        return product.getStock() > 0;
    }

    private List<Product> paginate(List<Product> list, ProductFilter filter) {
        if (filter.getPageSize() == Integer.MAX_VALUE) return list;

        int fromIndex = filter.getPage() * filter.getPageSize();
        if (fromIndex >= list.size()) return new ArrayList<>();

        int toIndex = Math.min(fromIndex + filter.getPageSize(), list.size());
        return new ArrayList<>(list.subList(fromIndex, toIndex));
    }
}

package com.techsolutions.iterator;

/**
 * Objeto de criterios de filtrado y paginación para el catálogo de productos.
 * Usa el patrón Builder para construcción parcial y legible.
 *
 * <p><b>GRASP — Alta Cohesión:</b> solo agrupa criterios de búsqueda;
 * no contiene lógica de aplicación de filtros.</p>
 */
public class ProductFilter {

    private String  category      = null;
    private double  minPrice      = 0.0;
    private double  maxPrice      = Double.MAX_VALUE;
    private boolean onlyAvailable = false;
    private int     page          = 0;
    private int     pageSize      = Integer.MAX_VALUE;

    private ProductFilter() {}

    public String  getCategory()      { return category; }
    public double  getMinPrice()      { return minPrice; }
    public double  getMaxPrice()      { return maxPrice; }
    public boolean isOnlyAvailable()  { return onlyAvailable; }
    public int     getPage()          { return page; }
    public int     getPageSize()      { return pageSize; }

    public static Builder builder()  { return new Builder(); }

    public static ProductFilter noFilter() { return new Builder().build(); }

    public static class Builder {
        private final ProductFilter filter = new ProductFilter();

        public Builder category(String category) {
            filter.category = category;
            return this;
        }

        public Builder minPrice(double minPrice) {
            if (minPrice < 0) throw new IllegalArgumentException("El precio mínimo no puede ser negativo.");
            filter.minPrice = minPrice;
            return this;
        }

        public Builder maxPrice(double maxPrice) {
            filter.maxPrice = maxPrice;
            return this;
        }

        public Builder onlyAvailable(boolean onlyAvailable) {
            filter.onlyAvailable = onlyAvailable;
            return this;
        }

        public Builder page(int page) {
            if (page < 0) throw new IllegalArgumentException("La página no puede ser negativa.");
            filter.page = page;
            return this;
        }

        public Builder pageSize(int pageSize) {
            if (pageSize < 1) throw new IllegalArgumentException("El tamaño de página debe ser al menos 1.");
            filter.pageSize = pageSize;
            return this;
        }

        public ProductFilter build() { return filter; }
    }
}

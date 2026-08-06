package com.techsolutions.observer;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Entidad Product — actúa como Subject (sujeto observable) en el patrón Observer.
 *
 * <p>Mantiene su propia lista de observadores y los notifica automáticamente
 * cada vez que el stock cae por debajo del {@code minimumStock} configurado.</p>
 *
 * <p>Es también la entidad central del módulo de inventario: representa un
 * producto del catálogo de TechSolutions S.A.</p>
 */
public class Product {

    private final String id;
    private String name;
    private String category;
    private double basePrice;
    private int stock;
    private int minimumStock;

    private final List<StockObserver> observers = new ArrayList<>();

    public Product(String id, String name, String category, double basePrice,
                   int stock, int minimumStock) {
        if (minimumStock < 0) throw new IllegalArgumentException("El stock mínimo no puede ser negativo.");
        if (stock < 0)        throw new IllegalArgumentException("El stock inicial no puede ser negativo.");
        this.id           = id;
        this.name         = name;
        this.category     = category;
        this.basePrice    = basePrice;
        this.stock        = stock;
        this.minimumStock = minimumStock;
    }

    // ─── Observer management ────────────────────────────────────────────────

    public void registerObserver(StockObserver observer) {
        if (observer != null && !observers.contains(observer)) {
            observers.add(observer);
        }
    }

    public void removeObserver(StockObserver observer) {
        observers.remove(observer);
    }

    public List<StockObserver> getObservers() {
        return Collections.unmodifiableList(observers);
    }

    private void notifyObservers(StockAlertEvent event) {
        for (StockObserver observer : observers) {
            observer.update(event);
        }
    }

    // ─── Stock mutation (triggers Observer) ─────────────────────────────────

    /**
     * Reduce el stock en {@code quantity} unidades.
     * Si el stock resultante cae por debajo del mínimo, se disparan los observadores.
     *
     * @param quantity unidades a descontar (positivo)
     * @throws IllegalArgumentException si la cantidad es negativa o mayor al stock disponible
     */
    public void decreaseStock(int quantity) {
        if (quantity <= 0) throw new IllegalArgumentException("La cantidad a descontar debe ser positiva.");
        if (quantity > stock) {
            throw new IllegalArgumentException(
                String.format("Stock insuficiente. Disponible: %d, solicitado: %d", stock, quantity)
            );
        }
        int previousStock = this.stock;
        this.stock -= quantity;

        if (this.stock < minimumStock) {
            notifyObservers(new StockAlertEvent(id, name, category, previousStock, this.stock, minimumStock));
        }
    }

    /**
     * Incrementa el stock en {@code quantity} unidades (reabastecimiento).
     *
     * @param quantity unidades a añadir (positivo)
     */
    public void increaseStock(int quantity) {
        if (quantity <= 0) throw new IllegalArgumentException("La cantidad a añadir debe ser positiva.");
        this.stock += quantity;
    }

    /**
     * Establece directamente el stock y dispara observadores si queda bajo el mínimo.
     *
     * @param newStock nuevo valor de stock
     */
    public void setStock(int newStock) {
        if (newStock < 0) throw new IllegalArgumentException("El stock no puede ser negativo.");
        int previousStock = this.stock;
        this.stock = newStock;
        if (this.stock < minimumStock) {
            notifyObservers(new StockAlertEvent(id, name, category, previousStock, this.stock, minimumStock));
        }
    }

    // ─── Getters & Setters ───────────────────────────────────────────────────

    public String getId()          { return id; }
    public String getName()        { return name; }
    public String getCategory()    { return category; }
    public double getBasePrice()   { return basePrice; }
    public int    getStock()       { return stock; }
    public int    getMinimumStock(){ return minimumStock; }

    public void setName(String name)            { this.name = name; }
    public void setCategory(String category)    { this.category = category; }
    public void setBasePrice(double basePrice)  {
        if (basePrice < 0) throw new IllegalArgumentException("El precio base no puede ser negativo.");
        this.basePrice = basePrice;
    }
    public void setMinimumStock(int minimumStock) {
        if (minimumStock < 0) throw new IllegalArgumentException("El stock mínimo no puede ser negativo.");
        this.minimumStock = minimumStock;
    }

    public boolean isBelowMinimum() {
        return stock < minimumStock;
    }

    @Override
    public String toString() {
        return String.format("Product{id='%s', name='%s', category='%s', price=%.2f, stock=%d, minStock=%d}",
                id, name, category, basePrice, stock, minimumStock);
    }
}

package com.techsolutions.adapter;

/**
 * Interfaz del patrón Adapter para sistemas de pago.
 * Define el contrato que todas las implementaciones de pasarela de pago deben cumplir.
 */
public interface PaymentAdapter {

    /**
     * Procesa un pago mediante la pasarela específica.
     *
     * @param amount   monto a cobrar en soles (PEN)
     * @param currency código ISO de moneda (e.g. "PEN", "USD")
     * @return resultado textual de la operación
     */
    String processPayment(double amount, String currency);

    /**
     * Devuelve el nombre identificador de la pasarela.
     */
    String getAdapterName();

    /**
     * Verifica si la pasarela está habilitada para operar.
     */
    boolean isEnabled();

    /**
     * Habilita o deshabilita la pasarela de pago.
     *
     * @param enabled {@code true} para activar, {@code false} para desactivar
     */
    void setEnabled(boolean enabled);
}

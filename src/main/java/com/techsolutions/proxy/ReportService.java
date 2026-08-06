package com.techsolutions.proxy;

import java.util.List;

/**
 * Interfaz del patrón Proxy para el servicio de reportes financieros.
 * Define las operaciones de reporte que pueden estar sujetas a control de acceso.
 */
public interface ReportService {

    /**
     * Genera el reporte de ventas del período indicado.
     *
     * @param period período en formato "YYYY-MM" (e.g. "2024-07")
     * @return contenido del reporte de ventas
     */
    String generateSalesReport(String period);

    /**
     * Genera el balance general (activos, pasivos, patrimonio).
     *
     * @param fiscalYear año fiscal (e.g. 2024)
     * @return contenido del balance general
     */
    String generateBalanceSheet(int fiscalYear);

    /**
     * Genera el estado de resultados para el año indicado.
     *
     * @param fiscalYear año fiscal
     * @return contenido del estado de resultados
     */
    String generateIncomeStatement(int fiscalYear);

    /**
     * Lista los reportes disponibles en el sistema.
     *
     * @return lista de nombres de reportes disponibles
     */
    List<String> listAvailableReports();
}

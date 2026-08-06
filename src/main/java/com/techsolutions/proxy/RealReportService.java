package com.techsolutions.proxy;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

/**
 * Implementación real del servicio de reportes financieros.
 * Contiene la lógica de negocio auténtica para generar cada tipo de reporte.
 * Este bean NO se expone directamente al controlador; se accede siempre
 * a través de {@link ReportServiceProxy}.
 */
@Service("realReportService")
public class RealReportService implements ReportService {

    private static final Logger log = LoggerFactory.getLogger(RealReportService.class);
    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss");

    @Override
    public String generateSalesReport(String period) {
        log.info("[RealReportService] Generando reporte de ventas para el período: {}", period);

        String timestamp = LocalDateTime.now().format(FORMATTER);

        return """
                ╔══════════════════════════════════════════════╗
                ║       REPORTE DE VENTAS — TechSolutions      ║
                ╠══════════════════════════════════════════════╣
                ║  Período       : %s
                ║  Generado      : %s
                ║  Ventas totales: S/ 145,320.00
                ║  Nro de órdenes: 1,240
                ║  Ticket promedio: S/ 117.19
                ║  Canal online  : 68%%
                ║  Canal presencial: 32%%
                ╚══════════════════════════════════════════════╝
                """.formatted(period, timestamp);
    }

    @Override
    public String generateBalanceSheet(int fiscalYear) {
        log.info("[RealReportService] Generando balance general para el año fiscal: {}", fiscalYear);

        String timestamp = LocalDateTime.now().format(FORMATTER);

        return """
                ╔══════════════════════════════════════════════╗
                ║      BALANCE GENERAL — TechSolutions S.A.    ║
                ╠══════════════════════════════════════════════╣
                ║  Año Fiscal    : %d
                ║  Generado      : %s
                ╠══════════════════════════════════════════════╣
                ║  ACTIVOS                                      ║
                ║    Activo Corriente  : S/ 320,000.00          ║
                ║    Activo No Corriente: S/ 580,000.00         ║
                ║  TOTAL ACTIVOS       : S/ 900,000.00          ║
                ╠══════════════════════════════════════════════╣
                ║  PASIVOS                                      ║
                ║    Pasivo Corriente  : S/ 110,000.00          ║
                ║    Pasivo No Corriente: S/ 240,000.00         ║
                ║  TOTAL PASIVOS       : S/ 350,000.00          ║
                ╠══════════════════════════════════════════════╣
                ║  PATRIMONIO          : S/ 550,000.00          ║
                ╚══════════════════════════════════════════════╝
                """.formatted(fiscalYear, timestamp);
    }

    @Override
    public String generateIncomeStatement(int fiscalYear) {
        log.info("[RealReportService] Generando estado de resultados para el año fiscal: {}", fiscalYear);

        String timestamp = LocalDateTime.now().format(FORMATTER);

        return """
                ╔══════════════════════════════════════════════╗
                ║   ESTADO DE RESULTADOS — TechSolutions S.A.  ║
                ╠══════════════════════════════════════════════╣
                ║  Año Fiscal    : %d
                ║  Generado      : %s
                ╠══════════════════════════════════════════════╣
                ║  Ingresos Operativos : S/ 1,450,000.00       ║
                ║  Costo de Ventas     : S/   870,000.00       ║
                ║  Utilidad Bruta      : S/   580,000.00       ║
                ╠══════════════════════════════════════════════╣
                ║  Gastos Administrativos: S/ 120,000.00       ║
                ║  Gastos de Ventas    : S/    95,000.00       ║
                ║  Utilidad Operativa  : S/   365,000.00       ║
                ╠══════════════════════════════════════════════╣
                ║  Impuesto a la Renta (29.5%%): S/ 107,675.00 ║
                ║  UTILIDAD NETA       : S/   257,325.00       ║
                ╚══════════════════════════════════════════════╝
                """.formatted(fiscalYear, timestamp);
    }

    @Override
    public List<String> listAvailableReports() {
        log.info("[RealReportService] Listando reportes disponibles.");
        return List.of(
                "Reporte de Ventas (por período mensual)",
                "Balance General (por año fiscal)",
                "Estado de Resultados (por año fiscal)"
        );
    }
}

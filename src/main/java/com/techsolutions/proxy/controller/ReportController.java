package com.techsolutions.proxy.controller;

import com.techsolutions.proxy.AccessDeniedException;
import com.techsolutions.proxy.ReportService;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Controlador REST para acceder a los reportes financieros de TechSolutions S.A.
 * Todas las solicitudes pasan por {@link com.techsolutions.proxy.ReportServiceProxy},
 * que valida los roles antes de delegar al servicio real.
 *
 * <ul>
 *   <li>GET  /api/reports                          → listado de reportes disponibles</li>
 *   <li>GET  /api/reports/sales?period=YYYY-MM     → reporte de ventas</li>
 *   <li>GET  /api/reports/balance?year=YYYY        → balance general</li>
 *   <li>GET  /api/reports/income?year=YYYY         → estado de resultados</li>
 * </ul>
 */
@RestController
@RequestMapping("/api/reports")
public class ReportController {

    private final ReportService reportService;

    public ReportController(@Qualifier("reportServiceProxy") ReportService reportService) {
        this.reportService = reportService;
    }

    @GetMapping
    public ResponseEntity<List<String>> listReports() {
        List<String> reports = reportService.listAvailableReports();
        return ResponseEntity.ok(reports);
    }

    @GetMapping("/sales")
    public ResponseEntity<String> getSalesReport(
            @RequestParam(defaultValue = "2024-01") String period) {
        String report = reportService.generateSalesReport(period);
        return ResponseEntity.ok(report);
    }

    @GetMapping("/balance")
    public ResponseEntity<String> getBalanceSheet(
            @RequestParam(defaultValue = "2024") int year) {
        String report = reportService.generateBalanceSheet(year);
        return ResponseEntity.ok(report);
    }

    @GetMapping("/income")
    public ResponseEntity<String> getIncomeStatement(
            @RequestParam(defaultValue = "2024") int year) {
        String report = reportService.generateIncomeStatement(year);
        return ResponseEntity.ok(report);
    }

    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<String> handleAccessDenied(AccessDeniedException ex) {
        return ResponseEntity.status(HttpStatus.FORBIDDEN).body(ex.getMessage());
    }
}

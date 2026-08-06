package com.techsolutions.proxy;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("Pruebas Unitarias — Patrón Proxy")
class ReportServiceProxyTest {

    private RealReportService realReportService;
    private ReportServiceProxy proxy;

    @BeforeEach
    void setUp() {
        realReportService = new RealReportService();
        proxy = new ReportServiceProxy(realReportService);
    }

    @AfterEach
    void tearDown() {
        SecurityContextHolder.clearContext();
    }

    @Test
    @DisplayName("Proxy concede acceso a usuario con rol ROLE_GERENTE")
    void testAccessGrantedForGerente() {
        var auth = new UsernamePasswordAuthenticationToken(
                "gerente", "pass", List.of(new SimpleGrantedAuthority("ROLE_GERENTE")));
        SecurityContextHolder.getContext().setAuthentication(auth);

        String result = proxy.generateBalanceSheet(2024);

        assertNotNull(result);
        assertTrue(result.contains("BALANCE GENERAL"));
        assertTrue(result.contains("2024"));
    }

    @Test
    @DisplayName("Proxy concede acceso a usuario con rol ROLE_CONTADOR")
    void testAccessGrantedForContador() {
        var auth = new UsernamePasswordAuthenticationToken(
                "contador", "pass", List.of(new SimpleGrantedAuthority("ROLE_CONTADOR")));
        SecurityContextHolder.getContext().setAuthentication(auth);

        String result = proxy.generateSalesReport("2024-07");

        assertNotNull(result);
        assertTrue(result.contains("REPORTE DE VENTAS"));
        assertTrue(result.contains("2024-07"));
    }

    @Test
    @DisplayName("Proxy deniega acceso a usuario con rol no autorizado (ROLE_VENDEDOR)")
    void testAccessDeniedForUnauthorizedRole() {
        var auth = new UsernamePasswordAuthenticationToken(
                "vendedor", "pass", List.of(new SimpleGrantedAuthority("ROLE_VENDEDOR")));
        SecurityContextHolder.getContext().setAuthentication(auth);

        AccessDeniedException ex = assertThrows(
                AccessDeniedException.class,
                () -> proxy.generateIncomeStatement(2024)
        );

        assertTrue(ex.getMessage().contains("Acceso denegado"));
    }

    @Test
    @DisplayName("Proxy deniega acceso cuando no hay usuario autenticado")
    void testAccessDeniedWhenUnauthenticated() {
        SecurityContextHolder.clearContext();

        assertThrows(
                AccessDeniedException.class,
                () -> proxy.listAvailableReports()
        );
    }
}

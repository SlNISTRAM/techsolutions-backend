package com.techsolutions.proxy;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.Collection;
import java.util.List;
import java.util.Set;

/**
 * Proxy de protección para {@link ReportService}.
 * Intercepta todas las llamadas al servicio real y verifica que el usuario
 * autenticado posea el rol 'GERENTE' o 'CONTADOR'. Si el rol no es válido,
 * lanza una {@link AccessDeniedException} antes de delegar al servicio real.
 *
 * <p>Este bean es el único que se inyecta en los controladores; la clase
 * {@link RealReportService} permanece inaccesible directamente.</p>
 */
@Service("reportServiceProxy")
public class ReportServiceProxy implements ReportService {

    private static final Logger log = LoggerFactory.getLogger(ReportServiceProxy.class);

    private static final Set<String> ALLOWED_ROLES = Set.of("ROLE_GERENTE", "ROLE_CONTADOR");

    private final ReportService realReportService;

    public ReportServiceProxy(@Qualifier("realReportService") ReportService realReportService) {
        this.realReportService = realReportService;
    }

    @Override
    public String generateSalesReport(String period) {
        checkAccess("generateSalesReport");
        log.info("[Proxy] Acceso concedido a generateSalesReport para usuario: {}", currentUsername());
        return realReportService.generateSalesReport(period);
    }

    @Override
    public String generateBalanceSheet(int fiscalYear) {
        checkAccess("generateBalanceSheet");
        log.info("[Proxy] Acceso concedido a generateBalanceSheet para usuario: {}", currentUsername());
        return realReportService.generateBalanceSheet(fiscalYear);
    }

    @Override
    public String generateIncomeStatement(int fiscalYear) {
        checkAccess("generateIncomeStatement");
        log.info("[Proxy] Acceso concedido a generateIncomeStatement para usuario: {}", currentUsername());
        return realReportService.generateIncomeStatement(fiscalYear);
    }

    @Override
    public List<String> listAvailableReports() {
        checkAccess("listAvailableReports");
        log.info("[Proxy] Acceso concedido a listAvailableReports para usuario: {}", currentUsername());
        return realReportService.listAvailableReports();
    }

    /**
     * Verifica que el usuario autenticado tenga un rol permitido.
     *
     * @param operation nombre de la operación que se intenta ejecutar
     * @throws AccessDeniedException si el usuario no tiene el rol requerido
     */
    private void checkAccess(String operation) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication == null || !authentication.isAuthenticated()) {
            log.warn("[Proxy] Intento de acceso no autenticado a '{}'. Acceso denegado.", operation);
            throw new AccessDeniedException(
                    "Acceso denegado: debe autenticarse para acceder a reportes financieros."
            );
        }

        Collection<? extends GrantedAuthority> authorities = authentication.getAuthorities();
        boolean hasAccess = authorities.stream()
                .map(GrantedAuthority::getAuthority)
                .anyMatch(ALLOWED_ROLES::contains);

        if (!hasAccess) {
            String username = authentication.getName();
            String userRoles = authorities.stream()
                    .map(GrantedAuthority::getAuthority)
                    .reduce((a, b) -> a + ", " + b)
                    .orElse("ninguno");

            log.warn("[Proxy] Acceso denegado a '{}' para usuario='{}' con roles=[{}].",
                    operation, username, userRoles);

            throw new AccessDeniedException(
                    String.format("Acceso denegado: el usuario '%s' con roles [%s] no tiene permiso para "
                                    + "acceder a reportes financieros. Roles requeridos: GERENTE o CONTADOR.",
                            username, userRoles)
            );
        }
    }

    private String currentUsername() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        return auth != null ? auth.getName() : "ANÓNIMO";
    }
}

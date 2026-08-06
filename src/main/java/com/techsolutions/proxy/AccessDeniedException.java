package com.techsolutions.proxy;

/**
 * Excepción lanzada por {@link ReportServiceProxy} cuando un usuario no tiene
 * los roles necesarios para acceder a los reportes financieros.
 */
public class AccessDeniedException extends RuntimeException {

    public AccessDeniedException(String message) {
        super(message);
    }

    public AccessDeniedException(String message, Throwable cause) {
        super(message, cause);
    }
}

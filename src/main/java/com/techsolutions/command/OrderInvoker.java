package com.techsolutions.command;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Deque;
import java.util.List;

/**
 * Invoker del patrón Command para pedidos.
 *
 * <p>Ejecuta comandos y mantiene un historial LIFO para soporte de undo.
 * No conoce qué hace cada comando; solo sabe invocarlo y revertirlo.</p>
 *
 * <p><b>GRASP — Bajo Acoplamiento:</b> depende únicamente de la interfaz
 * {@link OrderCommand}, nunca de las implementaciones concretas.</p>
 *
 * <p><b>GRASP — Alta Cohesión:</b> su única responsabilidad es gestionar
 * la cola de ejecución y el historial de comandos.</p>
 */
@Component
public class OrderInvoker {

    private static final Logger log = LoggerFactory.getLogger(OrderInvoker.class);

    private final Deque<OrderCommand> commandHistory = new ArrayDeque<>();

    /**
     * Ejecuta un comando y lo añade al historial.
     *
     * @param command comando a ejecutar
     */
    public void execute(OrderCommand command) {
        log.info("[Invoker] Ejecutando: {}", command.getDescription());
        command.execute();
        commandHistory.push(command);
        log.debug("[Invoker] Historial: {} comando(s) registrado(s).", commandHistory.size());
    }

    /**
     * Deshace el último comando ejecutado (LIFO).
     *
     * @throws IllegalStateException si no hay comandos en el historial
     */
    public void undoLast() {
        if (commandHistory.isEmpty()) {
            throw new IllegalStateException("No hay comandos en el historial para deshacer.");
        }
        OrderCommand command = commandHistory.pop();
        log.info("[Invoker] Deshaciendo: {}", command.getDescription());
        command.undo();
    }

    /**
     * Devuelve el historial de descripciones de comandos ejecutados (del más reciente al más antiguo).
     */
    public List<String> getCommandHistory() {
        List<String> descriptions = new ArrayList<>();
        for (OrderCommand cmd : commandHistory) {
            descriptions.add(cmd.getDescription());
        }
        return descriptions;
    }

    /**
     * Indica si hay comandos disponibles para deshacer.
     */
    public boolean canUndo() {
        return !commandHistory.isEmpty();
    }

    /**
     * Limpia el historial completo de comandos.
     */
    public void clearHistory() {
        commandHistory.clear();
        log.info("[Invoker] Historial de comandos limpiado.");
    }
}

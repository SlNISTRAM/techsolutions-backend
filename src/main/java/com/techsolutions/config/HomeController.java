package com.techsolutions.config;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Controlador de bienvenida público.
 * Accesible sin autenticación para verificar que el servidor está activo
 * y conocer los endpoints disponibles en todos los patrones implementados.
 */
@RestController
public class HomeController {

    @GetMapping(value = "/", produces = "text/html;charset=UTF-8")
    public String home() {
        return """
                <!DOCTYPE html>
                <html lang="es">
                <head>
                  <meta charset="UTF-8"/>
                  <title>TechSolutions S.A. — API Patrones de Diseño</title>
                  <style>
                    body { font-family: sans-serif; background:#0f172a; color:#e2e8f0; padding:2rem; }
                    h1   { color:#38bdf8; }
                    h2   { color:#7dd3fc; border-bottom:1px solid #334155; padding-bottom:.4rem; margin-top:2rem; }
                    table{ border-collapse:collapse; width:100%; margin-bottom:1.5rem; }
                    th   { background:#1e293b; color:#94a3b8; text-align:left; padding:.5rem .8rem; }
                    td   { padding:.4rem .8rem; border-bottom:1px solid #1e293b; }
                    code { background:#1e293b; padding:.1rem .4rem; border-radius:4px; color:#f472b6; }
                    .badge { padding:.1rem .5rem; border-radius:4px; font-size:.8rem; font-weight:bold; }
                    .badge-admin   { background:#7c3aed; color:#fff; }
                    .badge-gerente { background:#0369a1; color:#fff; }
                    .badge-public  { background:#15803d; color:#fff; }
                    .badge-user    { background:#475569; color:#fff; }
                  </style>
                </head>
                <body>
                  <h1>🚀 TechSolutions S.A. — Backend API Patrones de Diseño</h1>
                  <p>Servidor activo · Spring Boot 3.2.5 · Java 17</p>

                  <h2>🔌 Patrón Adapter — Pasarelas de Pago</h2>
                  <p><span class="badge badge-admin">ADMIN</span> Credenciales: <code>admin / admin123</code></p>
                  <table>
                    <tr><th>Método</th><th>URL</th><th>Descripción</th></tr>
                    <tr><td>GET</td><td><code>/api/payments/adapters</code></td><td>Estado de todos los adaptadores</td></tr>
                    <tr><td>PUT</td><td><code>/api/payments/adapters/YAPE/enable</code></td><td>Habilitar adaptador Yape</td></tr>
                    <tr><td>PUT</td><td><code>/api/payments/adapters/YAPE/disable</code></td><td>Deshabilitar adaptador Yape</td></tr>
                    <tr><td>POST</td><td><code>/api/payments/process</code></td><td>Procesar pago (PayPal, Yape, Plin)</td></tr>
                  </table>

                  <h2>🛡️ Patrón Proxy — Reportes Financieros</h2>
                  <p><span class="badge badge-gerente">GERENTE / CONTADOR</span> <code>gerente/gerente123</code> | <code>contador/contador123</code></p>
                  <table>
                    <tr><th>Método</th><th>URL</th><th>Descripción</th></tr>
                    <tr><td>GET</td><td><code>/api/reports</code></td><td>Lista reportes disponibles</td></tr>
                    <tr><td>GET</td><td><code>/api/reports/sales?period=2024-07</code></td><td>Reporte de ventas</td></tr>
                    <tr><td>GET</td><td><code>/api/reports/balance?year=2024</code></td><td>Balance General</td></tr>
                    <tr><td>GET</td><td><code>/api/reports/income?year=2024</code></td><td>Estado de Resultados</td></tr>
                  </table>

                  <h2>🔔 Patrón Observer — Alertas de Stock</h2>
                  <p><span class="badge badge-gerente">ADMIN / GERENTE</span> <code>gerente/gerente123</code></p>
                  <table>
                    <tr><th>Método</th><th>URL</th><th>Descripción</th></tr>
                    <tr><td>GET</td><td><code>/api/inventory</code></td><td>Listar inventario de productos</td></tr>
                    <tr><td>PUT</td><td><code>/api/inventory/{id}/decrease?qty=5</code></td><td>Descontar stock (Dispara Observers)</td></tr>
                    <tr><td>GET</td><td><code>/api/inventory/notifications/gerente</code></td><td>Alertas recibidas por Gerente</td></tr>
                    <tr><td>GET</td><td><code>/api/inventory/notifications/compras</code></td><td>Órdenes de compra automáticas</td></tr>
                  </table>

                  <h2>🎯 Patrón Strategy — Cálculo de Precios</h2>
                  <p><span class="badge badge-user">AUTENTICADO</span> Cualquier usuario</p>
                  <table>
                    <tr><th>Método</th><th>URL</th><th>Descripción</th></tr>
                    <tr><td>GET</td><td><code>/api/pricing/strategies</code></td><td>Listar estrategias (Standard, Discount, Dynamic)</td></tr>
                    <tr><td>POST</td><td><code>/api/pricing/calculate</code></td><td>Calcular precio aplicando estrategia activa</td></tr>
                  </table>

                  <h2>⚡ Patrón Command & Memento — Gestión de Pedidos (GRASP)</h2>
                  <p><span class="badge badge-user">AUTENTICADO</span> Cualquier rol de usuario</p>
                  <table>
                    <tr><th>Método</th><th>URL</th><th>Descripción</th></tr>
                    <tr><td>POST</td><td><code>/api/orders</code></td><td>Crear pedido (CreateOrderCommand)</td></tr>
                    <tr><td>PUT</td><td><code>/api/orders/{id}/process</code></td><td>Procesar pedido (ProcessOrderCommand)</td></tr>
                    <tr><td>PUT</td><td><code>/api/orders/{id}/discount?pct=10</code></td><td>Aplicar descuento (ApplyDiscountCommand)</td></tr>
                    <tr><td>PUT</td><td><code>/api/orders/{id}/cancel</code></td><td>Cancelar pedido (CancelOrderCommand)</td></tr>
                    <tr><td>POST</td><td><code>/api/orders/undo</code></td><td>Deshacer último comando (Command Undo)</td></tr>
                    <tr><td>PUT</td><td><code>/api/orders/{id}/restore</code></td><td>Restaurar pedido a snapshot previo (Memento)</td></tr>
                    <tr><td>GET</td><td><code>/api/orders/{id}/snapshots</code></td><td>Historial de Mementos del pedido</td></tr>
                    <tr><td>GET</td><td><code>/api/orders/history</code></td><td>Historial de comandos ejecutados</td></tr>
                  </table>

                  <h2>🔍 Patrón Iterator — Catálogo de Productos</h2>
                  <p><span class="badge badge-public">PÚBLICO</span> Acceso libre sin credenciales</p>
                  <table>
                    <tr><th>Método</th><th>URL</th><th>Descripción</th></tr>
                    <tr><td>GET</td><td><code>/api/catalog</code></td><td>Recorrer catálogo con Iterator</td></tr>
                    <tr><td>GET</td><td><code>/api/catalog/search?category=MONITORES&minPrice=500&pageSize=5</code></td><td>Filtrar y paginar con Iterator</td></tr>
                    <tr><td>GET</td><td><code>/api/catalog/stats</code></td><td>Estadísticas calculadas via Iterator traversal</td></tr>
                  </table>
                </body>
                </html>
                """;
    }
}

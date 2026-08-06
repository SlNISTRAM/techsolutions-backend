# TechSolutions S.A. — Backend Spring Boot

Sistema Backend desarrollado en **Java 17** y **Spring Boot 3.2.5** para la empresa **TechSolutions S.A.** como parte del proyecto de **Patrones de Diseño de Software y Principios GRASP**.

---

## 👥 Integrantes del Equipo

| # | Nombre | Apellido | GitHub |
|---|---|---|---|
| 1 | Gino | Llanes | [@GinoLlanes](https://github.com/GinoLlanes) |
| 2 | Erick | Sumari | [@ErickSumari](https://github.com/ErickSumari) |
| 3 | Esteffen | Medina | [@EsteffenMedina](https://github.com/EsteffenMedina) |
| 4 | Yecson | Domador | [@YecsonDomador](https://github.com/YecsonDomador) |
| 5 | Lucio | Calderón | [@LucioCalderon](https://github.com/LucioCalderon) |

> **Curso:** Patrones de Diseño de Software — IDAT
> **Docente:** [Nombre del docente]
> **Repositorio colaborativo:** Trabajo en equipo con historial de commits distribuidos por cada integrante.

---

## 📋 Índice

1. [Requisitos del Sistema](#-requisitos-del-sistema)
2. [Instrucciones de Clonación, Compilación y Ejecución](#-instrucciones-de-clonación-compilación-y-ejecución)
   - [1. Clonar el Repositorio](#1-clonar-el-repositorio)
   - [2. Compilar el Proyecto](#2-compilar-el-proyecto)
   - [3. Ejecutar las Pruebas Unitarias](#3-ejecutar-las-pruebas-unitarias)
   - [4. Iniciar la Aplicación Spring Boot](#4-iniciar-la-aplicación-spring-boot)
3. [Credenciales de Prueba](#-credenciales-de-prueba-spring-security---http-basic)
4. [Endpoints Principales de la API](#-endpoints-principales-de-la-api)

---

## 📐 Patrones de Diseño e Implementación GRASP

Este proyecto implementa **6 patrones de diseño** (2 Estructurales y 4 de Comportamiento) estructurados bajo principios de diseño de software de alta calidad (**GRASP**):

### 1. Patrones Estructurales
* **Adapter (`com.techsolutions.adapter`)**: Interfaz unificada `PaymentAdapter` e implementaciones concretas para las pasarelas **PayPal**, **Yape** (BCP) y **Plin**. Incluye el servicio `PaymentAdapterService` para la activación/desactivación dinámica de adaptadores.
* **Proxy (`com.techsolutions.proxy`)**: Protección de acceso mediante `ReportServiceProxy`. Intercepta las peticiones a los reportes financieros de `RealReportService`, restringiendo el acceso exclusivamente a los roles `'GERENTE'` y `'CONTADOR'`.

### 2. Patrones de Comportamiento
* **Observer (`com.techsolutions.observer`)**: Entidad `Product` como sujeto observable (Subject) que notifica automáticamente a los observadores `GerenteNotificationObserver` (alertas ejecutivas) y `ComprasNotificationObserver` (órdenes de compra sugeridas) cuando el stock cae por debajo del mínimo configurado.
* **Strategy (`com.techsolutions.strategy`)**: Cálculo dinámico de precios con la interfaz `PricingStrategy` y sus tres estrategias concretas (`StandardPricingStrategy`, `PercentageDiscountStrategy`, `DynamicPricingStrategy`). Intercambiables en tiempo de ejecución a través del contexto `PriceCalculatorContext`.
* **Command y Memento (`com.techsolutions.command`)**:
  * **Command**: Encapsula las acciones de los pedidos (`CreateOrderCommand`, `ProcessOrderCommand`, `ApplyDiscountCommand`, `CancelOrderCommand`) ejecutadas a través del `OrderInvoker` con soporte de deshacer (Undo).
  * **Memento**: Captura snapshots inmutables del estado de los pedidos (`OrderMemento` guardados en `OrderCaretaker`) para restauración ante cancelaciones o reversiones.
* **Iterator (`com.techsolutions.iterator`)**: Recorrido, filtrado avanzado (categoría, rango de precio, stock disponible) y paginación del catálogo de productos con `ProductCatalogIterator` y `ProductCatalog`, protegiendo la estructura interna de la colección.

### 3. Principios GRASP Aplicados
* **Controlador (Controller)**: `OrderCommandService` coordina la lógica del negocio de pedidos como controlador de dominio, manteniendo a `OrderController` como una fachada HTTP pura.
* **Alta Cohesión (High Cohesion)**: Cada paquete y clase responde a una única responsabilidad (e.g. `OrderCaretaker` maneja snapshots, `OrderInvoker` gestiona historial de comandos).
* **Bajo Acoplamiento (Low Coupling)**: Desacoplamiento total utilizando interfaces (`PaymentAdapter`, `ReportService`, `StockObserver`, `PricingStrategy`, `OrderCommand`, `ProductIterator`).

---

## 🛠️ Requisitos del Sistema

* **JDK**: Java 17 o superior.
* **Maven**: 3.8.x o superior.
* **IDE Recomendado**: IntelliJ IDEA, VS Code o Eclipse.

---

## 🚀 Instrucciones de Clonación, Compilación y Ejecución

### 1. Clonar el Repositorio
```bash
git clone https://github.com/tu-usuario/techsolutions-backend.git
cd "SISTEMA Patrones de Diseño de Software"
```

### 2. Compilar el Proyecto
Para limpiar y compilar todas las clases del sistema:
```bash
mvn clean compile
```

### 3. Ejecutar las Pruebas Unitarias
Para ejecutar la suite completa de pruebas unitarias creadas con **JUnit 5** y **Mockito**:
```bash
mvn test
```

### 4. Iniciar la Aplicación Spring Boot
Para levantar el servidor backend local:
```bash
mvn spring-boot:run
```

El servidor iniciará en el puerto HTTP **8080**:
👉 **Página de Inicio / Documentación Interactiva:** [http://localhost:8080](http://localhost:8080)

---

## 🔑 Credenciales de Prueba (Spring Security - HTTP Basic)

El sistema cuenta con 4 usuarios preconfigurados en memoria para validar las reglas de seguridad y patrones de protección:

| Usuario | Contraseña | Rol Asignado | Permisos en la API |
|---|---|---|---|
| `admin` | `admin123` | `ROLE_ADMIN` | Gestión de adaptadores de pago (`/api/payments/**`) e inventario. |
| `gerente` | `gerente123` | `ROLE_GERENTE` | Ver reportes financieros, alertas de stock e inventario. |
| `contador` | `contador123` | `ROLE_CONTADOR` | Acceso a reportes financieros (`/api/reports/**`). |
| `vendedor` | `vendedor123` | `ROLE_VENDEDOR` | Operaciones de catálogo y precios (Sin acceso a reportes). |

---

## 📑 Endpoints Principales de la API

### 🔌 Adaptadores de Pago (Adapter)
* `GET /api/payments/adapters`: Ver estado de adaptadores (PayPal, Yape, Plin).
* `PUT /api/payments/adapters/{name}/enable`: Habilitar un adaptador.
* `PUT /api/payments/adapters/{name}/disable`: Deshabilitar un adaptador.
* `POST /api/payments/process`: Procesar pago mediante un adaptador específico.

### 🛡️ Reportes Financieros (Proxy)
* `GET /api/reports`: Lista de reportes disponibles (Solo Gerente/Contador).
* `GET /api/reports/sales?period=2024-07`: Reporte de ventas mensual.
* `GET /api/reports/balance?year=2024`: Balance general.
* `GET /api/reports/income?year=2024`: Estado de resultados.

### 🔔 Alertas de Inventario (Observer)
* `GET /api/inventory`: Lista de productos del inventario.
* `PUT /api/inventory/{id}/decrease?qty=N`: Descontar stock (Notifica observadores Gerente y Compras).
* `GET /api/inventory/notifications/gerente`: Alertas ejecutivas enviadas a Gerencia.
* `GET /api/inventory/notifications/compras`: Órdenes de reabastecimiento automáticas para Compras.

### 🎯 Calculadora de Precios (Strategy)
* `GET /api/pricing/strategies`: Listado de estrategias de precios disponibles.
* `POST /api/pricing/calculate`: Calcular precio enviando la estrategia a utilizar (STANDARD, PERCENTAGE_DISCOUNT, DYNAMIC).

### ⚡ Procesamiento de Pedidos (Command & Memento - GRASP)
* `POST /api/orders`: Crear pedido (`CreateOrderCommand`).
* `PUT /api/orders/{id}/process`: Procesar pedido (`ProcessOrderCommand`).
* `PUT /api/orders/{id}/discount?pct=15`: Aplicar descuento (`ApplyDiscountCommand`).
* `PUT /api/orders/{id}/cancel`: Cancelar pedido (`CancelOrderCommand`).
* `POST /api/orders/undo`: Deshacer última acción (`Invoker Undo`).
* `PUT /api/orders/{id}/restore`: Restaurar pedido a snapshot anterior (`Memento`).

### 🔍 Catálogo Filtrable (Iterator)
* `GET /api/catalog`: Recorrer catálogo sin filtros.
* `GET /api/catalog/search?category=MONITORES&minPrice=1000&pageSize=5`: Búsqueda filtrada y paginada usando `ProductIterator`.
* `GET /api/catalog/stats`: Estadísticas del catálogo calculadas via iterador.

---

## 🤝 Contribución en Equipo

Este repositorio fue construido bajo un flujo de trabajo colaborativo donde cada integrante escribió y comprobó (tests) su patrón correspondiente. El historial de commits refleja la distribución del trabajo:

| Patrón | Integrante |
|---|---|
| Estructura base (`pom.xml`, `.gitignore`, `application.yml`) | Gino Llanes |
| Configuración (`Application`, `SecurityConfig`, `HomeController`) | Erick Sumari |
| **Adapter** — Pasarelas de Pago | Esteffen Medina |
| **Strategy** — Cálculo de Precios | Yecson Domador |
| **Observer** — Alertas de Stock | Lucio Calderón |
| **Proxy** — Reportes Financieros | Gino Llanes |
| **Command & Memento** — Gestión de Pedidos | Erick Sumari |
| **Iterator** — Catálogo de Productos | Esteffen Medina |
| Pruebas Unitarias (JUnit 5 + Mockito) | Yecson Domador |
| Documentación (`README.md`) | Lucio Calderón |

### Flujo de trabajo sugerido
1. Crear una rama por funcionalidad: `git checkout -b feature/nombre-patron`
2. Implementar el patrón y sus pruebas.
3. Ejecutar las pruebas: `mvn test`
4. Hacer commit descriptivo: `git commit -m "feat(adapter): implementar PayPalAdapter"`
5. Subir la rama: `git push origin feature/nombre-patron`
6. Abrir un Pull Request para revisión por pares.

---

## 📦 Subir a GitHub (Nuevo Repositorio)

Para publicar el proyecto en un repositorio remoto de GitHub, ejecutar los siguientes comandos desde la carpeta del proyecto:

```bash
# 1. Crear el repositorio en GitHub (web) y copiar la URL HTTPS o SSH

# 2. Vincular el remoto
git remote add origin https://github.com/tu-usuario/techsolutions-backend.git

# 3. Renombrar la rama principal a main
git branch -M main

# 4. Subir todo el historial
git push -u origin main
```

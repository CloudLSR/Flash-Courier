# Flash Courier

Sistema de gestión de envíos y logística **"Flash Courier"**. Java, Swing y MySQL, con arquitectura en capas (MVC + DAO).

Aplicación de escritorio desarrollada para el curso de Análisis y Diseño de Sistemas de Información (UTP). Simula el flujo completo de una agencia de mensajería: desde que se registra un paquete hasta que se confirma su entrega, pasando por el seguimiento en tiempo real y la gestión del personal que opera el sistema.

## Tecnologías

- **Java 25** (Maven)
- **Swing** — interfaz de escritorio
- **MySQL 8** — persistencia, con [MySQL Connector/J 8.3.0](https://dev.mysql.com/downloads/connector/j/)
- **JDBC** con procedimientos almacenados y transacciones manuales (`Connection.setAutoCommit(false)` + `commit()`/`rollback()`) para operaciones que tocan varias tablas a la vez
- **Apache PDFBox 2.0.30** — generación de comprobantes y reportes en PDF

## Arquitectura y patrones de diseño

El proyecto sigue una arquitectura en capas (Vista → Facade → Controlador → DAO → Base de Datos):

| Capa | Paquete | Responsabilidad |
|---|---|---|
| Vista | `formularios` | Pantallas Swing, un formulario por caso de uso |
| Facade | `controlador.CourierFacade` | Punto único de entrada que consumen las vistas |
| Controlador | `controlador` | Lógica de negocio (cálculo de costos, generación de tracking) |
| DAO | `dao` | Acceso a datos, cada uno invoca sus procedimientos almacenados |
| Modelo | `modelo` | Entidades del dominio (POJOs) |
| Base de datos | `database.ConexionDB` | Conexión JDBC (patrón Singleton) |
| Reportes | `reportes` | Generación de PDFs (comprobante de envío y lista de envíos) con PDFBox |
| Interfaz | `ui` | Paleta de colores, fondo y botón estilo retrowave, usados en el Login |

Patrones de diseño aplicados:
- **Singleton** — `ConexionDB`, una única instancia de conexión.
- **Facade** — `CourierFacade`, oculta la complejidad de las demás capas a las vistas.
- **State** — `modelo.state`, cada estado de un envío (`Registrado`, `En Almacen`, `En Ruta`, `En Reparto`, `Entregado`, `Cancelado`) sabe cuál es su transición válida siguiente, evitando saltos de estado inválidos.

## Funcionalidades

- **Registrar Envío** — captura remitente, destinatario y paquete; calcula el costo según el peso y genera un código de tracking único (con opción de copiarlo al portapapeles).
- **Consultar Tracking** — búsqueda pública por código, con el historial completo de movimientos y descarga del comprobante en PDF.
- **Actualizar Estado** — avanza un envío al siguiente estado válido; si el siguiente es "Entregado" pide asignar un courier; permite cancelar el pedido desde cualquier estado no terminal.
- **Estadísticas** — total de pedidos, ingresos estimados (excluyendo cancelados), cantidad de personal de entrega, y detalle de todos los envíos con exportación a PDF.
- **Gestión de Personal** *(solo rol Administración)* — altas y bajas de couriers y de usuarios del sistema (Recepcionista/Supervisor); la cuenta de Administración nunca puede eliminarse.

## Interfaz

- **Login** — pantalla con estética retrowave: logo y formulario en una tarjeta translúcida sobre una ilustración de ciudad al atardecer.
- **Menú Principal** — fondo con foto de almacén; título, nombre de usuario y rol en texto claro para mantener la legibilidad sobre la imagen.
- El resto de pantallas (Registrar Envío, Consultar Tracking, Actualizar Estado, Estadísticas, Gestión de Personal, Listar Envíos) mantiene la apariencia por defecto de Swing.
- Las imágenes usadas (logo y fondos) están en `MavenProject/src/main/resources/img`.

## Roles de usuario

| Rol | Acceso |
|---|---|
| Administración | Todas las funcionalidades, incluida Gestión de Personal |
| Supervisor / Recepcionista | Registrar Envío, Consultar Tracking, Actualizar Estado, Estadísticas |

## Cómo ejecutarlo

### 1. Base de datos

Con MySQL 8 corriendo localmente, importa el script incluido (crea la base de datos, las tablas, los procedimientos almacenados y datos de prueba):

```bash
mysql -u root -p < FlashCourier.sql
```

### 2. Configurar la conexión

Por defecto, `ConexionDB` se conecta a `localhost:3306` con usuario `root` y sin contraseña. Si tu MySQL usa otras credenciales, edítalas en:

```
MavenProject/src/main/java/com/flashcourier/mavenproject/database/ConexionDB.java
```

### 3. Compilar y ejecutar

Desde la carpeta `MavenProject` (con Maven instalado):

```bash
mvn compile exec:java
```

También se puede abrir con NetBeans o con Visual Studio Code + la extensión *Extension Pack for Java* (opcional, no requerido).

### Credenciales de prueba

| Correo | Contraseña | Rol |
|---|---|---|
| admin@flashcourier.pe | admin | Administración |
| recepcion@flashcourier.pe | 1234 | Recepcionista |
| supervisor@flashcourier.pe | 1234 | Supervisor |

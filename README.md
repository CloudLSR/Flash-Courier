# Flash Courier

Sistema de gestión de envíos y logística **"Flash Courier"**. Java, Swing y MySQL, con arquitectura en capas (Facade + Controlador + DAO).

Aplicación de escritorio que simula el flujo completo de una agencia de mensajería: desde que se registra un paquete hasta que se confirma su entrega, pasando por el seguimiento en tiempo real, la generación de comprobantes en PDF y la gestión del personal que opera el sistema.

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
| Controlador | `controlador` | Lógica de negocio (cálculo de costos, generación de tracking, transiciones de estado) |
| DAO | `dao` | Acceso a datos, cada uno invoca sus procedimientos almacenados |
| Modelo | `modelo` | Entidades del dominio (POJOs) y patrón State (`modelo.state`) |
| Base de datos | `database.ConexionDB` | Conexión JDBC (patrón Singleton) |
| Reportes | `reportes` | Generación de PDFs (comprobante de envío y lista de envíos) con PDFBox |
| Interfaz | `ui` | Paleta de colores y componentes estilo retrowave (Login) y botones tipo píldora (Menú) |

Patrones de diseño aplicados:
- **Singleton** — `ConexionDB`, una única instancia de conexión.
- **Facade** — `CourierFacade`, oculta la complejidad de las demás capas a las vistas.
- **State** — `modelo.state`, cada estado de un envío (`Registrado`, `En Almacén`, `En Ruta`, `En Reparto`, `Entregado`) sabe cuál es su transición válida siguiente, evitando saltos de estado inválidos. `Cancelado` es un estado final alternativo, disponible desde cualquier estado no terminal.

## Funcionalidades

- **Registrar Envío** — captura remitente, destinatario y paquete; calcula el costo según el peso y genera un código de tracking único, mostrado en un diálogo con botón para copiarlo al portapapeles.
- **Consultar Tracking** — búsqueda pública por código: muestra todos los datos del envío, remitente, destinatario y paquete, el historial completo de movimientos (coloreado por estado) y permite descargar el comprobante en PDF.
- **Actualizar Estado** — avanza un envío al siguiente estado válido (patrón State); el courier se asigna una sola vez, justo al pasar a "En Reparto"; permite cancelar el pedido desde cualquier estado no terminal, con motivo obligatorio.
- **Estadísticas** — total de pedidos, ingresos estimados (excluyendo cancelados), cantidad de personal de entrega, y un gráfico de barras con el desglose por estado; acceso de solo lectura para todo el personal con permiso.
- **Listar Envíos** — detalle completo de todos los envíos en una tabla coloreada por estado, con exportación a PDF (se abre desde Estadísticas).
- **Gestión de Personal** — dos pestañas según el rol (ver tabla de roles más abajo):
  - **Personal de Entrega**: alta y baja de couriers. Un courier con un envío todavía en proceso (no Entregado ni Cancelado) no se puede eliminar hasta que ese envío llegue a un estado final.
  - **Usuarios del Sistema**: alta y baja de cuentas de acceso (Recepcionista/Supervisor). La cuenta con rol Administrador siempre aparece listada pero nunca se puede eliminar, ni se puede crear otra cuenta con ese rol desde el formulario (el combo de roles solo ofrece Recepcionista y Supervisor).

## Roles de usuario

| Rol | Registrar / Consultar / Actualizar | Estadísticas y Listar Envíos | Gestión de Personal — Personal de Entrega | Gestión de Personal — Usuarios del Sistema |
|---|---|---|---|---|
| Recepcionista | Sí | No (botones deshabilitados) | No | No |
| Supervisor | Sí | Sí | Sí (agregar/eliminar couriers) | No (no ve la pestaña) |
| Administrador | Sí | Sí | Sí | Sí (única cuenta que puede crear/eliminar usuarios del sistema; su propia cuenta nunca se puede eliminar ni duplicar) |

## Interfaz

- **Login** — pantalla con estética retrowave: logo y formulario en una tarjeta translúcida sobre un banner de ciudad al atardecer que se panea lentamente de un lado a otro.
- **Menú Principal** — fondo con foto de almacén; título, nombre de usuario y rol en texto de color sobre la imagen; botones de navegación en forma de píldora, habilitados o deshabilitados según el rol.
- El resto de pantallas (Registrar Envío, Consultar Tracking, Actualizar Estado, Estadísticas, Listar Envíos, Gestión de Personal) mantiene la apariencia por defecto de Swing.
- Las imágenes usadas (logo y fondos) están en `MavenProject/src/main/resources/img`.

## Cómo ejecutarlo

### 1. Base de datos

Con MySQL 8 corriendo localmente, importa el script incluido (crea la base de datos, las tablas, los procedimientos almacenados y datos de prueba):

```bash
mysql -u root -p < FlashCourier.sql
```

### 2. Configurar la conexión

Por defecto, `ConexionDB` se conecta a `localhost:3306` con usuario `root` y sin contraseña. Antes de compilar, revisa las credenciales en:

```
MavenProject/src/main/java/com/flashcourier/mavenproject/database/ConexionDB.java
```

### 3. Compilar y ejecutar

Desde la carpeta `MavenProject` (con Maven instalado):

```bash
mvn compile exec:java
```

También se puede abrir con NetBeans o con Visual Studio Code + la extensión *Extension Pack for Java*.

### Credenciales de prueba

| Correo | Contraseña | Rol |
|---|---|---|
| admin@flashcourier.pe | admin | Administrador |
| supervisor@flashcourier.pe | 1234 | Supervisor |
| recepcion@flashcourier.pe | 1234 | Recepcionista |

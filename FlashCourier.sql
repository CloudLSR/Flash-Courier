-- =====================================================================
-- Script de Base de Datos - Flash Courier
-- Motor: MySQL 8.0 (puerto por defecto 3306)
-- =====================================================================

CREATE DATABASE IF NOT EXISTS flashcourier;
USE flashcourier;

-- ---------------------------------------------------------------------
-- 1. TABLA: usuario  (login / control de acceso por rol)
-- ---------------------------------------------------------------------
CREATE TABLE IF NOT EXISTS usuario (
    id_usuario INT NOT NULL AUTO_INCREMENT,
    nombre VARCHAR(100) NOT NULL,
    correo VARCHAR(100) NOT NULL UNIQUE,
    contrasena VARCHAR(255) NOT NULL,
    rol VARCHAR(50) NOT NULL, -- 'Recepcionista', 'Operario', 'Supervisor', 'Courier', 'Administrador'
    PRIMARY KEY (id_usuario)
);

-- ---------------------------------------------------------------------
-- 2. TABLA: cliente  (remitente y destinatario)
-- ---------------------------------------------------------------------
CREATE TABLE IF NOT EXISTS cliente (
    id_cliente INT NOT NULL AUTO_INCREMENT,
    nombres VARCHAR(150) NOT NULL,
    dni VARCHAR(15) NOT NULL,
    telefono VARCHAR(20),
    direccion VARCHAR(255),
    PRIMARY KEY (id_cliente)
);

-- ---------------------------------------------------------------------
-- 3. TABLA: courier  (repartidores)
-- ---------------------------------------------------------------------
CREATE TABLE IF NOT EXISTS courier (
    id_courier INT NOT NULL AUTO_INCREMENT,
    nombre VARCHAR(100) NOT NULL,
    telefono VARCHAR(20),
    PRIMARY KEY (id_courier)
);

-- ---------------------------------------------------------------------
-- 4. TABLA: paquete
-- ---------------------------------------------------------------------
CREATE TABLE IF NOT EXISTS paquete (
    id_paquete INT NOT NULL AUTO_INCREMENT,
    peso DECIMAL(10,2) NOT NULL,
    dimensiones VARCHAR(50),
    descripcion VARCHAR(255),
    PRIMARY KEY (id_paquete)
);

-- ---------------------------------------------------------------------
-- 5. TABLA: envio  (entidad central del negocio)
-- ---------------------------------------------------------------------
CREATE TABLE IF NOT EXISTS envio (
    id_envio INT NOT NULL AUTO_INCREMENT,
    codigo_tracking VARCHAR(50) NOT NULL UNIQUE,
    fecha_registro DATETIME NOT NULL,
    estado VARCHAR(50) NOT NULL, -- Registrado, En Almacen, En Ruta, En Reparto, Entregado, Cancelado
    direccion_destino VARCHAR(255) NOT NULL,
    costo DECIMAL(10,2) NOT NULL,
    id_remitente INT NOT NULL,
    id_destinatario INT NOT NULL,
    id_paquete INT NOT NULL,
    id_courier INT NULL,
    PRIMARY KEY (id_envio),
    CONSTRAINT fk_envio_remitente FOREIGN KEY (id_remitente) REFERENCES cliente(id_cliente),
    CONSTRAINT fk_envio_destinatario FOREIGN KEY (id_destinatario) REFERENCES cliente(id_cliente),
    CONSTRAINT fk_envio_paquete FOREIGN KEY (id_paquete) REFERENCES paquete(id_paquete),
    CONSTRAINT fk_envio_courier FOREIGN KEY (id_courier) REFERENCES courier(id_courier)
);

-- ---------------------------------------------------------------------
-- 6. TABLA: historial_estado  (trazabilidad / tracking)
-- ---------------------------------------------------------------------
CREATE TABLE IF NOT EXISTS historial_estado (
    id_historial INT NOT NULL AUTO_INCREMENT,
    id_envio INT NOT NULL,
    estado VARCHAR(50) NOT NULL,
    fecha_hora DATETIME NOT NULL,
    observacion VARCHAR(255),
    PRIMARY KEY (id_historial),
    CONSTRAINT fk_historial_envio FOREIGN KEY (id_envio) REFERENCES envio(id_envio)
);

-- =====================================================================
-- PROCEDIMIENTOS ALMACENADOS (9, uno por cada operacion del prototipo)
-- =====================================================================
DELIMITER //

-- 1. Registrar cliente (remitente o destinatario)
CREATE PROCEDURE sp_registrar_cliente(
    IN p_nombres VARCHAR(150), IN p_dni VARCHAR(15),
    IN p_telefono VARCHAR(20), IN p_direccion VARCHAR(255),
    OUT p_id_cliente INT)
BEGIN
    INSERT INTO cliente(nombres, dni, telefono, direccion)
    VALUES (p_nombres, p_dni, p_telefono, p_direccion);
    SET p_id_cliente = LAST_INSERT_ID();
END //

-- 2. Registrar paquete
CREATE PROCEDURE sp_registrar_paquete(
    IN p_peso DECIMAL(10,2), IN p_dimensiones VARCHAR(50), IN p_descripcion VARCHAR(255),
    OUT p_id_paquete INT)
BEGIN
    INSERT INTO paquete(peso, dimensiones, descripcion)
    VALUES (p_peso, p_dimensiones, p_descripcion);
    SET p_id_paquete = LAST_INSERT_ID();
END //

-- 3. Registrar envio
CREATE PROCEDURE sp_registrar_envio(
    IN p_codigo_tracking VARCHAR(50), IN p_direccion_destino VARCHAR(255),
    IN p_costo DECIMAL(10,2), IN p_id_remitente INT, IN p_id_destinatario INT,
    IN p_id_paquete INT,
    OUT p_id_envio INT)
BEGIN
    INSERT INTO envio(codigo_tracking, fecha_registro, estado, direccion_destino, costo,
                       id_remitente, id_destinatario, id_paquete)
    VALUES (p_codigo_tracking, NOW(), 'Registrado', p_direccion_destino, p_costo,
            p_id_remitente, p_id_destinatario, p_id_paquete);
    SET p_id_envio = LAST_INSERT_ID();
END //

-- 4. Registrar un cambio de estado en el historial
CREATE PROCEDURE sp_registrar_historial(
    IN p_id_envio INT, IN p_estado VARCHAR(50), IN p_observacion VARCHAR(255))
BEGIN
    INSERT INTO historial_estado(id_envio, estado, fecha_hora, observacion)
    VALUES (p_id_envio, p_estado, NOW(), p_observacion);
END //

-- 5. Actualizar el estado de un envio
CREATE PROCEDURE sp_actualizar_estado_envio(
    IN p_id_envio INT, IN p_nuevo_estado VARCHAR(50))
BEGIN
    UPDATE envio SET estado = p_nuevo_estado WHERE id_envio = p_id_envio;
END //

-- 6. Confirmar entrega (asigna courier y marca Entregado)
CREATE PROCEDURE sp_confirmar_entrega(
    IN p_id_envio INT, IN p_id_courier INT)
BEGIN
    UPDATE envio SET estado = 'Entregado', id_courier = p_id_courier WHERE id_envio = p_id_envio;
END //

-- 7. Consultar envio por codigo de tracking
CREATE PROCEDURE sp_consultar_envio_por_tracking(IN p_codigo_tracking VARCHAR(50))
BEGIN
    SELECT e.id_envio, e.codigo_tracking, e.fecha_registro, e.estado, e.direccion_destino,
           e.costo,
           r.nombres AS remitente, r.dni AS remitente_dni, r.telefono AS remitente_telefono, r.direccion AS remitente_direccion,
           d.nombres AS destinatario, d.dni AS destinatario_dni, d.telefono AS destinatario_telefono,
           p.peso, p.dimensiones, p.descripcion AS descripcion_paquete,
           e.id_courier
    FROM envio e
    JOIN cliente r ON e.id_remitente = r.id_cliente
    JOIN cliente d ON e.id_destinatario = d.id_cliente
    JOIN paquete p ON e.id_paquete = p.id_paquete
    WHERE e.codigo_tracking = p_codigo_tracking;
END //

-- 8. Consultar historial de un envio
CREATE PROCEDURE sp_consultar_historial_por_envio(IN p_id_envio INT)
BEGIN
    SELECT estado, fecha_hora, observacion
    FROM historial_estado
    WHERE id_envio = p_id_envio
    ORDER BY fecha_hora ASC;
END //

-- 9. Listar couriers disponibles (para asignar en confirmar entrega)
CREATE PROCEDURE sp_listar_couriers()
BEGIN
    SELECT id_courier, nombre, telefono FROM courier
    ORDER BY id_courier ASC;
END //

-- 10. Registrar un nuevo courier (personal de entrega)
CREATE PROCEDURE sp_registrar_courier(
    IN p_nombre VARCHAR(100), IN p_telefono VARCHAR(20),
    OUT p_id_courier INT)
BEGIN
    INSERT INTO courier(nombre, telefono) VALUES (p_nombre, p_telefono);
    SET p_id_courier = LAST_INSERT_ID();
END //

-- 11. Eliminar un courier (solo si no tiene entregas asociadas, por la FK)
CREATE PROCEDURE sp_eliminar_courier(IN p_id_courier INT)
BEGIN
    DELETE FROM courier WHERE id_courier = p_id_courier;
END //

-- 12. Contar envios agrupados por estado (para el dashboard de estadisticas)
CREATE PROCEDURE sp_contar_envios_por_estado()
BEGIN
    SELECT estado, COUNT(*) AS cantidad FROM envio GROUP BY estado;
END //

-- 13. Resumen general para la pantalla de Estadisticas (total pedidos, ingresos
--     estimados excluyendo cancelados, y total de couriers)
CREATE PROCEDURE sp_estadisticas_generales()
BEGIN
    SELECT
        (SELECT COUNT(*) FROM envio) AS total_envios,
        (SELECT IFNULL(SUM(costo), 0) FROM envio WHERE estado <> 'Cancelado') AS ingresos_estimados,
        (SELECT COUNT(*) FROM courier) AS total_couriers;
END //

-- 14. Listar el personal que usa la app (Recepcionista, Supervisor, Administrador)
CREATE PROCEDURE sp_listar_usuarios()
BEGIN
    SELECT id_usuario, nombre, correo, rol
    FROM usuario
    ORDER BY id_usuario ASC;
END //

-- 15. Registrar un nuevo usuario de la app (Gestion de Personal, solo admin)
CREATE PROCEDURE sp_registrar_usuario(
    IN p_nombre VARCHAR(100), IN p_correo VARCHAR(100),
    IN p_contrasena VARCHAR(255), IN p_rol VARCHAR(50),
    OUT p_id_usuario INT)
BEGIN
    INSERT INTO usuario(nombre, correo, contrasena, rol) VALUES (p_nombre, p_correo, p_contrasena, p_rol);
    SET p_id_usuario = LAST_INSERT_ID();
END //

-- 16. Eliminar un usuario de la app. La cuenta con rol Administrador queda
--     protegida a nivel de base de datos (nunca se borra, ni por error).
CREATE PROCEDURE sp_eliminar_usuario(IN p_id_usuario INT)
BEGIN
    DELETE FROM usuario WHERE id_usuario = p_id_usuario AND rol <> 'Administrador';
END //

-- 17. Listar todos los envios con el courier asignado (si tiene), para la
--     pantalla de detalle completo ("Ver detalle de todos los envios")
CREATE PROCEDURE sp_listar_envios()
BEGIN
    SELECT e.id_envio, e.codigo_tracking, e.fecha_registro, e.estado,
           e.direccion_destino, e.costo,
           r.nombres AS nombre_remitente, d.nombres AS nombre_destinatario,
           p.peso, p.dimensiones,
           c.nombre AS nombre_courier
    FROM envio e
    JOIN cliente r ON e.id_remitente = r.id_cliente
    JOIN cliente d ON e.id_destinatario = d.id_cliente
    JOIN paquete p ON e.id_paquete = p.id_paquete
    LEFT JOIN courier c ON e.id_courier = c.id_courier
    ORDER BY e.fecha_registro DESC;
END //

DELIMITER ;

-- =====================================================================
-- DATOS DE PRUEBA
-- =====================================================================
INSERT INTO usuario (nombre, correo, contrasena, rol) VALUES
('Admin', 'admin@flashcourier.pe', 'admin', 'Administrador'),
('José Sánchez', 'supervisor@flashcourier.pe', '1234', 'Supervisor'),
('Alizon Ramos', 'recepcion@flashcourier.pe', '1234', 'Recepcionista');

INSERT INTO courier (nombre, telefono) VALUES
('Jhonatan Flores', '964218863'),
('Augusto Corrales', '940336633'),
('Abbiel Sigueñas', '953350326');

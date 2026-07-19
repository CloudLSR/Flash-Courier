-- =====================================================================
-- Script de Base de Datos - Flash Courier
-- Creacion de base de datos y tablas
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
    rol VARCHAR(50) NOT NULL, -- 'Recepcionista', 'Operario', 'Supervisor', 'Courier', 'Administracion'
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
    estado VARCHAR(50) NOT NULL, -- Registrado, En Almacen, En Ruta, En Reparto, Entregado
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

-- =====================================================================
--  DOHELP - Script de la base de datos (modelo físico)
--  Evidencia: GA6-220501096-AA2-EV03 - Script bases de datos del proyecto
--  Aprendiz : Víctor Manuel Ruiz Rojas - ADSO (228118) - Ficha 3336220
--  Motor    : MySQL 8.0 o superior (InnoDB, utf8mb4)
--  Base     : modelo lógico y diccionario de datos de GA4-220501095-AA1-EV02
-- =====================================================================

DROP DATABASE IF EXISTS dohelp;
CREATE DATABASE dohelp
  CHARACTER SET utf8mb4
  COLLATE utf8mb4_spanish_ci;
USE dohelp;

-- ---------------------------------------------------------------------
-- 1. USUARIOS Y ROLES
-- ---------------------------------------------------------------------

-- Personas registradas en la plataforma (clientes, trabajadores, administradores)
CREATE TABLE usuario (
  id_usuario      INT           NOT NULL AUTO_INCREMENT,
  nombre          VARCHAR(100)  NOT NULL,
  apellido        VARCHAR(100)  NOT NULL,
  correo          VARCHAR(100)  NOT NULL,
  telefono        VARCHAR(20)   NULL,
  contrasena      VARCHAR(255)  NOT NULL COMMENT 'Hash de la contraseña (nunca texto plano)',
  fecha_registro  DATE          NOT NULL DEFAULT (CURRENT_DATE),
  CONSTRAINT pk_usuario PRIMARY KEY (id_usuario),
  CONSTRAINT uq_usuario_correo UNIQUE (correo),
  CONSTRAINT ck_usuario_correo CHECK (correo LIKE '%_@_%._%')
) ENGINE = InnoDB;

-- Roles disponibles: cliente, trabajador, administrador
CREATE TABLE rol (
  id_rol      INT          NOT NULL AUTO_INCREMENT,
  nombre_rol  VARCHAR(50)  NOT NULL,
  CONSTRAINT pk_rol PRIMARY KEY (id_rol),
  CONSTRAINT uq_rol_nombre UNIQUE (nombre_rol)
) ENGINE = InnoDB;

-- Relación muchos a muchos: un usuario puede tener varios roles
CREATE TABLE usuario_rol (
  id_usuario_rol  INT  NOT NULL AUTO_INCREMENT,
  id_usuario      INT  NOT NULL,
  id_rol          INT  NOT NULL,
  CONSTRAINT pk_usuario_rol PRIMARY KEY (id_usuario_rol),
  CONSTRAINT uq_usuario_rol UNIQUE (id_usuario, id_rol),
  CONSTRAINT fk_usuariorol_usuario FOREIGN KEY (id_usuario)
    REFERENCES usuario (id_usuario) ON DELETE CASCADE ON UPDATE CASCADE,
  CONSTRAINT fk_usuariorol_rol FOREIGN KEY (id_rol)
    REFERENCES rol (id_rol) ON DELETE RESTRICT ON UPDATE CASCADE
) ENGINE = InnoDB;

-- ---------------------------------------------------------------------
-- 2. CATÁLOGOS
-- ---------------------------------------------------------------------

CREATE TABLE categoria (
  id_categoria      INT           NOT NULL AUTO_INCREMENT,
  nombre_categoria  VARCHAR(100)  NOT NULL,
  CONSTRAINT pk_categoria PRIMARY KEY (id_categoria),
  CONSTRAINT uq_categoria_nombre UNIQUE (nombre_categoria)
) ENGINE = InnoDB;

-- Subcategorías de cada categoría (p. ej. Hogar > Plomería)
CREATE TABLE subcategoria (
  id_subcategoria      INT           NOT NULL AUTO_INCREMENT,
  nombre_subcategoria  VARCHAR(100)  NOT NULL,
  id_categoria         INT           NOT NULL,
  CONSTRAINT pk_subcategoria PRIMARY KEY (id_subcategoria),
  CONSTRAINT uq_subcategoria UNIQUE (id_categoria, nombre_subcategoria),
  CONSTRAINT fk_subcategoria_categoria FOREIGN KEY (id_categoria)
    REFERENCES categoria (id_categoria) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE = InnoDB;

CREATE TABLE ciudad (
  id_ciudad      INT           NOT NULL AUTO_INCREMENT,
  nombre_ciudad  VARCHAR(100)  NOT NULL,
  CONSTRAINT pk_ciudad PRIMARY KEY (id_ciudad),
  CONSTRAINT uq_ciudad_nombre UNIQUE (nombre_ciudad)
) ENGINE = InnoDB;

-- ---------------------------------------------------------------------
-- 3. SERVICIOS (ANUNCIOS)
-- ---------------------------------------------------------------------

CREATE TABLE servicio (
  id_servicio        INT            NOT NULL AUTO_INCREMENT,
  titulo             VARCHAR(100)   NOT NULL,
  descripcion        TEXT           NOT NULL,
  tipo_precio        VARCHAR(20)    NOT NULL DEFAULT 'cotizacion',
  valor              DECIMAL(10,2)  NULL,
  modalidad          VARCHAR(20)    NOT NULL DEFAULT 'presencial',
  fecha_publicacion  DATE           NOT NULL DEFAULT (CURRENT_DATE),
  estado             VARCHAR(20)    NOT NULL DEFAULT 'pendiente',
  id_usuario         INT            NOT NULL COMMENT 'Trabajador que publica el anuncio',
  id_categoria       INT            NOT NULL,
  id_subcategoria    INT            NULL,
  id_ciudad          INT            NOT NULL,
  CONSTRAINT pk_servicio PRIMARY KEY (id_servicio),
  CONSTRAINT ck_servicio_tipo_precio CHECK (tipo_precio IN ('fijo', 'desde', 'cotizacion')),
  CONSTRAINT ck_servicio_valor CHECK (valor IS NULL OR valor >= 0),
  CONSTRAINT ck_servicio_modalidad CHECK (modalidad IN ('presencial', 'domicilio', 'virtual')),
  CONSTRAINT ck_servicio_estado CHECK (estado IN ('pendiente', 'activo', 'pausado', 'rechazado')),
  CONSTRAINT fk_servicio_usuario FOREIGN KEY (id_usuario)
    REFERENCES usuario (id_usuario) ON DELETE CASCADE ON UPDATE CASCADE,
  CONSTRAINT fk_servicio_categoria FOREIGN KEY (id_categoria)
    REFERENCES categoria (id_categoria) ON DELETE RESTRICT ON UPDATE CASCADE,
  CONSTRAINT fk_servicio_subcategoria FOREIGN KEY (id_subcategoria)
    REFERENCES subcategoria (id_subcategoria) ON DELETE SET NULL ON UPDATE CASCADE,
  CONSTRAINT fk_servicio_ciudad FOREIGN KEY (id_ciudad)
    REFERENCES ciudad (id_ciudad) ON DELETE RESTRICT ON UPDATE CASCADE
) ENGINE = InnoDB;

-- Índices para las búsquedas y filtros del sitio
CREATE INDEX idx_servicio_estado ON servicio (estado);
CREATE INDEX idx_servicio_cat_ciudad ON servicio (id_categoria, id_ciudad);
CREATE FULLTEXT INDEX ftx_servicio_texto ON servicio (titulo, descripcion);

CREATE TABLE imagen_servicio (
  id_imagen    INT           NOT NULL AUTO_INCREMENT,
  url_imagen   VARCHAR(255)  NOT NULL,
  descripcion  VARCHAR(255)  NULL,
  id_servicio  INT           NOT NULL,
  CONSTRAINT pk_imagen_servicio PRIMARY KEY (id_imagen),
  CONSTRAINT fk_imagen_servicio FOREIGN KEY (id_servicio)
    REFERENCES servicio (id_servicio) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE = InnoDB;

-- ---------------------------------------------------------------------
-- 4. SOLICITUDES, RESEÑAS Y FAVORITOS
-- ---------------------------------------------------------------------

CREATE TABLE solicitud_servicio (
  id_solicitud     INT          NOT NULL AUTO_INCREMENT,
  fecha_solicitud  DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP,
  estado           VARCHAR(20)  NOT NULL DEFAULT 'pendiente',
  id_usuario       INT          NOT NULL COMMENT 'Cliente que realiza la solicitud',
  id_servicio      INT          NOT NULL,
  CONSTRAINT pk_solicitud_servicio PRIMARY KEY (id_solicitud),
  CONSTRAINT ck_solicitud_estado CHECK (estado IN ('pendiente', 'aceptada', 'rechazada', 'finalizada', 'cancelada')),
  CONSTRAINT fk_solicitud_usuario FOREIGN KEY (id_usuario)
    REFERENCES usuario (id_usuario) ON DELETE CASCADE ON UPDATE CASCADE,
  CONSTRAINT fk_solicitud_servicio FOREIGN KEY (id_servicio)
    REFERENCES servicio (id_servicio) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE = InnoDB;

CREATE TABLE resena (
  id_resena     INT      NOT NULL AUTO_INCREMENT,
  calificacion  TINYINT  NOT NULL,
  comentario    TEXT     NULL,
  fecha         DATE     NOT NULL DEFAULT (CURRENT_DATE),
  id_usuario    INT      NOT NULL COMMENT 'Usuario que califica',
  id_servicio   INT      NOT NULL,
  CONSTRAINT pk_resena PRIMARY KEY (id_resena),
  CONSTRAINT uq_resena_usuario_servicio UNIQUE (id_usuario, id_servicio),
  CONSTRAINT ck_resena_calificacion CHECK (calificacion BETWEEN 1 AND 5),
  CONSTRAINT fk_resena_usuario FOREIGN KEY (id_usuario)
    REFERENCES usuario (id_usuario) ON DELETE CASCADE ON UPDATE CASCADE,
  CONSTRAINT fk_resena_servicio FOREIGN KEY (id_servicio)
    REFERENCES servicio (id_servicio) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE = InnoDB;

CREATE TABLE favorito (
  id_favorito     INT   NOT NULL AUTO_INCREMENT,
  id_usuario      INT   NOT NULL,
  id_servicio     INT   NOT NULL,
  fecha_agregado  DATE  NOT NULL DEFAULT (CURRENT_DATE),
  CONSTRAINT pk_favorito PRIMARY KEY (id_favorito),
  CONSTRAINT uq_favorito UNIQUE (id_usuario, id_servicio),
  CONSTRAINT fk_favorito_usuario FOREIGN KEY (id_usuario)
    REFERENCES usuario (id_usuario) ON DELETE CASCADE ON UPDATE CASCADE,
  CONSTRAINT fk_favorito_servicio FOREIGN KEY (id_servicio)
    REFERENCES servicio (id_servicio) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE = InnoDB;

-- ---------------------------------------------------------------------
-- 5. CONFIANZA Y SEGURIDAD
-- ---------------------------------------------------------------------

CREATE TABLE verificacion_trabajador (
  id_verificacion      INT           NOT NULL AUTO_INCREMENT,
  id_usuario           INT           NOT NULL,
  documento_identidad  VARCHAR(50)   NOT NULL,
  antecedentes         VARCHAR(255)  NULL COMMENT 'Ruta del soporte cargado',
  referencia_laboral   VARCHAR(255)  NULL,
  estado_verificacion  VARCHAR(20)   NOT NULL DEFAULT 'pendiente',
  fecha_verificacion   DATE          NULL,
  CONSTRAINT pk_verificacion PRIMARY KEY (id_verificacion),
  CONSTRAINT uq_verificacion_usuario UNIQUE (id_usuario),
  CONSTRAINT ck_verificacion_estado CHECK (estado_verificacion IN ('pendiente', 'aprobada', 'rechazada')),
  CONSTRAINT fk_verificacion_usuario FOREIGN KEY (id_usuario)
    REFERENCES usuario (id_usuario) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE = InnoDB;

-- Denuncias sobre un anuncio o sobre una solicitud (moderación)
CREATE TABLE reporte_problema (
  id_reporte      INT           NOT NULL AUTO_INCREMENT,
  id_usuario      INT           NOT NULL COMMENT 'Usuario que reporta',
  id_servicio     INT           NULL,
  id_solicitud    INT           NULL,
  tipo_problema   VARCHAR(100)  NOT NULL,
  descripcion     TEXT          NOT NULL,
  fecha_reporte   DATETIME      NOT NULL DEFAULT CURRENT_TIMESTAMP,
  estado_reporte  VARCHAR(20)   NOT NULL DEFAULT 'abierto',
  CONSTRAINT pk_reporte_problema PRIMARY KEY (id_reporte),
  CONSTRAINT ck_reporte_estado CHECK (estado_reporte IN ('abierto', 'en_revision', 'cerrado')),
  CONSTRAINT fk_reporte_usuario FOREIGN KEY (id_usuario)
    REFERENCES usuario (id_usuario) ON DELETE CASCADE ON UPDATE CASCADE,
  CONSTRAINT fk_reporte_servicio FOREIGN KEY (id_servicio)
    REFERENCES servicio (id_servicio) ON DELETE SET NULL ON UPDATE CASCADE,
  CONSTRAINT fk_reporte_solicitud FOREIGN KEY (id_solicitud)
    REFERENCES solicitud_servicio (id_solicitud) ON DELETE SET NULL ON UPDATE CASCADE
) ENGINE = InnoDB;

-- ---------------------------------------------------------------------
-- 6. COMUNICACIÓN Y PAGOS (fases posteriores al MVP)
-- ---------------------------------------------------------------------

CREATE TABLE chat (
  id_chat                INT       NOT NULL AUTO_INCREMENT,
  id_usuario_cliente     INT       NOT NULL,
  id_usuario_trabajador  INT       NOT NULL,
  fecha_creacion         DATETIME  NOT NULL DEFAULT CURRENT_TIMESTAMP,
  CONSTRAINT pk_chat PRIMARY KEY (id_chat),
  CONSTRAINT uq_chat_participantes UNIQUE (id_usuario_cliente, id_usuario_trabajador),
  CONSTRAINT fk_chat_cliente FOREIGN KEY (id_usuario_cliente)
    REFERENCES usuario (id_usuario) ON DELETE CASCADE ON UPDATE CASCADE,
  CONSTRAINT fk_chat_trabajador FOREIGN KEY (id_usuario_trabajador)
    REFERENCES usuario (id_usuario) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE = InnoDB;

CREATE TABLE mensaje (
  id_mensaje   INT       NOT NULL AUTO_INCREMENT,
  id_chat      INT       NOT NULL,
  id_usuario   INT       NOT NULL COMMENT 'Usuario que envía el mensaje',
  contenido    TEXT      NOT NULL,
  fecha_envio  DATETIME  NOT NULL DEFAULT CURRENT_TIMESTAMP,
  CONSTRAINT pk_mensaje PRIMARY KEY (id_mensaje),
  CONSTRAINT fk_mensaje_chat FOREIGN KEY (id_chat)
    REFERENCES chat (id_chat) ON DELETE CASCADE ON UPDATE CASCADE,
  CONSTRAINT fk_mensaje_usuario FOREIGN KEY (id_usuario)
    REFERENCES usuario (id_usuario) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE = InnoDB;

CREATE TABLE pago (
  id_pago       INT            NOT NULL AUTO_INCREMENT,
  id_solicitud  INT            NOT NULL,
  metodo_pago   VARCHAR(20)    NOT NULL,
  valor_pago    DECIMAL(10,2)  NOT NULL,
  fecha_pago    DATETIME       NOT NULL DEFAULT CURRENT_TIMESTAMP,
  estado_pago   VARCHAR(20)    NOT NULL DEFAULT 'pendiente',
  CONSTRAINT pk_pago PRIMARY KEY (id_pago),
  CONSTRAINT ck_pago_metodo CHECK (metodo_pago IN ('efectivo', 'tarjeta', 'transferencia', 'pse')),
  CONSTRAINT ck_pago_valor CHECK (valor_pago >= 0),
  CONSTRAINT ck_pago_estado CHECK (estado_pago IN ('pendiente', 'aprobado', 'rechazado', 'reembolsado')),
  CONSTRAINT fk_pago_solicitud FOREIGN KEY (id_solicitud)
    REFERENCES solicitud_servicio (id_solicitud) ON DELETE RESTRICT ON UPDATE CASCADE
) ENGINE = InnoDB;

-- Fin del script de estructura

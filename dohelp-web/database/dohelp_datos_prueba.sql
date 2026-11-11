-- =====================================================================
--  DOHELP - Datos iniciales y de prueba
--  Evidencia: GA6-220501096-AA2-EV03
--  Ejecutar DESPUÉS de dohelp_script.sql
--  Los usuarios y anuncios son FICTICIOS, solo para probar la base de datos.
-- =====================================================================

USE dohelp;

-- Catálogos ------------------------------------------------------------
INSERT INTO rol (nombre_rol) VALUES
  ('cliente'), ('trabajador'), ('administrador');

INSERT INTO categoria (nombre_categoria) VALUES
  ('Hogar y reparaciones'),
  ('Educación y clases'),
  ('Tecnología'),
  ('Belleza y cuidado personal');

INSERT INTO subcategoria (nombre_subcategoria, id_categoria) VALUES
  ('Plomería', 1), ('Electricidad', 1), ('Limpieza del hogar', 1),
  ('Matemáticas', 2), ('Idiomas', 2), ('Música', 2),
  ('Soporte informático', 3), ('Configuración de equipos', 3),
  ('Manicure y pedicure', 4), ('Peluquería', 4);

INSERT INTO ciudad (nombre_ciudad) VALUES
  ('Bogotá D.C.'), ('Soacha'), ('Chía');

-- Usuarios (contraseñas: hashes de ejemplo, no reales) -----------------
INSERT INTO usuario (nombre, apellido, correo, telefono, contrasena) VALUES
  ('Admin',  'DOHELP',  'admin@dohelp.test',        '3000000000', '$2b$10$ejemploHashAdmin000000000000000000000000000000000000'),
  ('Laura',  'Pérez',   'laura.perez@correo.test',  '3001112233', '$2b$10$ejemploHashLaura000000000000000000000000000000000000'),
  ('Camilo', 'Torres',  'camilo.torres@correo.test','3104445566', '$2b$10$ejemploHashCamilo00000000000000000000000000000000000'),
  ('Andrea', 'Gómez',   'andrea.gomez@correo.test', '3157778899', '$2b$10$ejemploHashAndrea00000000000000000000000000000000000'),
  ('Julián', 'Rojas',   'julian.rojas@correo.test', '3201234567', '$2b$10$ejemploHashJulian00000000000000000000000000000000000');

INSERT INTO usuario_rol (id_usuario, id_rol) VALUES
  (1, 3),          -- Admin: administrador
  (2, 1),          -- Laura: cliente
  (3, 2),          -- Camilo: trabajador
  (4, 2), (4, 1),  -- Andrea: trabajadora y cliente
  (5, 1);          -- Julián: cliente

-- Anuncios ---------------------------------------------------------------
INSERT INTO servicio (titulo, descripcion, tipo_precio, valor, modalidad, estado, id_usuario, id_categoria, id_subcategoria, id_ciudad) VALUES
  ('Reparación de tomas e instalaciones eléctricas',
   'Revisión y reparación de tomas, interruptores y tableros en casas y apartamentos.',
   'desde', 70000.00, 'domicilio', 'activo', 3, 1, 2, 1),
  ('Arreglo de fugas y destape de tuberías',
   'Solución de fugas en lavaplatos, baños y duchas. Incluye diagnóstico.',
   'cotizacion', NULL, 'domicilio', 'activo', 3, 1, 1, 1),
  ('Clases de inglés para principiantes',
   'Clases personalizadas de conversación y gramática básica.',
   'fijo', 45000.00, 'virtual', 'activo', 4, 2, 5, 1),
  ('Manicure y pedicure a domicilio',
   'Servicio completo con esmaltado tradicional o semipermanente.',
   'desde', 35000.00, 'domicilio', 'pendiente', 4, 4, 9, 3);

INSERT INTO imagen_servicio (url_imagen, descripcion, id_servicio) VALUES
  ('/uploads/servicios/1/tablero.jpg', 'Revisión de tablero eléctrico', 1),
  ('/uploads/servicios/3/clase.jpg',   'Clase virtual de inglés', 3);

-- Interacciones ---------------------------------------------------------
INSERT INTO favorito (id_usuario, id_servicio) VALUES
  (2, 1), (2, 3), (5, 2);

INSERT INTO solicitud_servicio (estado, id_usuario, id_servicio) VALUES
  ('finalizada', 2, 1),
  ('pendiente',  5, 2);

INSERT INTO resena (calificacion, comentario, id_usuario, id_servicio) VALUES
  (5, 'Puntual y explicó muy bien el problema.', 2, 1);

INSERT INTO verificacion_trabajador (id_usuario, documento_identidad, estado_verificacion) VALUES
  (3, 'CC-0000000001', 'pendiente');

INSERT INTO reporte_problema (id_usuario, id_servicio, tipo_problema, descripcion) VALUES
  (5, 4, 'Información incompleta', 'El anuncio no indica las zonas de cobertura.');

-- Consultas de verificación ---------------------------------------------
-- Anuncios activos con su categoría, ciudad y anunciante
SELECT s.id_servicio, s.titulo, c.nombre_categoria, ci.nombre_ciudad,
       CONCAT(u.nombre, ' ', u.apellido) AS anunciante, s.modalidad, s.valor
FROM servicio s
JOIN categoria c ON c.id_categoria = s.id_categoria
JOIN ciudad ci   ON ci.id_ciudad   = s.id_ciudad
JOIN usuario u   ON u.id_usuario   = s.id_usuario
WHERE s.estado = 'activo';

-- Búsqueda por palabra clave (índice FULLTEXT)
SELECT id_servicio, titulo
FROM servicio
WHERE MATCH(titulo, descripcion) AGAINST ('inglés');

-- Cantidad de registros por tabla principal
SELECT (SELECT COUNT(*) FROM usuario)  AS usuarios,
       (SELECT COUNT(*) FROM servicio) AS anuncios,
       (SELECT COUNT(*) FROM resena)   AS resenas;

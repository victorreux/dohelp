# DOHELP – Módulo de gestión de anuncios (JDBC)

Evidencia **GA7-220501096-AA2-EV01 – Codificación de módulos del software**
Aprendiz: Víctor Manuel Ruiz Rojas – Ficha 3336220 – SENA, Tecnólogo en Análisis y Desarrollo de Software.

DOHELP es una plataforma web de anuncios clasificados de servicios: los trabajadores publican lo que ofrecen
y los clientes los buscan por categoría y ciudad para contactarlos directamente.

Este módulo implementa la **gestión de anuncios** (tabla `servicio`) conectándose a MySQL por medio de **JDBC**,
con las cuatro operaciones del CRUD:

| Operación | Opción del menú | Método |
|---|---|---|
| Inserción | 4. Publicar un anuncio nuevo | `AnuncioDaoJdbc.insertar()` |
| Consulta | 1, 2 y 3. Listar, buscar por ciudad y consultar por id | `listarTodos()`, `buscarActivosPorCiudad()`, `buscarPorId()` |
| Actualización | 5. Actualizar un anuncio | `actualizar()` |
| Eliminación | 6. Eliminar un anuncio | `eliminar()` |

## Tecnologías

- Java 17
- Maven
- JDBC con el driver `mysql-connector-j` 8.4.0
- MySQL 8 (base de datos `dohelp`)

## Estructura del proyecto

```
dohelp-jdbc/
├── pom.xml                         Configuración de Maven y dependencias
├── database/
│   ├── dohelp_script.sql           Creación de la base de datos y sus 16 tablas
│   └── dohelp_datos_prueba.sql     Datos de prueba
└── src/main/
    ├── java/co/dohelp/
    │   ├── Aplicacion.java          Clase principal (método main)
    │   ├── config/ConexionBD.java   Conexión JDBC con MySQL
    │   ├── anuncio/
    │   │   ├── Anuncio.java         Entidad (tabla servicio)
    │   │   ├── AnuncioDao.java      Interfaz con las operaciones CRUD
    │   │   ├── AnuncioDaoJdbc.java  Implementación del CRUD con JDBC
    │   │   └── AnuncioService.java  Reglas de negocio y validaciones
    │   ├── catalogo/
    │   │   ├── CatalogoDao.java     Consulta de ciudades, categorías y trabajadores
    │   │   └── Opcion.java          Elemento de un catálogo
    │   ├── consola/MenuAnuncios.java  Menú de consola
    │   └── excepcion/DatosException.java  Errores con mensajes entendibles
    └── resources/
        └── db.properties.example   Plantilla de los datos de conexión
```

## Estándares de codificación aplicados

Según la evidencia GA7-220501096-AA1-EV02:

- **Paquetes:** en minúscula y por módulo funcional (`co.dohelp.anuncio`, `co.dohelp.catalogo`).
- **Clases:** PascalCase con sufijo según su capa (`Anuncio`, `AnuncioDao`, `AnuncioService`, `MenuAnuncios`).
- **Métodos:** camelCase que empiezan con verbo (`publicarAnuncio`, `buscarPorId`, `eliminarAnuncio`).
- **Variables:** camelCase descriptivas (`idAnuncio`, `fechaPublicacion`, `nombreCiudad`).
- **Constantes:** mayúsculas con guion bajo (`MAX_LONGITUD_TITULO`, `SQL_INSERTAR`).
- Consultas con `PreparedStatement` (evita la inyección SQL) y recursos cerrados con try-with-resources.
- Dinero con `BigDecimal` y fechas con `LocalDate`.
- Javadoc en las clases y los métodos públicos.

## Cómo ejecutarlo

1. Crear la base de datos en MySQL Workbench ejecutando `database/dohelp_script.sql` y luego
   `database/dohelp_datos_prueba.sql` (si aún no existe).
2. Copiar `src/main/resources/db.properties.example` como `db.properties` en la misma carpeta y escribir
   la contraseña de MySQL en `db.contrasena`.
3. Abrir la carpeta `dohelp-jdbc` en IntelliJ IDEA (o NetBeans / VS Code) como proyecto Maven.
4. Ejecutar la clase `co.dohelp.Aplicacion`.

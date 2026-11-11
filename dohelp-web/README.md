# DOHELP – Módulo web de gestión de anuncios (Servlets y JSP)

Evidencia **GA7-220501096-AA2-EV02 – Módulos de software codificados y probados**
Aprendiz: Víctor Manuel Ruiz Rojas – Ficha 3336220 – SENA, Tecnólogo en Análisis y Desarrollo de Software.

Versión web del módulo de gestión de anuncios de DOHELP (evidencia AA2-EV01). Las páginas usan
**formularios HTML** que envían los datos a un **servlet**, el cual se conecta a MySQL con **JDBC**
y muestra los resultados en páginas **JSP**.

## Lo que pide la guía y dónde está

| Requisito | Dónde se cumple |
|---|---|
| Formularios HTML con servlets | `formulario.jsp` (publicar/editar), `lista.jsp` (buscar y eliminar) → `AnuncioServlet` |
| Método **GET** | Consultas: listar, buscar por ciudad, ver detalle y abrir formularios (`doGet`) |
| Método **POST** | Cambios en la base de datos: insertar, actualizar y eliminar (`doPost`) |
| Elementos de **JSP** | Directivas `page` e `include`, scriptlets `<% %>`, expresiones `<%= %>`, declaración `<%! %>`, Expression Language `${...}`, objetos implícitos `request`, `response` y `out`, comentarios `<%-- --%>` |
| Proyecto versionado | Repositorio Git en GitHub |
| Estándares de codificación | Mismos de la evidencia GA7-220501096-AA1-EV02 |

## Funcionamiento (patrón MVC)

```
Navegador ──GET/POST──► AnuncioServlet (controlador)
                             │
                             ├─► AnuncioService (reglas de negocio)
                             │        └─► AnuncioDaoJdbc ──JDBC──► MySQL (tabla servicio)
                             │
                             └─► JSP (vista): lista.jsp, detalle.jsp, formulario.jsp
```

| URL | Método | Acción |
|---|---|---|
| `/anuncios` | GET | Lista todos los anuncios |
| `/anuncios?accion=listar&ciudad=1` | GET | Anuncios activos de una ciudad |
| `/anuncios?accion=ver&id=1` | GET | Detalle de un anuncio |
| `/anuncios?accion=nuevo` | GET | Formulario para publicar |
| `/anuncios?accion=editar&id=1` | GET | Formulario para editar |
| `/anuncios` con `accion=guardar` | POST | Inserta (sin id) o actualiza (con id) |
| `/anuncios` con `accion=eliminar` | POST | Elimina un anuncio |

Después de guardar o eliminar, el servlet redirige con el patrón **Post/Redirect/Get**, para que al
recargar la página no se repita el envío del formulario.

## Estructura

```
dohelp-web/
├── pom.xml
├── database/                       Scripts de la base de datos dohelp
├── docs/capturas/                  Capturas de las pruebas
└── src/main/
    ├── java/co/dohelp/
    │   ├── web/AnuncioServlet.java  Servlet (controlador): doGet y doPost
    │   ├── web/Html.java            Escapa texto para evitar ataques XSS
    │   ├── anuncio/                 Anuncio, AnuncioDao, AnuncioDaoJdbc, AnuncioService
    │   ├── catalogo/                CatalogoDao, Opcion, Subcategoria
    │   ├── config/ConexionBD.java   Conexión JDBC
    │   └── excepcion/DatosException.java
    ├── resources/db.properties.example
    └── webapp/
        ├── index.jsp                Redirige a /anuncios
        ├── css/estilos.css
        └── WEB-INF/
            ├── web.xml
            └── vistas/
                ├── comun/encabezado.jspf, pie.jspf   (se incluyen con <%@ include %>)
                └── anuncios/lista.jsp, detalle.jsp, formulario.jsp
```

## Pruebas realizadas

| # | Prueba | Resultado |
|---|---|---|
| 1 | Abrir `/dohelp/anuncios` (GET) | Lista los 4 anuncios de prueba |
| 2 | Publicar un anuncio con el formulario (POST) | Se guarda en estado *pendiente* y muestra el detalle |
| 3 | Editar el anuncio: precio y estado (POST) | Se actualiza en MySQL y muestra el mensaje de confirmación |
| 4 | Buscar anuncios activos en Soacha (GET) | Muestra solo el anuncio de esa ciudad |
| 5 | Eliminar el anuncio (POST) | Se borra y vuelve a la lista con 4 anuncios |
| 6 | Enviar precio vacío con tipo "fijo" | Muestra el error "Debe indicar un precio mayor que cero" |
| 7 | Consultar un id que no existe | Muestra "No existe un anuncio con el id 99" |
| 8 | Escribir `<script>` en el título | Se muestra como texto, no se ejecuta |

Las capturas están en `docs/capturas/`.

## Cómo ejecutarlo

1. Tener la base de datos `dohelp` creada en MySQL (scripts de la carpeta `database`).
2. Copiar `src/main/resources/db.properties.example` como `db.properties` y escribir la contraseña de MySQL.
3. Opción A – Sin instalar servidor: abrir el proyecto en IntelliJ IDEA y ejecutar el objetivo de Maven
   `jetty:run`. Luego abrir http://localhost:8080/dohelp
4. Opción B – Con Tomcat 10.1: ejecutar `mvn package` y copiar `target/dohelp.war` en la carpeta
   `webapps` de Tomcat. Luego abrir http://localhost:8080/dohelp

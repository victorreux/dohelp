package co.dohelp.web;

import co.dohelp.anuncio.Anuncio;
import co.dohelp.anuncio.AnuncioDaoJdbc;
import co.dohelp.anuncio.AnuncioService;
import co.dohelp.catalogo.CatalogoDao;
import co.dohelp.excepcion.DatosException;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.List;

/**
 * Controlador web del modulo de gestion de anuncios de DOHELP.
 *
 * <p>Peticiones GET (consultar): listar, buscar por ciudad, ver detalle y mostrar formularios.
 * Peticiones POST (modificar datos): guardar (insertar o actualizar) y eliminar.</p>
 */
@WebServlet(name = "AnuncioServlet", urlPatterns = "/anuncios")
public class AnuncioServlet extends HttpServlet {

    private static final String VISTA_LISTA = "/WEB-INF/vistas/anuncios/lista.jsp";
    private static final String VISTA_DETALLE = "/WEB-INF/vistas/anuncios/detalle.jsp";
    private static final String VISTA_FORMULARIO = "/WEB-INF/vistas/anuncios/formulario.jsp";

    private AnuncioService anuncioService;
    private CatalogoDao catalogoDao;

    @Override
    public void init() {
        anuncioService = new AnuncioService(new AnuncioDaoJdbc());
        catalogoDao = new CatalogoDao();
    }

    // ---------- GET: consultas y formularios ----------

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        request.setCharacterEncoding("UTF-8");
        String accion = obtenerParametro(request, "accion", "listar");
        try {
            switch (accion) {
                case "ver" -> mostrarDetalle(request, response);
                case "nuevo" -> mostrarFormulario(request, response, new Anuncio());
                case "editar" -> mostrarFormulario(request, response,
                        anuncioService.consultarAnuncio(leerEntero(request, "id")));
                default -> listarAnuncios(request, response);
            }
        } catch (DatosException e) {
            request.setAttribute("error", e.getMessage());
            listarAnunciosSinFiltro(request, response);
        }
    }

    private void listarAnuncios(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        String ciudad = obtenerParametro(request, "ciudad", "");
        List<Anuncio> anuncios = ciudad.isEmpty()
                ? anuncioService.listarAnuncios()
                : anuncioService.buscarPorCiudad(Integer.parseInt(ciudad));
        request.setAttribute("anuncios", anuncios);
        request.setAttribute("ciudades", catalogoDao.listarCiudades());
        request.setAttribute("ciudadElegida", ciudad);
        request.getRequestDispatcher(VISTA_LISTA).forward(request, response);
    }

    private void listarAnunciosSinFiltro(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        try {
            request.setAttribute("anuncios", anuncioService.listarAnuncios());
            request.setAttribute("ciudades", catalogoDao.listarCiudades());
        } catch (DatosException e) {
            // Si tampoco hay conexion, la pagina muestra solo el mensaje de error
            request.setAttribute("anuncios", List.of());
            request.setAttribute("ciudades", List.of());
        }
        request.setAttribute("ciudadElegida", "");
        request.getRequestDispatcher(VISTA_LISTA).forward(request, response);
    }

    private void mostrarDetalle(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        Anuncio anuncio = anuncioService.consultarAnuncio(leerEntero(request, "id"));
        request.setAttribute("anuncio", anuncio);
        request.getRequestDispatcher(VISTA_DETALLE).forward(request, response);
    }

    private void mostrarFormulario(HttpServletRequest request, HttpServletResponse response,
                                   Anuncio anuncio) throws ServletException, IOException {
        request.setAttribute("anuncio", anuncio);
        request.setAttribute("trabajadores", catalogoDao.listarTrabajadores());
        request.setAttribute("subcategorias", catalogoDao.listarSubcategoriasAgrupadas());
        request.setAttribute("ciudades", catalogoDao.listarCiudades());
        request.getRequestDispatcher(VISTA_FORMULARIO).forward(request, response);
    }

    // ---------- POST: insertar, actualizar y eliminar ----------

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        request.setCharacterEncoding("UTF-8");
        String accion = obtenerParametro(request, "accion", "");
        switch (accion) {
            case "guardar" -> guardarAnuncio(request, response);
            case "eliminar" -> eliminarAnuncio(request, response);
            default -> response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Accion no valida");
        }
    }

    private void guardarAnuncio(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        Anuncio anuncio = new Anuncio();
        try {
            leerAnuncioDelFormulario(request, anuncio);
            String resultado;
            if (anuncio.getIdAnuncio() == null) {
                anuncioService.publicarAnuncio(anuncio);
                resultado = "creado";
            } else {
                anuncioService.actualizarAnuncio(anuncio);
                resultado = "actualizado";
            }
            // Patron Post/Redirect/Get: evita que se repita el envio al recargar la pagina
            response.sendRedirect(request.getContextPath() + "/anuncios?accion=ver&id="
                    + anuncio.getIdAnuncio() + "&ok=" + resultado);
        } catch (DatosException e) {
            request.setAttribute("error", e.getMessage());
            mostrarFormulario(request, response, anuncio);
        }
    }

    private void eliminarAnuncio(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        try {
            anuncioService.eliminarAnuncio(leerEntero(request, "id"));
            response.sendRedirect(request.getContextPath() + "/anuncios?ok=eliminado");
        } catch (DatosException e) {
            request.setAttribute("error", e.getMessage());
            listarAnunciosSinFiltro(request, response);
        }
    }

    // ---------- Metodos de apoyo ----------

    /** Copia en el anuncio los datos enviados por el formulario. */
    private void leerAnuncioDelFormulario(HttpServletRequest request, Anuncio anuncio) {
        String id = obtenerParametro(request, "idAnuncio", "");
        anuncio.setIdAnuncio(id.isEmpty() ? null : Integer.parseInt(id));
        anuncio.setTitulo(obtenerParametro(request, "titulo", ""));
        anuncio.setDescripcion(obtenerParametro(request, "descripcion", ""));
        anuncio.setTipoPrecio(obtenerParametro(request, "tipoPrecio", "cotizacion"));
        anuncio.setModalidad(obtenerParametro(request, "modalidad", "presencial"));
        anuncio.setEstado(obtenerParametro(request, "estado", "pendiente"));
        anuncio.setIdUsuario(leerEnteroOpcional(request, "idUsuario"));
        anuncio.setIdCiudad(leerEnteroOpcional(request, "idCiudad"));

        Integer idSubcategoria = leerEnteroOpcional(request, "idSubcategoria");
        anuncio.setIdSubcategoria(idSubcategoria);
        if (idSubcategoria != null) {
            anuncio.setIdCategoria(catalogoDao.obtenerIdCategoria(idSubcategoria));
        }

        String precio = obtenerParametro(request, "precio", "");
        try {
            anuncio.setPrecio(precio.isEmpty() ? null : new BigDecimal(precio));
        } catch (NumberFormatException e) {
            throw new DatosException("El precio debe ser un numero, por ejemplo 50000");
        }
    }

    private String obtenerParametro(HttpServletRequest request, String nombre, String valorPorDefecto) {
        String valor = request.getParameter(nombre);
        return valor == null || valor.isBlank() ? valorPorDefecto : valor.trim();
    }

    private int leerEntero(HttpServletRequest request, String nombre) {
        Integer valor = leerEnteroOpcional(request, nombre);
        if (valor == null) {
            throw new DatosException("Falta el dato: " + nombre);
        }
        return valor;
    }

    private Integer leerEnteroOpcional(HttpServletRequest request, String nombre) {
        String valor = obtenerParametro(request, nombre, "");
        try {
            return valor.isEmpty() ? null : Integer.valueOf(valor);
        } catch (NumberFormatException e) {
            throw new DatosException("El dato " + nombre + " debe ser un numero");
        }
    }
}

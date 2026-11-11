<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ page import="java.util.List" %>
<%@ page import="co.dohelp.anuncio.Anuncio" %>
<%@ page import="co.dohelp.catalogo.Opcion" %>
<%@ page import="co.dohelp.catalogo.Subcategoria" %>
<%@ page import="co.dohelp.web.Html" %>
<%@ include file="/WEB-INF/vistas/comun/encabezado.jspf" %>
<%
    Anuncio anuncio = (Anuncio) request.getAttribute("anuncio");
    List<Opcion> trabajadores = (List<Opcion>) request.getAttribute("trabajadores");
    List<Subcategoria> subcategorias = (List<Subcategoria>) request.getAttribute("subcategorias");
    List<Opcion> ciudades = (List<Opcion>) request.getAttribute("ciudades");
    boolean esNuevo = anuncio.getIdAnuncio() == null;
    String precio = anuncio.getPrecio() == null ? "" : anuncio.getPrecio().toPlainString();
%>

<a class="volver" href="${pageContext.request.contextPath}/anuncios">← Volver a la lista</a>

<section class="tarjeta">
    <h1><%= esNuevo ? "Publicar un anuncio nuevo" : "Editar anuncio #" + anuncio.getIdAnuncio() %></h1>
    <p class="ayuda">Los campos con * son obligatorios.</p>

    <%-- Formulario con metodo POST: envia datos que se guardan en la base de datos --%>
    <form class="formulario" method="post" action="${pageContext.request.contextPath}/anuncios">
        <input type="hidden" name="accion" value="guardar">
        <input type="hidden" name="idAnuncio" value="<%= esNuevo ? "" : anuncio.getIdAnuncio() %>">

        <% if (esNuevo) { %>
        <label for="idUsuario">Trabajador que publica *</label>
        <select id="idUsuario" name="idUsuario" required>
            <option value="">Seleccione…</option>
            <% for (Opcion trabajador : trabajadores) { %>
                <option value="<%= trabajador.id() %>" <%= Html.seleccionar(trabajador.id(), anuncio.getIdUsuario()) %>>
                    <%= Html.escapar(trabajador.nombre()) %>
                </option>
            <% } %>
        </select>
        <% } else { %>
        <input type="hidden" name="idUsuario" value="<%= anuncio.getIdUsuario() %>">
        <p class="dato-fijo">Anunciante: <b><%= Html.escapar(anuncio.getNombreAnunciante()) %></b></p>
        <% } %>

        <label for="titulo">Título *</label>
        <input id="titulo" name="titulo" type="text" maxlength="<%= Anuncio.MAX_LONGITUD_TITULO %>" required
               value="<%= Html.escapar(anuncio.getTitulo()) %>"
               placeholder="Ej.: Clases de guitarra para principiantes">

        <label for="descripcion">Descripción *</label>
        <textarea id="descripcion" name="descripcion" rows="4" required
                  placeholder="Cuenta qué incluye tu servicio"><%= Html.escapar(anuncio.getDescripcion()) %></textarea>

        <div class="fila">
            <div>
                <label for="idSubcategoria">Categoría *</label>
                <select id="idSubcategoria" name="idSubcategoria" required>
                    <option value="">Seleccione…</option>
                    <%
                        // Agrupa las subcategorias por categoria con <optgroup>
                        Integer categoriaActual = null;
                        for (Subcategoria subcategoria : subcategorias) {
                            if (!Integer.valueOf(subcategoria.idCategoria()).equals(categoriaActual)) {
                                if (categoriaActual != null) {
                                    out.println("</optgroup>");
                                }
                                categoriaActual = subcategoria.idCategoria();
                                out.println("<optgroup label=\"" + Html.escapar(subcategoria.nombreCategoria()) + "\">");
                            }
                    %>
                        <option value="<%= subcategoria.id() %>" <%= Html.seleccionar(subcategoria.id(), anuncio.getIdSubcategoria()) %>>
                            <%= Html.escapar(subcategoria.nombre()) %>
                        </option>
                    <%
                        }
                        if (categoriaActual != null) {
                            out.println("</optgroup>");
                        }
                    %>
                </select>
            </div>
            <div>
                <label for="idCiudad">Ciudad *</label>
                <select id="idCiudad" name="idCiudad" required>
                    <option value="">Seleccione…</option>
                    <% for (Opcion ciudad : ciudades) { %>
                        <option value="<%= ciudad.id() %>" <%= Html.seleccionar(ciudad.id(), anuncio.getIdCiudad()) %>>
                            <%= Html.escapar(ciudad.nombre()) %>
                        </option>
                    <% } %>
                </select>
            </div>
        </div>

        <div class="fila">
            <div>
                <label for="modalidad">Modalidad *</label>
                <select id="modalidad" name="modalidad">
                    <option value="presencial" <%= Html.seleccionar("presencial", anuncio.getModalidad()) %>>Presencial</option>
                    <option value="domicilio" <%= Html.seleccionar("domicilio", anuncio.getModalidad()) %>>A domicilio</option>
                    <option value="virtual" <%= Html.seleccionar("virtual", anuncio.getModalidad()) %>>Virtual</option>
                </select>
            </div>
            <div>
                <label for="tipoPrecio">Tipo de precio *</label>
                <select id="tipoPrecio" name="tipoPrecio">
                    <option value="cotizacion" <%= Html.seleccionar("cotizacion", anuncio.getTipoPrecio()) %>>A cotizar</option>
                    <option value="desde" <%= Html.seleccionar("desde", anuncio.getTipoPrecio()) %>>Desde</option>
                    <option value="fijo" <%= Html.seleccionar("fijo", anuncio.getTipoPrecio()) %>>Precio fijo</option>
                </select>
            </div>
            <div>
                <label for="precio">Precio en pesos</label>
                <input id="precio" name="precio" type="number" min="0" step="1000"
                       value="<%= precio %>" placeholder="Ej.: 50000">
            </div>
        </div>

        <% if (!esNuevo) { %>
        <label for="estado">Estado</label>
        <select id="estado" name="estado">
            <option value="pendiente" <%= Html.seleccionar("pendiente", anuncio.getEstado()) %>>Pendiente</option>
            <option value="activo" <%= Html.seleccionar("activo", anuncio.getEstado()) %>>Activo</option>
            <option value="pausado" <%= Html.seleccionar("pausado", anuncio.getEstado()) %>>Pausado</option>
            <option value="rechazado" <%= Html.seleccionar("rechazado", anuncio.getEstado()) %>>Rechazado</option>
        </select>
        <% } %>

        <div class="botones">
            <button type="submit" class="boton boton-principal"><%= esNuevo ? "Publicar anuncio" : "Guardar cambios" %></button>
            <a class="boton boton-secundario" href="${pageContext.request.contextPath}/anuncios">Cancelar</a>
        </div>
    </form>
</section>

<%@ include file="/WEB-INF/vistas/comun/pie.jspf" %>

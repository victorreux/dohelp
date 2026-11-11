<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ page import="java.util.List" %>
<%@ page import="co.dohelp.anuncio.Anuncio" %>
<%@ page import="co.dohelp.catalogo.Opcion" %>
<%@ page import="co.dohelp.web.Html" %>
<%@ include file="/WEB-INF/vistas/comun/encabezado.jspf" %>
<%!
    // Declaracion JSP: metodo disponible en toda la pagina
    private String claseEstado(String estado) {
        return "etiqueta etiqueta-" + estado;
    }
%>
<%
    List<Anuncio> anuncios = (List<Anuncio>) request.getAttribute("anuncios");
    List<Opcion> ciudades = (List<Opcion>) request.getAttribute("ciudades");
    String ciudadElegida = (String) request.getAttribute("ciudadElegida");
%>

<section class="titulo-seccion">
    <h1>Anuncios de servicios</h1>
    <p>Consulta, publica, edita o elimina los anuncios de DOHELP.</p>
</section>

<%-- Formulario con metodo GET: solo consulta datos, por eso los filtros quedan en la URL --%>
<form class="filtros" method="get" action="${pageContext.request.contextPath}/anuncios">
    <input type="hidden" name="accion" value="listar">
    <label for="ciudad">Ver anuncios activos en:</label>
    <select id="ciudad" name="ciudad">
        <option value="">Todas las ciudades (todos los estados)</option>
        <% for (Opcion ciudad : ciudades) { %>
            <option value="<%= ciudad.id() %>" <%= Html.seleccionar(ciudad.id(), ciudadElegida) %>>
                <%= Html.escapar(ciudad.nombre()) %>
            </option>
        <% } %>
    </select>
    <button type="submit" class="boton boton-secundario">Buscar</button>
</form>

<p class="contador"><%= anuncios.size() %> anuncio(s) encontrado(s)</p>

<% if (anuncios.isEmpty()) { %>
    <p class="vacio">No hay anuncios para mostrar.</p>
<% } else { %>
<div class="tabla-contenedor">
<table class="tabla">
    <thead>
    <tr>
        <th>#</th>
        <th>Título</th>
        <th>Categoría</th>
        <th>Ciudad</th>
        <th>Precio</th>
        <th>Estado</th>
        <th>Acciones</th>
    </tr>
    </thead>
    <tbody>
    <% for (Anuncio anuncio : anuncios) { %>
        <tr>
            <td><%= anuncio.getIdAnuncio() %></td>
            <td>
                <a href="${pageContext.request.contextPath}/anuncios?accion=ver&id=<%= anuncio.getIdAnuncio() %>">
                    <%= Html.escapar(anuncio.getTitulo()) %>
                </a>
            </td>
            <td><%= Html.escapar(anuncio.getNombreCategoria()) %></td>
            <td><%= Html.escapar(anuncio.getNombreCiudad()) %></td>
            <td><%= anuncio.obtenerPrecioTexto() %></td>
            <td><span class="<%= claseEstado(anuncio.getEstado()) %>"><%= anuncio.getEstado() %></span></td>
            <td>
                <div class="acciones">
                <a class="boton boton-pequeno" href="${pageContext.request.contextPath}/anuncios?accion=editar&id=<%= anuncio.getIdAnuncio() %>">Editar</a>
                <%-- Eliminar usa POST porque modifica datos --%>
                <form method="post" action="${pageContext.request.contextPath}/anuncios"
                      onsubmit="return confirm('¿Eliminar este anuncio?');">
                    <input type="hidden" name="accion" value="eliminar">
                    <input type="hidden" name="id" value="<%= anuncio.getIdAnuncio() %>">
                    <button type="submit" class="boton boton-pequeno boton-peligro">Eliminar</button>
                </form>
                </div>
            </td>
        </tr>
    <% } %>
    </tbody>
</table>
</div>
<% } %>

<%@ include file="/WEB-INF/vistas/comun/pie.jspf" %>

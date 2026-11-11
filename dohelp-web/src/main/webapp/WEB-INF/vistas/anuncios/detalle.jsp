<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ page import="co.dohelp.anuncio.Anuncio" %>
<%@ page import="co.dohelp.web.Html" %>
<%@ include file="/WEB-INF/vistas/comun/encabezado.jspf" %>
<%
    Anuncio anuncio = (Anuncio) request.getAttribute("anuncio");
%>

<a class="volver" href="${pageContext.request.contextPath}/anuncios">← Volver a la lista</a>

<article class="tarjeta detalle">
    <div class="detalle-encabezado">
        <span class="etiqueta etiqueta-<%= anuncio.getEstado() %>"><%= anuncio.getEstado() %></span>
        <%-- Expresion EL: lee el atributo "anuncio" que envio el servlet --%>
        <span class="id-anuncio">Anuncio #${anuncio.idAnuncio}</span>
    </div>
    <h1><%= Html.escapar(anuncio.getTitulo()) %></h1>
    <p class="descripcion"><%= Html.escapar(anuncio.getDescripcion()) %></p>

    <dl class="datos">
        <div><dt>Anunciante</dt><dd><%= Html.escapar(anuncio.getNombreAnunciante()) %></dd></div>
        <div><dt>Categoría</dt><dd><%= Html.escapar(anuncio.getNombreCategoria()) %></dd></div>
        <div><dt>Ciudad</dt><dd><%= Html.escapar(anuncio.getNombreCiudad()) %></dd></div>
        <div><dt>Modalidad</dt><dd>${anuncio.modalidad}</dd></div>
        <div><dt>Precio</dt><dd><%= anuncio.obtenerPrecioTexto() %></dd></div>
        <div><dt>Publicado</dt><dd>${anuncio.fechaPublicacion}</dd></div>
    </dl>

    <div class="botones">
        <a class="boton boton-principal" href="${pageContext.request.contextPath}/anuncios?accion=editar&id=${anuncio.idAnuncio}">Editar anuncio</a>
        <form method="post" action="${pageContext.request.contextPath}/anuncios"
              onsubmit="return confirm('¿Eliminar este anuncio?');">
            <input type="hidden" name="accion" value="eliminar">
            <input type="hidden" name="id" value="${anuncio.idAnuncio}">
            <button type="submit" class="boton boton-peligro">Eliminar</button>
        </form>
    </div>
</article>

<%@ include file="/WEB-INF/vistas/comun/pie.jspf" %>

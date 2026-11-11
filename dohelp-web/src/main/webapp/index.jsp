<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%-- Pagina de inicio: redirige al listado de anuncios que maneja el servlet --%>
<%
    response.sendRedirect(request.getContextPath() + "/anuncios");
%>

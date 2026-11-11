package co.dohelp.catalogo;

/**
 * Subcategoria con el nombre de su categoria, para agrupar las opciones
 * en las listas desplegables del formulario.
 *
 * @param id              identificador de la subcategoria
 * @param nombre          nombre de la subcategoria
 * @param idCategoria     identificador de la categoria a la que pertenece
 * @param nombreCategoria nombre de la categoria a la que pertenece
 */
public record Subcategoria(int id, String nombre, int idCategoria, String nombreCategoria) {
}

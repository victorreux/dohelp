package co.dohelp.catalogo;

/**
 * Elemento de un catalogo (ciudad, categoria, trabajador) para mostrar en los menus.
 *
 * @param id     identificador en la base de datos
 * @param nombre texto que se muestra al usuario
 */
public record Opcion(int id, String nombre) {

    @Override
    public String toString() {
        return id + ". " + nombre;
    }
}

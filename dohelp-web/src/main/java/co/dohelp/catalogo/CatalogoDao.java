package co.dohelp.catalogo;

import co.dohelp.config.ConexionBD;
import co.dohelp.excepcion.DatosException;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * Consulta los catalogos que se necesitan para registrar un anuncio:
 * ciudades, categorias, subcategorias y trabajadores.
 */
public class CatalogoDao {

    private static final String SQL_CIUDADES =
            "SELECT id_ciudad, nombre_ciudad FROM ciudad ORDER BY nombre_ciudad";

    private static final String SQL_CATEGORIAS =
            "SELECT id_categoria, nombre_categoria FROM categoria ORDER BY id_categoria";

    private static final String SQL_SUBCATEGORIAS =
            "SELECT id_subcategoria, nombre_subcategoria FROM subcategoria "
            + "WHERE id_categoria = ? ORDER BY nombre_subcategoria";

    private static final String SQL_SUBCATEGORIAS_AGRUPADAS =
            "SELECT s.id_subcategoria, s.nombre_subcategoria, c.id_categoria, c.nombre_categoria "
            + "FROM subcategoria AS s "
            + "INNER JOIN categoria AS c ON c.id_categoria = s.id_categoria "
            + "ORDER BY c.id_categoria, s.nombre_subcategoria";

    private static final String SQL_CATEGORIA_DE_SUBCATEGORIA =
            "SELECT id_categoria FROM subcategoria WHERE id_subcategoria = ?";

    private static final String SQL_TRABAJADORES =
            "SELECT u.id_usuario, CONCAT(u.nombre, ' ', u.apellido) "
            + "FROM usuario AS u "
            + "INNER JOIN usuario_rol AS ur ON ur.id_usuario = u.id_usuario "
            + "INNER JOIN rol AS r ON r.id_rol = ur.id_rol "
            + "WHERE r.nombre_rol = 'trabajador' ORDER BY u.nombre";

    public List<Opcion> listarCiudades() {
        return consultar(SQL_CIUDADES, null);
    }

    public List<Opcion> listarCategorias() {
        return consultar(SQL_CATEGORIAS, null);
    }

    public List<Opcion> listarSubcategorias(int idCategoria) {
        return consultar(SQL_SUBCATEGORIAS, idCategoria);
    }

    /** Lista todas las subcategorias con su categoria, ordenadas por categoria. */
    public List<Subcategoria> listarSubcategoriasAgrupadas() {
        List<Subcategoria> subcategorias = new ArrayList<>();
        try (Connection conexion = ConexionBD.obtenerConexion();
             PreparedStatement consulta = conexion.prepareStatement(SQL_SUBCATEGORIAS_AGRUPADAS);
             ResultSet resultado = consulta.executeQuery()) {

            while (resultado.next()) {
                subcategorias.add(new Subcategoria(resultado.getInt(1), resultado.getString(2),
                        resultado.getInt(3), resultado.getString(4)));
            }
        } catch (SQLException e) {
            throw new DatosException("No fue posible consultar las subcategorias", e);
        }
        return subcategorias;
    }

    /** Devuelve el id de la categoria a la que pertenece una subcategoria. */
    public int obtenerIdCategoria(int idSubcategoria) {
        try (Connection conexion = ConexionBD.obtenerConexion();
             PreparedStatement consulta = conexion.prepareStatement(SQL_CATEGORIA_DE_SUBCATEGORIA)) {

            consulta.setInt(1, idSubcategoria);
            try (ResultSet resultado = consulta.executeQuery()) {
                if (resultado.next()) {
                    return resultado.getInt(1);
                }
                throw new DatosException("La subcategoria elegida no existe");
            }
        } catch (SQLException e) {
            throw new DatosException("No fue posible consultar la subcategoria", e);
        }
    }

    public List<Opcion> listarTrabajadores() {
        return consultar(SQL_TRABAJADORES, null);
    }

    private List<Opcion> consultar(String sql, Integer parametro) {
        List<Opcion> opciones = new ArrayList<>();
        try (Connection conexion = ConexionBD.obtenerConexion();
             PreparedStatement consulta = conexion.prepareStatement(sql)) {

            if (parametro != null) {
                consulta.setInt(1, parametro);
            }
            try (ResultSet resultado = consulta.executeQuery()) {
                while (resultado.next()) {
                    opciones.add(new Opcion(resultado.getInt(1), resultado.getString(2)));
                }
            }
        } catch (SQLException e) {
            throw new DatosException("No fue posible consultar el catalogo", e);
        }
        return opciones;
    }
}

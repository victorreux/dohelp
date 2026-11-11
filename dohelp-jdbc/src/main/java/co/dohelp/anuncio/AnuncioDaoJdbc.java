package co.dohelp.anuncio;

import co.dohelp.config.ConexionBD;
import co.dohelp.excepcion.DatosException;

import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Types;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * Implementacion del CRUD de anuncios con JDBC sobre la tabla servicio.
 * Todas las consultas usan PreparedStatement para evitar la inyeccion SQL.
 */
public class AnuncioDaoJdbc implements AnuncioDao {

    private static final String SQL_SELECT_BASE =
            "SELECT s.id_servicio, s.titulo, s.descripcion, s.tipo_precio, s.valor, "
            + "s.modalidad, s.fecha_publicacion, s.estado, s.id_usuario, s.id_categoria, "
            + "s.id_subcategoria, s.id_ciudad, "
            + "CONCAT(u.nombre, ' ', u.apellido) AS nombre_anunciante, "
            + "c.nombre_categoria, ci.nombre_ciudad "
            + "FROM servicio AS s "
            + "INNER JOIN usuario AS u ON u.id_usuario = s.id_usuario "
            + "INNER JOIN categoria AS c ON c.id_categoria = s.id_categoria "
            + "INNER JOIN ciudad AS ci ON ci.id_ciudad = s.id_ciudad ";

    private static final String SQL_INSERTAR =
            "INSERT INTO servicio (titulo, descripcion, tipo_precio, valor, modalidad, estado, "
            + "id_usuario, id_categoria, id_subcategoria, id_ciudad) "
            + "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";

    private static final String SQL_BUSCAR_POR_ID =
            SQL_SELECT_BASE + "WHERE s.id_servicio = ?";

    private static final String SQL_LISTAR_TODOS =
            SQL_SELECT_BASE + "ORDER BY s.fecha_publicacion DESC, s.id_servicio DESC";

    private static final String SQL_ACTIVOS_POR_CIUDAD =
            SQL_SELECT_BASE + "WHERE s.estado = 'activo' AND s.id_ciudad = ? "
            + "ORDER BY s.fecha_publicacion DESC";

    private static final String SQL_ACTUALIZAR =
            "UPDATE servicio SET titulo = ?, descripcion = ?, tipo_precio = ?, valor = ?, "
            + "modalidad = ?, estado = ?, id_categoria = ?, id_subcategoria = ?, id_ciudad = ? "
            + "WHERE id_servicio = ?";

    private static final String SQL_ELIMINAR =
            "DELETE FROM servicio WHERE id_servicio = ?";

    @Override
    public int insertar(Anuncio anuncio) {
        try (Connection conexion = ConexionBD.obtenerConexion();
             PreparedStatement consulta = conexion.prepareStatement(
                     SQL_INSERTAR, Statement.RETURN_GENERATED_KEYS)) {

            consulta.setString(1, anuncio.getTitulo());
            consulta.setString(2, anuncio.getDescripcion());
            consulta.setString(3, anuncio.getTipoPrecio());
            consulta.setBigDecimal(4, anuncio.getPrecio());
            consulta.setString(5, anuncio.getModalidad());
            consulta.setString(6, anuncio.getEstado());
            consulta.setInt(7, anuncio.getIdUsuario());
            consulta.setInt(8, anuncio.getIdCategoria());
            asignarEnteroOpcional(consulta, 9, anuncio.getIdSubcategoria());
            consulta.setInt(10, anuncio.getIdCiudad());
            consulta.executeUpdate();

            try (ResultSet llaves = consulta.getGeneratedKeys()) {
                llaves.next();
                int idGenerado = llaves.getInt(1);
                anuncio.setIdAnuncio(idGenerado);
                return idGenerado;
            }
        } catch (SQLException e) {
            throw new DatosException("No fue posible guardar el anuncio: " + e.getMessage(), e);
        }
    }

    @Override
    public Optional<Anuncio> buscarPorId(int idAnuncio) {
        try (Connection conexion = ConexionBD.obtenerConexion();
             PreparedStatement consulta = conexion.prepareStatement(SQL_BUSCAR_POR_ID)) {

            consulta.setInt(1, idAnuncio);
            try (ResultSet resultado = consulta.executeQuery()) {
                if (resultado.next()) {
                    return Optional.of(convertirFila(resultado));
                }
                return Optional.empty();
            }
        } catch (SQLException e) {
            throw new DatosException("No fue posible consultar el anuncio", e);
        }
    }

    @Override
    public List<Anuncio> listarTodos() {
        try (Connection conexion = ConexionBD.obtenerConexion();
             PreparedStatement consulta = conexion.prepareStatement(SQL_LISTAR_TODOS);
             ResultSet resultado = consulta.executeQuery()) {

            return convertirFilas(resultado);
        } catch (SQLException e) {
            throw new DatosException("No fue posible listar los anuncios", e);
        }
    }

    @Override
    public List<Anuncio> buscarActivosPorCiudad(int idCiudad) {
        try (Connection conexion = ConexionBD.obtenerConexion();
             PreparedStatement consulta = conexion.prepareStatement(SQL_ACTIVOS_POR_CIUDAD)) {

            consulta.setInt(1, idCiudad);
            try (ResultSet resultado = consulta.executeQuery()) {
                return convertirFilas(resultado);
            }
        } catch (SQLException e) {
            throw new DatosException("No fue posible buscar los anuncios de la ciudad", e);
        }
    }

    @Override
    public boolean actualizar(Anuncio anuncio) {
        try (Connection conexion = ConexionBD.obtenerConexion();
             PreparedStatement consulta = conexion.prepareStatement(SQL_ACTUALIZAR)) {

            consulta.setString(1, anuncio.getTitulo());
            consulta.setString(2, anuncio.getDescripcion());
            consulta.setString(3, anuncio.getTipoPrecio());
            consulta.setBigDecimal(4, anuncio.getPrecio());
            consulta.setString(5, anuncio.getModalidad());
            consulta.setString(6, anuncio.getEstado());
            consulta.setInt(7, anuncio.getIdCategoria());
            asignarEnteroOpcional(consulta, 8, anuncio.getIdSubcategoria());
            consulta.setInt(9, anuncio.getIdCiudad());
            consulta.setInt(10, anuncio.getIdAnuncio());
            return consulta.executeUpdate() > 0;
        } catch (SQLException e) {
            throw new DatosException("No fue posible actualizar el anuncio: " + e.getMessage(), e);
        }
    }

    @Override
    public boolean eliminar(int idAnuncio) {
        try (Connection conexion = ConexionBD.obtenerConexion();
             PreparedStatement consulta = conexion.prepareStatement(SQL_ELIMINAR)) {

            consulta.setInt(1, idAnuncio);
            return consulta.executeUpdate() > 0;
        } catch (SQLException e) {
            throw new DatosException("No fue posible eliminar el anuncio", e);
        }
    }

    // Metodos privados de apoyo

    private List<Anuncio> convertirFilas(ResultSet resultado) throws SQLException {
        List<Anuncio> anuncios = new ArrayList<>();
        while (resultado.next()) {
            anuncios.add(convertirFila(resultado));
        }
        return anuncios;
    }

    private Anuncio convertirFila(ResultSet resultado) throws SQLException {
        Anuncio anuncio = new Anuncio();
        anuncio.setIdAnuncio(resultado.getInt("id_servicio"));
        anuncio.setTitulo(resultado.getString("titulo"));
        anuncio.setDescripcion(resultado.getString("descripcion"));
        anuncio.setTipoPrecio(resultado.getString("tipo_precio"));
        anuncio.setPrecio(resultado.getBigDecimal("valor"));
        anuncio.setModalidad(resultado.getString("modalidad"));
        Date fecha = resultado.getDate("fecha_publicacion");
        anuncio.setFechaPublicacion(fecha != null ? fecha.toLocalDate() : null);
        anuncio.setEstado(resultado.getString("estado"));
        anuncio.setIdUsuario(resultado.getInt("id_usuario"));
        anuncio.setIdCategoria(resultado.getInt("id_categoria"));
        int idSubcategoria = resultado.getInt("id_subcategoria");
        anuncio.setIdSubcategoria(resultado.wasNull() ? null : idSubcategoria);
        anuncio.setIdCiudad(resultado.getInt("id_ciudad"));
        anuncio.setNombreAnunciante(resultado.getString("nombre_anunciante"));
        anuncio.setNombreCategoria(resultado.getString("nombre_categoria"));
        anuncio.setNombreCiudad(resultado.getString("nombre_ciudad"));
        return anuncio;
    }

    private void asignarEnteroOpcional(PreparedStatement consulta, int posicion, Integer valor)
            throws SQLException {
        if (valor == null) {
            consulta.setNull(posicion, Types.INTEGER);
        } else {
            consulta.setInt(posicion, valor);
        }
    }
}

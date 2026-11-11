package co.dohelp.anuncio;

import java.util.List;
import java.util.Optional;

/**
 * Operaciones de acceso a datos de los anuncios (CRUD).
 */
public interface AnuncioDao {

    /** Inserta un anuncio nuevo y devuelve el id generado. */
    int insertar(Anuncio anuncio);

    /** Busca un anuncio por su id. */
    Optional<Anuncio> buscarPorId(int idAnuncio);

    /** Lista todos los anuncios, del mas reciente al mas antiguo. */
    List<Anuncio> listarTodos();

    /** Lista los anuncios activos de una ciudad. */
    List<Anuncio> buscarActivosPorCiudad(int idCiudad);

    /** Actualiza los datos de un anuncio. Devuelve true si se modifico. */
    boolean actualizar(Anuncio anuncio);

    /** Elimina un anuncio. Devuelve true si se elimino. */
    boolean eliminar(int idAnuncio);
}

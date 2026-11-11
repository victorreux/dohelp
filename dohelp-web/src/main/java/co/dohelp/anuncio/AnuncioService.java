package co.dohelp.anuncio;

import co.dohelp.excepcion.DatosException;

import java.math.BigDecimal;
import java.util.List;
import java.util.Set;

/**
 * Reglas de negocio del modulo de anuncios.
 * Valida los datos antes de enviarlos a la base de datos.
 */
public class AnuncioService {

    public static final Set<String> TIPOS_PRECIO = Set.of("fijo", "desde", "cotizacion");
    public static final Set<String> MODALIDADES = Set.of("presencial", "domicilio", "virtual");
    public static final Set<String> ESTADOS = Set.of("pendiente", "activo", "pausado", "rechazado");

    private final AnuncioDao anuncioDao;

    public AnuncioService(AnuncioDao anuncioDao) {
        this.anuncioDao = anuncioDao;
    }

    /**
     * Publica un anuncio nuevo. Todo anuncio nuevo queda en estado pendiente
     * hasta que un administrador lo revise.
     *
     * @param anuncio datos del anuncio
     * @return id asignado por la base de datos
     */
    public int publicarAnuncio(Anuncio anuncio) {
        anuncio.setEstado("pendiente");
        validarAnuncio(anuncio);
        return anuncioDao.insertar(anuncio);
    }

    public Anuncio consultarAnuncio(int idAnuncio) {
        return anuncioDao.buscarPorId(idAnuncio)
                .orElseThrow(() -> new DatosException("No existe un anuncio con el id " + idAnuncio));
    }

    public List<Anuncio> listarAnuncios() {
        return anuncioDao.listarTodos();
    }

    public List<Anuncio> buscarPorCiudad(int idCiudad) {
        return anuncioDao.buscarActivosPorCiudad(idCiudad);
    }

    public void actualizarAnuncio(Anuncio anuncio) {
        validarAnuncio(anuncio);
        if (!ESTADOS.contains(anuncio.getEstado())) {
            throw new DatosException("Estado no valido: " + anuncio.getEstado());
        }
        if (!anuncioDao.actualizar(anuncio)) {
            throw new DatosException("No existe un anuncio con el id " + anuncio.getIdAnuncio());
        }
    }

    public void eliminarAnuncio(int idAnuncio) {
        if (!anuncioDao.eliminar(idAnuncio)) {
            throw new DatosException("No existe un anuncio con el id " + idAnuncio);
        }
    }

    private void validarAnuncio(Anuncio anuncio) {
        if (estaVacio(anuncio.getTitulo())) {
            throw new DatosException("El titulo es obligatorio");
        }
        if (anuncio.getTitulo().length() > Anuncio.MAX_LONGITUD_TITULO) {
            throw new DatosException("El titulo no puede superar "
                    + Anuncio.MAX_LONGITUD_TITULO + " caracteres");
        }
        if (estaVacio(anuncio.getDescripcion())) {
            throw new DatosException("La descripcion es obligatoria");
        }
        if (anuncio.getIdUsuario() == null) {
            throw new DatosException("Debe elegir el trabajador que publica el anuncio");
        }
        if (anuncio.getIdCategoria() == null) {
            throw new DatosException("Debe elegir una categoria");
        }
        if (anuncio.getIdCiudad() == null) {
            throw new DatosException("Debe elegir una ciudad");
        }
        if (!TIPOS_PRECIO.contains(anuncio.getTipoPrecio())) {
            throw new DatosException("Tipo de precio no valido: " + anuncio.getTipoPrecio());
        }
        if (!MODALIDADES.contains(anuncio.getModalidad())) {
            throw new DatosException("Modalidad no valida: " + anuncio.getModalidad());
        }
        validarPrecio(anuncio);
    }

    private void validarPrecio(Anuncio anuncio) {
        BigDecimal precio = anuncio.getPrecio();
        if ("cotizacion".equals(anuncio.getTipoPrecio())) {
            anuncio.setPrecio(null);
            return;
        }
        if (precio == null || precio.compareTo(BigDecimal.ZERO) <= 0) {
            throw new DatosException("Debe indicar un precio mayor que cero");
        }
    }

    private boolean estaVacio(String texto) {
        return texto == null || texto.isBlank();
    }
}

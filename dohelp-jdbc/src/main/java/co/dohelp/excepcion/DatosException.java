package co.dohelp.excepcion;

/**
 * Error al acceder a la base de datos o al validar los datos de un anuncio.
 * Lleva un mensaje entendible para mostrar al usuario.
 */
public class DatosException extends RuntimeException {

    public DatosException(String mensaje) {
        super(mensaje);
    }

    public DatosException(String mensaje, Throwable causa) {
        super(mensaje, causa);
    }
}

package co.dohelp.config;

import co.dohelp.excepcion.DatosException;

import java.io.IOException;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;

/**
 * Crea las conexiones JDBC con la base de datos dohelp.
 * Los datos de conexion se leen del archivo db.properties.
 */
public final class ConexionBD {

    private static final String ARCHIVO_CONFIGURACION = "db.properties";
    private static final String DRIVER_POR_DEFECTO = "com.mysql.cj.jdbc.Driver";

    private static String url;
    private static String usuario;
    private static String contrasena;

    private ConexionBD() {
        // Clase de utilidad: no se crean objetos de ella
    }

    /**
     * Abre una nueva conexion con la base de datos.
     * Quien la usa debe cerrarla, preferiblemente con try-with-resources.
     *
     * @return conexion abierta con MySQL
     * @throws DatosException si no es posible conectarse
     */
    public static Connection obtenerConexion() {
        if (url == null) {
            cargarConfiguracion();
        }
        try {
            return DriverManager.getConnection(url, usuario, contrasena);
        } catch (SQLException e) {
            throw new DatosException(
                    "No fue posible conectarse a MySQL. Revise que el servidor este encendido "
                    + "y que el usuario y la contrasena de db.properties sean correctos. "
                    + "Detalle: " + e.getMessage(), e);
        }
    }

    private static void cargarConfiguracion() {
        Properties propiedades = new Properties();
        try (InputStream entrada = ConexionBD.class.getClassLoader()
                .getResourceAsStream(ARCHIVO_CONFIGURACION)) {
            if (entrada == null) {
                throw new DatosException("No se encontro el archivo " + ARCHIVO_CONFIGURACION);
            }
            propiedades.load(entrada);
        } catch (IOException e) {
            throw new DatosException("No se pudo leer " + ARCHIVO_CONFIGURACION, e);
        }
        cargarDriver(propiedades.getProperty("db.driver", DRIVER_POR_DEFECTO));
        url = propiedades.getProperty("db.url");
        usuario = propiedades.getProperty("db.usuario");
        contrasena = propiedades.getProperty("db.contrasena");
    }

    /**
     * Registra el driver JDBC. En una aplicacion web es necesario cargarlo
     * explicitamente porque el servidor usa su propio cargador de clases.
     */
    private static void cargarDriver(String nombreDriver) {
        try {
            Class.forName(nombreDriver);
        } catch (ClassNotFoundException e) {
            throw new DatosException("No se encontro el driver JDBC " + nombreDriver, e);
        }
    }
}

package co.dohelp.anuncio;

import java.math.BigDecimal;
import java.time.LocalDate;

/**
 * Anuncio de un servicio publicado por un trabajador en DOHELP.
 * Se guarda en la tabla servicio de la base de datos.
 */
public class Anuncio {

    public static final int MAX_LONGITUD_TITULO = 100;

    private Integer idAnuncio;
    private String titulo;
    private String descripcion;
    private String tipoPrecio = "cotizacion";
    private BigDecimal precio;
    private String modalidad = "presencial";
    private LocalDate fechaPublicacion;
    private String estado = "pendiente";
    private Integer idUsuario;
    private Integer idCategoria;
    private Integer idSubcategoria;
    private Integer idCiudad;

    // Datos de solo lectura que se obtienen con JOIN para mostrar en pantalla
    private String nombreAnunciante;
    private String nombreCategoria;
    private String nombreCiudad;

    public boolean esActivo() {
        return "activo".equals(estado);
    }

    /**
     * Devuelve el precio en texto, por ejemplo "Desde $70.000" o "A cotizar".
     */
    public String obtenerPrecioTexto() {
        if (precio == null || "cotizacion".equals(tipoPrecio)) {
            return "A cotizar";
        }
        String valor = String.format("$%,.0f", precio).replace(',', '.');
        return "desde".equals(tipoPrecio) ? "Desde " + valor : valor;
    }

    @Override
    public String toString() {
        return String.format("#%d | %s | %s | %s | %s | %s | %s",
                idAnuncio, titulo, nombreCategoria, nombreCiudad,
                obtenerPrecioTexto(), modalidad, estado);
    }

    // Metodos get y set

    public Integer getIdAnuncio() {
        return idAnuncio;
    }

    public void setIdAnuncio(Integer idAnuncio) {
        this.idAnuncio = idAnuncio;
    }

    public String getTitulo() {
        return titulo;
    }

    public void setTitulo(String titulo) {
        this.titulo = titulo;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    public String getTipoPrecio() {
        return tipoPrecio;
    }

    public void setTipoPrecio(String tipoPrecio) {
        this.tipoPrecio = tipoPrecio;
    }

    public BigDecimal getPrecio() {
        return precio;
    }

    public void setPrecio(BigDecimal precio) {
        this.precio = precio;
    }

    public String getModalidad() {
        return modalidad;
    }

    public void setModalidad(String modalidad) {
        this.modalidad = modalidad;
    }

    public LocalDate getFechaPublicacion() {
        return fechaPublicacion;
    }

    public void setFechaPublicacion(LocalDate fechaPublicacion) {
        this.fechaPublicacion = fechaPublicacion;
    }

    public String getEstado() {
        return estado;
    }

    public void setEstado(String estado) {
        this.estado = estado;
    }

    public Integer getIdUsuario() {
        return idUsuario;
    }

    public void setIdUsuario(Integer idUsuario) {
        this.idUsuario = idUsuario;
    }

    public Integer getIdCategoria() {
        return idCategoria;
    }

    public void setIdCategoria(Integer idCategoria) {
        this.idCategoria = idCategoria;
    }

    public Integer getIdSubcategoria() {
        return idSubcategoria;
    }

    public void setIdSubcategoria(Integer idSubcategoria) {
        this.idSubcategoria = idSubcategoria;
    }

    public Integer getIdCiudad() {
        return idCiudad;
    }

    public void setIdCiudad(Integer idCiudad) {
        this.idCiudad = idCiudad;
    }

    public String getNombreAnunciante() {
        return nombreAnunciante;
    }

    public void setNombreAnunciante(String nombreAnunciante) {
        this.nombreAnunciante = nombreAnunciante;
    }

    public String getNombreCategoria() {
        return nombreCategoria;
    }

    public void setNombreCategoria(String nombreCategoria) {
        this.nombreCategoria = nombreCategoria;
    }

    public String getNombreCiudad() {
        return nombreCiudad;
    }

    public void setNombreCiudad(String nombreCiudad) {
        this.nombreCiudad = nombreCiudad;
    }
}

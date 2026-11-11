package co.dohelp.consola;

import co.dohelp.anuncio.Anuncio;
import co.dohelp.anuncio.AnuncioService;
import co.dohelp.catalogo.CatalogoDao;
import co.dohelp.catalogo.Opcion;
import co.dohelp.excepcion.DatosException;

import java.math.BigDecimal;
import java.util.List;
import java.util.Scanner;
import java.util.Set;
import java.util.TreeSet;

/**
 * Menu de consola del modulo de gestion de anuncios.
 * Permite insertar, consultar, actualizar y eliminar anuncios.
 */
public class MenuAnuncios {

    private static final int OPCION_SALIR = 0;

    private final AnuncioService anuncioService;
    private final CatalogoDao catalogoDao;
    private final Scanner lector;

    public MenuAnuncios(AnuncioService anuncioService, CatalogoDao catalogoDao, Scanner lector) {
        this.anuncioService = anuncioService;
        this.catalogoDao = catalogoDao;
        this.lector = lector;
    }

    /** Muestra el menu hasta que el usuario elija salir. */
    public void iniciar() {
        int opcion;
        do {
            mostrarMenu();
            opcion = leerEntero("Elija una opcion: ");
            try {
                ejecutarOpcion(opcion);
            } catch (DatosException e) {
                System.out.println("\n[!] " + e.getMessage());
            }
        } while (opcion != OPCION_SALIR);
    }

    private void mostrarMenu() {
        System.out.println();
        System.out.println("==============================================");
        System.out.println("   DOHELP - Modulo de gestion de anuncios");
        System.out.println("==============================================");
        System.out.println("1. Listar todos los anuncios");
        System.out.println("2. Buscar anuncios activos por ciudad");
        System.out.println("3. Consultar un anuncio por id");
        System.out.println("4. Publicar un anuncio nuevo");
        System.out.println("5. Actualizar un anuncio");
        System.out.println("6. Eliminar un anuncio");
        System.out.println("0. Salir");
    }

    private void ejecutarOpcion(int opcion) {
        switch (opcion) {
            case 1 -> listarAnuncios();
            case 2 -> buscarPorCiudad();
            case 3 -> consultarAnuncio();
            case 4 -> publicarAnuncio();
            case 5 -> actualizarAnuncio();
            case 6 -> eliminarAnuncio();
            case OPCION_SALIR -> System.out.println("\nHasta pronto.");
            default -> System.out.println("\nOpcion no valida.");
        }
    }

    // ---------- Consulta ----------

    private void listarAnuncios() {
        mostrarLista("Todos los anuncios", anuncioService.listarAnuncios());
    }

    private void buscarPorCiudad() {
        Opcion ciudad = elegirOpcion("Ciudades", catalogoDao.listarCiudades());
        mostrarLista("Anuncios activos en " + ciudad.nombre(),
                anuncioService.buscarPorCiudad(ciudad.id()));
    }

    private void consultarAnuncio() {
        int idAnuncio = leerEntero("Id del anuncio: ");
        mostrarDetalle(anuncioService.consultarAnuncio(idAnuncio));
    }

    // ---------- Insercion ----------

    private void publicarAnuncio() {
        System.out.println("\n--- Publicar un anuncio nuevo ---");
        Anuncio anuncio = new Anuncio();
        anuncio.setIdUsuario(elegirOpcion("Trabajador que publica", catalogoDao.listarTrabajadores()).id());
        anuncio.setTitulo(leerTexto("Titulo: "));
        anuncio.setDescripcion(leerTexto("Descripcion: "));
        Opcion categoria = elegirOpcion("Categorias", catalogoDao.listarCategorias());
        anuncio.setIdCategoria(categoria.id());
        anuncio.setIdSubcategoria(elegirOpcion("Subcategorias",
                catalogoDao.listarSubcategorias(categoria.id())).id());
        anuncio.setIdCiudad(elegirOpcion("Ciudades", catalogoDao.listarCiudades()).id());
        anuncio.setModalidad(elegirValor("Modalidad", AnuncioService.MODALIDADES));
        anuncio.setTipoPrecio(elegirValor("Tipo de precio", AnuncioService.TIPOS_PRECIO));
        if (!"cotizacion".equals(anuncio.getTipoPrecio())) {
            anuncio.setPrecio(leerPrecio("Precio en pesos (solo numeros): "));
        }

        int idNuevo = anuncioService.publicarAnuncio(anuncio);
        System.out.println("\n[OK] Anuncio publicado con el id " + idNuevo
                + ". Queda en estado pendiente hasta que un administrador lo apruebe.");
    }

    // ---------- Actualizacion ----------

    private void actualizarAnuncio() {
        int idAnuncio = leerEntero("Id del anuncio a actualizar: ");
        Anuncio anuncio = anuncioService.consultarAnuncio(idAnuncio);
        mostrarDetalle(anuncio);
        System.out.println("\nEscriba el nuevo valor o presione Enter para dejar el actual.");

        anuncio.setTitulo(leerTextoOpcional("Titulo", anuncio.getTitulo()));
        anuncio.setDescripcion(leerTextoOpcional("Descripcion", anuncio.getDescripcion()));
        anuncio.setModalidad(leerTextoOpcional("Modalidad (presencial/domicilio/virtual)",
                anuncio.getModalidad()));
        anuncio.setTipoPrecio(leerTextoOpcional("Tipo de precio (fijo/desde/cotizacion)",
                anuncio.getTipoPrecio()));
        if (!"cotizacion".equals(anuncio.getTipoPrecio())) {
            String precioActual = anuncio.getPrecio() == null ? "" : anuncio.getPrecio().toPlainString();
            String precioNuevo = leerTextoOpcional("Precio", precioActual);
            anuncio.setPrecio(convertirPrecio(precioNuevo));
        }
        anuncio.setEstado(leerTextoOpcional("Estado (pendiente/activo/pausado/rechazado)",
                anuncio.getEstado()));

        anuncioService.actualizarAnuncio(anuncio);
        System.out.println("\n[OK] Anuncio " + idAnuncio + " actualizado.");
        mostrarDetalle(anuncioService.consultarAnuncio(idAnuncio));
    }

    // ---------- Eliminacion ----------

    private void eliminarAnuncio() {
        int idAnuncio = leerEntero("Id del anuncio a eliminar: ");
        Anuncio anuncio = anuncioService.consultarAnuncio(idAnuncio);
        System.out.println("\nSe eliminara: " + anuncio.getTitulo());
        String confirmacion = leerTexto("Esta seguro? (s/n): ");
        if (confirmacion.equalsIgnoreCase("s")) {
            anuncioService.eliminarAnuncio(idAnuncio);
            System.out.println("\n[OK] Anuncio " + idAnuncio + " eliminado.");
        } else {
            System.out.println("\nEliminacion cancelada.");
        }
    }

    // ---------- Metodos de apoyo para mostrar ----------

    private void mostrarLista(String titulo, List<Anuncio> anuncios) {
        System.out.println("\n--- " + titulo + " (" + anuncios.size() + ") ---");
        if (anuncios.isEmpty()) {
            System.out.println("No hay anuncios para mostrar.");
        }
        anuncios.forEach(System.out::println);
    }

    private void mostrarDetalle(Anuncio anuncio) {
        System.out.println("\n--- Anuncio #" + anuncio.getIdAnuncio() + " ---");
        System.out.println("Titulo:       " + anuncio.getTitulo());
        System.out.println("Descripcion:  " + anuncio.getDescripcion());
        System.out.println("Anunciante:   " + anuncio.getNombreAnunciante());
        System.out.println("Categoria:    " + anuncio.getNombreCategoria());
        System.out.println("Ciudad:       " + anuncio.getNombreCiudad());
        System.out.println("Modalidad:    " + anuncio.getModalidad());
        System.out.println("Precio:       " + anuncio.obtenerPrecioTexto());
        System.out.println("Estado:       " + anuncio.getEstado());
        System.out.println("Publicado:    " + anuncio.getFechaPublicacion());
    }

    // ---------- Metodos de apoyo para leer datos ----------

    private Opcion elegirOpcion(String titulo, List<Opcion> opciones) {
        if (opciones.isEmpty()) {
            throw new DatosException("No hay registros en: " + titulo);
        }
        System.out.println("\n" + titulo + ":");
        opciones.forEach(opcion -> System.out.println("  " + opcion));
        while (true) {
            int idElegido = leerEntero("Numero: ");
            for (Opcion opcion : opciones) {
                if (opcion.id() == idElegido) {
                    return opcion;
                }
            }
            System.out.println("Ese numero no esta en la lista.");
        }
    }

    private String elegirValor(String titulo, Set<String> valores) {
        List<String> lista = List.copyOf(new TreeSet<>(valores));
        System.out.println("\n" + titulo + ":");
        for (int i = 0; i < lista.size(); i++) {
            System.out.println("  " + (i + 1) + ". " + lista.get(i));
        }
        while (true) {
            int posicion = leerEntero("Numero: ");
            if (posicion >= 1 && posicion <= lista.size()) {
                return lista.get(posicion - 1);
            }
            System.out.println("Ese numero no esta en la lista.");
        }
    }

    private int leerEntero(String mensaje) {
        while (true) {
            System.out.print(mensaje);
            String texto = lector.nextLine().trim();
            try {
                return Integer.parseInt(texto);
            } catch (NumberFormatException e) {
                System.out.println("Escriba solo un numero.");
            }
        }
    }

    private String leerTexto(String mensaje) {
        String texto;
        do {
            System.out.print(mensaje);
            texto = lector.nextLine().trim();
        } while (texto.isEmpty());
        return texto;
    }

    private String leerTextoOpcional(String campo, String valorActual) {
        System.out.print(campo + " [" + valorActual + "]: ");
        String texto = lector.nextLine().trim();
        return texto.isEmpty() ? valorActual : texto;
    }

    private BigDecimal leerPrecio(String mensaje) {
        while (true) {
            BigDecimal precio = convertirPrecio(leerTexto(mensaje));
            if (precio != null) {
                return precio;
            }
            System.out.println("Escriba un valor numerico, por ejemplo 50000.");
        }
    }

    private BigDecimal convertirPrecio(String texto) {
        try {
            return new BigDecimal(texto.replace(".", "").replace(",", "").trim());
        } catch (NumberFormatException e) {
            return null;
        }
    }
}

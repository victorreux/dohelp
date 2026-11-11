package co.dohelp;

import co.dohelp.anuncio.AnuncioDaoJdbc;
import co.dohelp.anuncio.AnuncioService;
import co.dohelp.catalogo.CatalogoDao;
import co.dohelp.consola.MenuAnuncios;

import java.util.Scanner;

/**
 * Punto de inicio del modulo de gestion de anuncios de DOHELP.
 * Evidencia GA7-220501096-AA2-EV01 - Codificacion de modulos del software.
 *
 * @author Victor Manuel Ruiz Rojas
 */
public class Aplicacion {

    public static void main(String[] args) {
        AnuncioService anuncioService = new AnuncioService(new AnuncioDaoJdbc());
        CatalogoDao catalogoDao = new CatalogoDao();

        try (Scanner lector = new Scanner(System.in)) {
            new MenuAnuncios(anuncioService, catalogoDao, lector).iniciar();
        }
    }
}

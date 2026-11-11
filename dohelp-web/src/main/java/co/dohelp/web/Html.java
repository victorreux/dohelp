package co.dohelp.web;

/**
 * Utilidades para mostrar texto de forma segura en las paginas JSP.
 */
public final class Html {

    private Html() {
        // Clase de utilidad: no se crean objetos de ella
    }

    /**
     * Convierte los caracteres especiales de HTML para que el texto escrito
     * por los usuarios no se interprete como codigo (evita ataques XSS).
     *
     * @param texto texto original; puede ser nulo
     * @return texto seguro para insertar en HTML
     */
    public static String escapar(Object texto) {
        if (texto == null) {
            return "";
        }
        StringBuilder resultado = new StringBuilder();
        for (char caracter : texto.toString().toCharArray()) {
            switch (caracter) {
                case '<' -> resultado.append("&lt;");
                case '>' -> resultado.append("&gt;");
                case '&' -> resultado.append("&amp;");
                case '"' -> resultado.append("&quot;");
                case '\'' -> resultado.append("&#39;");
                default -> resultado.append(caracter);
            }
        }
        return resultado.toString();
    }

    /** Devuelve "selected" si los dos valores son iguales, para las listas desplegables. */
    public static String seleccionar(Object valor, Object valorActual) {
        return valorActual != null && String.valueOf(valor).equals(String.valueOf(valorActual))
                ? "selected" : "";
    }
}
